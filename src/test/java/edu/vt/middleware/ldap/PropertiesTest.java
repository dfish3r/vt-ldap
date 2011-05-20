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
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.SearchDnResolver;
import edu.vt.middleware.ldap.handler.DnAttributeResultHandler;
import edu.vt.middleware.ldap.handler.LdapResultHandler;
import edu.vt.middleware.ldap.handler.MergeResultHandler;
import edu.vt.middleware.ldap.handler.RecursiveResultHandler;
import edu.vt.middleware.ldap.jaas.TestCallbackHandler;
import edu.vt.middleware.ldap.props.AuthenticatorPropertySource;
import edu.vt.middleware.ldap.props.ConnectionConfigPropertySource;
import edu.vt.middleware.ldap.props.SearchRequestPropertySource;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
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
  @BeforeClass(groups = {"propstest"})
  public void init()
    throws Exception
  {
    System.setProperty(
      "java.security.auth.login.config",
      "src/test/resources/ldap_jaas.config");
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"propstest"})
  public void nullProperties()
    throws Exception
  {
    final ConnectionConfigPropertySource lccSource =
      new ConnectionConfigPropertySource(
        PropertiesTest.class.getResourceAsStream("/ldap.null.properties"));
    final ConnectionConfig lcc = lccSource.get();

    AssertJUnit.assertNull(lcc.getSslSocketFactory());
    AssertJUnit.assertNull(lcc.getHostnameVerifier());

    final SearchRequestPropertySource srSource =
      new SearchRequestPropertySource(
        PropertiesTest.class.getResourceAsStream("/ldap.null.properties"));
    final SearchRequest sr = srSource.get();

    AssertJUnit.assertNull(sr.getLdapResultHandlers());
    AssertJUnit.assertNull(sr.getSearchIgnoreResultCodes());
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"propstest"})
  public void parserProperties()
    throws Exception
  {
    final ConnectionConfigPropertySource lccSource =
      new ConnectionConfigPropertySource(
        PropertiesTest.class.getResourceAsStream("/ldap.parser.properties"));
    final ConnectionConfig lcc = lccSource.get();

    AssertJUnit.assertEquals(
      "ldap://ed-dev.middleware.vt.edu:14389", lcc.getLdapUrl());
    AssertJUnit.assertEquals("uid=1,ou=test,dc=vt,dc=edu", lcc.getBindDn());
    AssertJUnit.assertEquals(
      AuthenticationType.SIMPLE, lcc.getAuthenticationType());
    AssertJUnit.assertEquals(8000, lcc.getTimeout());
    AssertJUnit.assertFalse(lcc.isTlsEnabled());
    AssertJUnit.assertEquals(1, lcc.getProviderProperties().size());
    AssertJUnit.assertEquals(
      "true", lcc.getProviderProperties().get("java.naming.authoritative"));
    AssertJUnit.assertEquals(7, lcc.getOperationRetry());
    AssertJUnit.assertEquals(2000, lcc.getOperationRetryWait());
    AssertJUnit.assertEquals(3, lcc.getOperationRetryBackoff());

    final SearchRequestPropertySource scSource =
      new SearchRequestPropertySource(
        PropertiesTest.class.getResourceAsStream("/ldap.parser.properties"));
    final SearchRequest sr = scSource.get();

    AssertJUnit.assertEquals("ou=test,dc=vt,dc=edu", sr.getBaseDn());
    AssertJUnit.assertEquals(10, sr.getBatchSize());
    AssertJUnit.assertEquals(SearchScope.OBJECT, sr.getSearchScope());
    AssertJUnit.assertEquals(5000, sr.getTimeLimit());
    AssertJUnit.assertEquals("jpegPhoto", sr.getBinaryAttributes()[0]);

    for (LdapResultHandler srh : sr.getLdapResultHandlers()) {
      if (RecursiveResultHandler.class.isInstance(srh)) {
        final RecursiveResultHandler h = (RecursiveResultHandler)
          srh;
        AssertJUnit.assertEquals("member", h.getSearchAttribute());
        AssertJUnit.assertEquals(
          Arrays.asList(new String[] {"mail", "department"}),
          Arrays.asList(h.getMergeAttributes()));
      } else if (MergeResultHandler.class.isInstance(srh)) {
        final MergeResultHandler h = (MergeResultHandler) srh;
        AssertJUnit.assertNotNull(h);
      } else if (DnAttributeResultHandler.class.isInstance(srh)) {
        final DnAttributeResultHandler h = (DnAttributeResultHandler) srh;
        AssertJUnit.assertEquals("myDN", h.getDnAttributeName());
      } else {
        throw new Exception("Unknown search result handler type " + srh);
      }
    }

    AssertJUnit.assertEquals(2, sr.getSearchIgnoreResultCodes().length);
    AssertJUnit.assertEquals(
      ResultCode.SIZE_LIMIT_EXCEEDED, sr.getSearchIgnoreResultCodes()[0]);
    AssertJUnit.assertEquals(
      ResultCode.PARTIAL_RESULTS, sr.getSearchIgnoreResultCodes()[1]);

    final AuthenticatorPropertySource aSource =
      new AuthenticatorPropertySource(
        PropertiesTest.class.getResourceAsStream("/ldap.parser.properties"));
    final Authenticator auth = aSource.get();

    final ConnectionConfig authLcc =
      ((SearchDnResolver) auth.getDnResolver()).getConnectionConfig();
    AssertJUnit.assertEquals(
      "ldap://ed-auth.middleware.vt.edu:14389", authLcc.getLdapUrl());
    AssertJUnit.assertEquals("uid=1,ou=test,dc=vt,dc=edu", authLcc.getBindDn());
    AssertJUnit.assertEquals(
      AuthenticationType.SIMPLE, authLcc.getAuthenticationType());
    AssertJUnit.assertEquals(8000, authLcc.getTimeout());
    AssertJUnit.assertTrue(authLcc.isTlsEnabled());
    AssertJUnit.assertEquals(1, authLcc.getProviderProperties().size());
    AssertJUnit.assertEquals(
      "true", authLcc.getProviderProperties().get("java.naming.authoritative"));
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"propstest"})
  public void jaasProperties()
    throws Exception
  {
    final LoginContext lc = new LoginContext(
      "vt-ldap-props", new TestCallbackHandler());
    lc.login();

    ConnectionConfig lcc = null;
    SearchRequest sr = null;
    Authenticator auth = null;
    for (Object o : lc.getSubject().getPublicCredentials()) {
      if (o instanceof Connection) {
        lcc = ((Connection) o).getConnectionConfig();
      } else if (o instanceof SearchRequest) {
        sr = (SearchRequest) o;
      } else if (o instanceof Authenticator) {
        auth = (Authenticator) o;
      } else {
        throw new Exception("Unknown public credential found: " + o);
      }
    }

    AssertJUnit.assertEquals(
      edu.vt.middleware.ldap.provider.jndi.JndiProvider.class,
      lcc.getProvider().getClass());
    AssertJUnit.assertEquals(
      "ldap://ed-dev.middleware.vt.edu:14389", lcc.getLdapUrl());
    AssertJUnit.assertEquals("uid=1,ou=test,dc=vt,dc=edu", lcc.getBindDn());
    AssertJUnit.assertEquals(
      AuthenticationType.SIMPLE, lcc.getAuthenticationType());
    AssertJUnit.assertEquals(8000, lcc.getTimeout());
    AssertJUnit.assertTrue(lcc.isTlsEnabled());
    AssertJUnit.assertEquals(1, lcc.getProviderProperties().size());
    AssertJUnit.assertEquals(
      "true", lcc.getProviderProperties().get("java.naming.authoritative"));
    AssertJUnit.assertEquals(7, lcc.getOperationRetry());
    AssertJUnit.assertEquals(2000, lcc.getOperationRetryWait());
    AssertJUnit.assertEquals(3, lcc.getOperationRetryBackoff());

    AssertJUnit.assertEquals("ou=test,dc=vt,dc=edu", sr.getBaseDn());
    AssertJUnit.assertEquals(10, sr.getBatchSize());
    AssertJUnit.assertEquals(SearchScope.OBJECT, sr.getSearchScope());
    AssertJUnit.assertEquals(5000, sr.getTimeLimit());
    AssertJUnit.assertEquals("jpegPhoto", sr.getBinaryAttributes()[0]);

    for (LdapResultHandler srh : sr.getLdapResultHandlers()) {
      if (RecursiveResultHandler.class.isInstance(srh)) {
        final RecursiveResultHandler h = (RecursiveResultHandler)
          srh;
        AssertJUnit.assertEquals("member", h.getSearchAttribute());
        AssertJUnit.assertEquals(
          Arrays.asList(new String[] {"mail", "department"}),
          Arrays.asList(h.getMergeAttributes()));
      } else if (MergeResultHandler.class.isInstance(srh)) {
        final MergeResultHandler h = (MergeResultHandler) srh;
        AssertJUnit.assertNotNull(h);
      } else if (DnAttributeResultHandler.class.isInstance(srh)) {
        final DnAttributeResultHandler h = (DnAttributeResultHandler) srh;
        AssertJUnit.assertEquals("myDN", h.getDnAttributeName());
      } else {
        throw new Exception("Unknown search result handler type " + srh);
      }
    }

    AssertJUnit.assertEquals(2, sr.getSearchIgnoreResultCodes().length);
    AssertJUnit.assertEquals(
      ResultCode.SIZE_LIMIT_EXCEEDED, sr.getSearchIgnoreResultCodes()[0]);
    AssertJUnit.assertEquals(
      ResultCode.PARTIAL_RESULTS, sr.getSearchIgnoreResultCodes()[1]);

    final ConnectionConfig authLcc =
      ((SearchDnResolver) auth.getDnResolver()).getConnectionConfig();
    AssertJUnit.assertEquals(
      "ldap://ed-dev.middleware.vt.edu:14389", authLcc.getLdapUrl());
    AssertJUnit.assertEquals("uid=1,ou=test,dc=vt,dc=edu", authLcc.getBindDn());
    AssertJUnit.assertEquals(
      AuthenticationType.SIMPLE, authLcc.getAuthenticationType());
    AssertJUnit.assertEquals(8000, authLcc.getTimeout());
    AssertJUnit.assertTrue(authLcc.isTlsEnabled());
    AssertJUnit.assertEquals(1, authLcc.getProviderProperties().size());
    AssertJUnit.assertEquals(
      "true", authLcc.getProviderProperties().get("java.naming.authoritative"));

    AssertJUnit.assertEquals(
      edu.vt.middleware.ldap.auth.handler.CompareAuthenticationHandler.class,
      auth.getAuthenticationHandler().getClass());
    AssertJUnit.assertEquals(
      edu.vt.middleware.ldap.auth.PersistentSearchDnResolver.class,
      auth.getDnResolver().getClass());
  }
}
