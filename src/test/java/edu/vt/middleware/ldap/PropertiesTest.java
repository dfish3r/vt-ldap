/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import java.util.Arrays;
import javax.security.auth.login.LoginContext;
import edu.vt.middleware.ldap.auth.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.AuthenticationRequest;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.PooledSearchDnResolver;
import edu.vt.middleware.ldap.auth.SearchDnResolver;
import edu.vt.middleware.ldap.control.PagedResultsControl;
import edu.vt.middleware.ldap.handler.DnAttributeEntryHandler;
import edu.vt.middleware.ldap.handler.LdapEntryHandler;
import edu.vt.middleware.ldap.handler.MergeAttributeEntryHandler;
import edu.vt.middleware.ldap.handler.RecursiveEntryHandler;
import edu.vt.middleware.ldap.jaas.RoleResolver;
import edu.vt.middleware.ldap.jaas.TestCallbackHandler;
import edu.vt.middleware.ldap.pool.BlockingConnectionPool;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;
import edu.vt.middleware.ldap.props.AuthenticatorPropertySource;
import edu.vt.middleware.ldap.props.ConnectionConfigPropertySource;
import edu.vt.middleware.ldap.props.DefaultConnectionFactoryPropertySource;
import edu.vt.middleware.ldap.props.SearchRequestPropertySource;
import edu.vt.middleware.ldap.provider.Provider;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SearchConfigProperties}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PropertiesTest
{


  /** @throws  Exception  On test failure. */
  @BeforeClass(groups = {"props"})
  public void init()
    throws Exception
  {
    System.setProperty(
      "java.security.auth.login.config",
      "target/test-classes/ldap_jaas.config");
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"props"})
  public void nullProperties()
    throws Exception
  {
    final ConnectionConfig cc = new ConnectionConfig();
    final ConnectionConfigPropertySource ccSource =
      new ConnectionConfigPropertySource(
        cc, PropertiesTest.class.getResourceAsStream(
          "/edu/vt/middleware/ldap/ldap.null.properties"));
    ccSource.initialize();

    AssertJUnit.assertNull(cc.getSslSocketFactory());
    AssertJUnit.assertNull(cc.getHostnameVerifier());

    final SearchRequest sr = new SearchRequest();
    final SearchRequestPropertySource srSource =
      new SearchRequestPropertySource(
        sr, PropertiesTest.class.getResourceAsStream(
          "/edu/vt/middleware/ldap/ldap.null.properties"));
    srSource.initialize();

    AssertJUnit.assertNull(sr.getLdapEntryHandlers());
  }


  /**
   * @param  host  that should match a property.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("ldapTestHost")
  @Test(groups = {"props"})
  public void parserProperties(final String host)
    throws Exception
  {
    final DefaultConnectionFactory cf = new DefaultConnectionFactory();
    final DefaultConnectionFactoryPropertySource cfSource =
      new DefaultConnectionFactoryPropertySource(
        cf,
        PropertiesTest.class.getResourceAsStream(
          "/edu/vt/middleware/ldap/ldap.parser.properties"));
    cfSource.initialize();

    final ConnectionConfig cc = cf.getConnectionConfig();

    AssertJUnit.assertEquals(host, cc.getLdapUrl());
    AssertJUnit.assertEquals("uid=1,ou=test,dc=vt,dc=edu", cc.getBindDn());
    AssertJUnit.assertEquals(8000, cc.getConnectTimeout());
    AssertJUnit.assertFalse(cc.isTlsEnabled());
    AssertJUnit.assertEquals(
      1, cf.getProvider().getProviderConfig().getProperties().size());
    AssertJUnit.assertEquals(
      "true",
      cf.getProvider().getProviderConfig().getProperties().get(
        "java.naming.authoritative"));
    AssertJUnit.assertEquals(7, cc.getOperationRetry());
    AssertJUnit.assertEquals(2000, cc.getOperationRetryWait());
    AssertJUnit.assertEquals(3, cc.getOperationRetryBackoff());

    final SearchRequest sr = new SearchRequest();
    final SearchRequestPropertySource srSource =
      new SearchRequestPropertySource(
        sr,
        PropertiesTest.class.getResourceAsStream(
          "/edu/vt/middleware/ldap/ldap.parser.properties"));
    srSource.initialize();

    AssertJUnit.assertEquals("ou=test,dc=vt,dc=edu", sr.getBaseDn());
    AssertJUnit.assertEquals(SearchScope.OBJECT, sr.getSearchScope());
    AssertJUnit.assertEquals(5000, sr.getTimeLimit());
    AssertJUnit.assertEquals("jpegPhoto", sr.getBinaryAttributes()[0]);
    AssertJUnit.assertEquals(
      5, ((PagedResultsControl) sr.getControls()[0]).getSize());

    for (LdapEntryHandler rh : sr.getLdapEntryHandlers()) {
      if (RecursiveEntryHandler.class.isInstance(rh)) {
        final RecursiveEntryHandler h = (RecursiveEntryHandler) rh;
        AssertJUnit.assertEquals("member", h.getSearchAttribute());
        AssertJUnit.assertEquals(
          Arrays.asList(new String[] {"mail", "department"}),
          Arrays.asList(h.getMergeAttributes()));
      } else if (MergeAttributeEntryHandler.class.isInstance(rh)) {
        final MergeAttributeEntryHandler h = (MergeAttributeEntryHandler) rh;
        AssertJUnit.assertNotNull(h);
      } else if (DnAttributeEntryHandler.class.isInstance(rh)) {
        final DnAttributeEntryHandler h = (DnAttributeEntryHandler) rh;
        AssertJUnit.assertEquals("myDN", h.getDnAttributeName());
      } else {
        throw new Exception("Unknown search result handler type " + rh);
      }
    }

    final Authenticator auth = new Authenticator();
    final AuthenticatorPropertySource aSource =
      new AuthenticatorPropertySource(
        auth,
        PropertiesTest.class.getResourceAsStream(
          "/edu/vt/middleware/ldap/ldap.parser.properties"));
    aSource.initialize();

    final SearchDnResolver dnResolver = (SearchDnResolver) auth.getDnResolver();
    final DefaultConnectionFactory authCf =
      (DefaultConnectionFactory) dnResolver.getConnectionFactory();
    final ConnectionConfig authCc = authCf.getConnectionConfig();
    AssertJUnit.assertEquals(
      "ldap://ed-auth.middleware.vt.edu:14389", authCc.getLdapUrl());
    AssertJUnit.assertEquals("uid=1,ou=test,dc=vt,dc=edu", authCc.getBindDn());
    AssertJUnit.assertEquals(8000, authCc.getConnectTimeout());
    AssertJUnit.assertTrue(authCc.isTlsEnabled());
    AssertJUnit.assertEquals(
      1, authCf.getProvider().getProviderConfig().getProperties().size());
    AssertJUnit.assertEquals(
      "true",
      authCf.getProvider().getProviderConfig().getProperties().get(
        "java.naming.authoritative"));

    if (auth.getDnResolver() instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager cfm =
        (PooledConnectionFactoryManager) auth.getDnResolver();
      cfm.getConnectionFactory().getConnectionPool().close();
    }
    final AuthenticationHandler ah = auth.getAuthenticationHandler();
    if (ah instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager cfm =
        (PooledConnectionFactoryManager) ah;
      cfm.getConnectionFactory().getConnectionPool().close();
    }
  }


  /**
   * @param  host  that should match a property.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("ldapTestHost")
  @Test(groups = {"props"})
  public void jaasProperties(final String host)
    throws Exception
  {
    final LoginContext lc = new LoginContext(
      "vt-ldap-props", new TestCallbackHandler());
    lc.login();

    Authenticator auth = null;
    AuthenticationRequest authRequest = null;
    RoleResolver roleResolver = null;
    SearchRequest searchRequest = null;
    for (Object o : lc.getSubject().getPublicCredentials()) {
      if (o instanceof Authenticator) {
        auth = (Authenticator) o;
      } else if (o instanceof AuthenticationRequest) {
        authRequest = (AuthenticationRequest) o;
      } else if (o instanceof RoleResolver) {
        roleResolver = (RoleResolver) o;
      } else if (o instanceof SearchRequest) {
        searchRequest = (SearchRequest) o;
      } else {
        throw new Exception("Unknown public credential found: " + o);
      }
    }

    final ConnectionFactoryManager cfm =
      (ConnectionFactoryManager) auth.getAuthenticationHandler();
    final DefaultConnectionFactory cf =
      (DefaultConnectionFactory) cfm.getConnectionFactory();
    final ConnectionConfig cc = cf.getConnectionConfig();

    AssertJUnit.assertNotNull(cf.getProvider().getClass());
    AssertJUnit.assertEquals(host, cc.getLdapUrl());
    AssertJUnit.assertEquals("uid=1,ou=test,dc=vt,dc=edu", cc.getBindDn());
    AssertJUnit.assertEquals(8000, cc.getConnectTimeout());
    AssertJUnit.assertTrue(cc.isTlsEnabled());
    AssertJUnit.assertEquals(
      1, cf.getProvider().getProviderConfig().getProperties().size());
    AssertJUnit.assertEquals(
      "true",
      cf.getProvider().getProviderConfig().getProperties().get(
        "java.naming.authoritative"));
    AssertJUnit.assertEquals(7, cc.getOperationRetry());
    AssertJUnit.assertEquals(2000, cc.getOperationRetryWait());
    AssertJUnit.assertEquals(3, cc.getOperationRetryBackoff());

    AssertJUnit.assertEquals("ou=test,dc=vt,dc=edu", searchRequest.getBaseDn());
    AssertJUnit.assertEquals(
      SearchScope.OBJECT, searchRequest.getSearchScope());
    AssertJUnit.assertEquals(5000, searchRequest.getTimeLimit());
    AssertJUnit.assertEquals(
      "jpegPhoto", searchRequest.getBinaryAttributes()[0]);

    for (LdapEntryHandler srh : searchRequest.getLdapEntryHandlers()) {
      if (RecursiveEntryHandler.class.isInstance(srh)) {
        final RecursiveEntryHandler h = (RecursiveEntryHandler)
          srh;
        AssertJUnit.assertEquals("member", h.getSearchAttribute());
        AssertJUnit.assertEquals(
          Arrays.asList(new String[] {"mail", "department"}),
          Arrays.asList(h.getMergeAttributes()));
      } else if (MergeAttributeEntryHandler.class.isInstance(srh)) {
        final MergeAttributeEntryHandler h = (MergeAttributeEntryHandler) srh;
        AssertJUnit.assertNotNull(h);
      } else if (DnAttributeEntryHandler.class.isInstance(srh)) {
        final DnAttributeEntryHandler h = (DnAttributeEntryHandler) srh;
        AssertJUnit.assertEquals("myDN", h.getDnAttributeName());
      } else {
        throw new Exception("Unknown search result handler type " + srh);
      }
    }

    final PooledConnectionFactory authCf =
      ((PooledSearchDnResolver) auth.getDnResolver()).getConnectionFactory();
    final BlockingConnectionPool authCp =
      (BlockingConnectionPool) authCf.getConnectionPool();
    final ConnectionConfig authCc =
      authCp.getConnectionFactory().getConnectionConfig();
    final Provider<?> authP =
      authCp.getConnectionFactory().getProvider();
    AssertJUnit.assertEquals(host, authCc.getLdapUrl());
    AssertJUnit.assertEquals("uid=1,ou=test,dc=vt,dc=edu", authCc.getBindDn());
    AssertJUnit.assertEquals(8000, authCc.getConnectTimeout());
    AssertJUnit.assertTrue(authCc.isTlsEnabled());
    AssertJUnit.assertEquals(
      1, authP.getProviderConfig().getProperties().size());
    AssertJUnit.assertEquals(
      "true",
      authP.getProviderConfig().getProperties().get(
        "java.naming.authoritative"));

    AssertJUnit.assertEquals(
      edu.vt.middleware.ldap.auth.CompareAuthenticationHandler.class,
      auth.getAuthenticationHandler().getClass());
    AssertJUnit.assertEquals(
      edu.vt.middleware.ldap.auth.PooledSearchDnResolver.class,
      auth.getDnResolver().getClass());

    if (auth.getDnResolver() instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager resolverCfm =
        (PooledConnectionFactoryManager) auth.getDnResolver();
      resolverCfm.getConnectionFactory().getConnectionPool().close();
    }
    final AuthenticationHandler authHandler = auth.getAuthenticationHandler();
    if (authHandler instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager handlerCfm =
        (PooledConnectionFactoryManager) authHandler;
      handlerCfm.getConnectionFactory().getConnectionPool().close();
    }
  }
}
