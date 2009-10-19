/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.dsml.Dsmlv1;
import edu.vt.middleware.ldap.handler.AttributeHandler;
import edu.vt.middleware.ldap.handler.BinaryAttributeHandler;
import edu.vt.middleware.ldap.handler.EntryDnSearchResultHandler;
import edu.vt.middleware.ldap.handler.FqdnSearchResultHandler;
import edu.vt.middleware.ldap.handler.SearchResultHandler;
import edu.vt.middleware.ldap.ldif.Ldif;
import edu.vt.middleware.ldap.pool.BlockingLdapPool;
import edu.vt.middleware.ldap.pool.BlockingTimeoutException;
import edu.vt.middleware.ldap.pool.CloseLdapPassivator;
import edu.vt.middleware.ldap.pool.CompareLdapValidator;
import edu.vt.middleware.ldap.pool.ConnectLdapActivator;
import edu.vt.middleware.ldap.pool.DefaultLdapFactory;
import edu.vt.middleware.ldap.pool.LdapActivationException;
import edu.vt.middleware.ldap.pool.LdapPoolConfig;
import edu.vt.middleware.ldap.pool.LdapPoolException;
import edu.vt.middleware.ldap.pool.LdapValidationException;
import edu.vt.middleware.ldap.pool.PoolInterruptedException;
import edu.vt.middleware.ldap.pool.SharedLdapPool;
import edu.vt.middleware.ldap.pool.SoftLimitLdapPool;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for wiki sample code.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class WikiCodeTest
{


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleCompare()
    throws Exception
  {
    final Ldap ldap = new Ldap(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    if (ldap.compare("uid=818037,ou=People,dc=vt,dc=edu",
                     new SearchFilter("mail=dfisher@vt.edu"))) {
      System.out.println("Compare succeeded");
    } else {
      System.out.println("Compare failed");
    }
    ldap.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleSubtreeSearch()
    throws Exception
  {
    final Ldap ldap = new Ldap(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    (new Ldif()).outputLdif(
      ldap.search(new SearchFilter("sn=Fisher")),
       new BufferedWriter(new OutputStreamWriter(System.out)));
    ldap.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleAttributeSearch()
    throws Exception
  {
    final Ldap ldap = new Ldap(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    (new Dsmlv1()).outputDsml(
      ldap.searchAttributes(
        AttributesFactory.createAttributes("mail", "dfisher@vt.edu"),
        new String[]{"sn", "givenName"}),
      new BufferedWriter(new OutputStreamWriter(System.out)));
    ldap.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleAuthentication()
    throws Exception
  {
    final AuthenticatorConfig config = new AuthenticatorConfig(
      "ldap://authn.directory.vt.edu", "ou=People,dc=vt,dc=edu");
    config.setTls(true);
    // attribute to search for user with
    config.setUserField(new String[]{"uid", "mail"});
    final Authenticator auth = new Authenticator(config);
    auth.useTls(true);
    if (auth.authenticate("user", "credential")) {
      System.out.println("Authentication succeeded");
    } else {
      System.out.println("Authentication failed");
    }
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleAuthorization()
    throws Exception
  {
    final AuthenticatorConfig config = new AuthenticatorConfig(
      "ldap://authn.directory.vt.edu", "ou=People,dc=vt,dc=edu");
    config.setTls(true);
    // attribute to search for user with
    config.setUserField(new String[]{"uid", "mail"});
    final Authenticator auth = new Authenticator(config);
    auth.useTls(true);
    if (auth.authenticate(
        "user", "credential", new SearchFilter("eduPersonAffiliation=staff"))) {
      System.out.println("Authentication/Authorization succeeded");
    } else {
      System.out.println("Authentication/Authorization failed");
    }
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void samplePooling()
    throws Exception
  {
    final DefaultLdapFactory factory = new DefaultLdapFactory(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    final SoftLimitLdapPool pool = new SoftLimitLdapPool(factory);
    pool.initialize();
    Ldap ldap = null;
    try {
      ldap = pool.checkOut();

      final Iterator<SearchResult> i = ldap.search(
        new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});

      AssertJUnit.assertTrue(i.hasNext());

    } catch (LdapPoolException e) {
      e.printStackTrace();
    } finally {
      pool.checkIn(ldap);
    }

    pool.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleNoHandler()
    throws Exception
  {
    final Ldap ldap = new Ldap(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    final Iterator<SearchResult> iter = ldap.search(
      "ou=People,dc=vt,dc=edu",
      new SearchFilter("sn=Fisher"),
      new String[]{"givenName", "mail"},
      (SearchResultHandler[]) null);

    AssertJUnit.assertTrue(iter.hasNext());
    ldap.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleBinaryAttributeHandler()
    throws Exception
  {
    final Ldap ldap = new Ldap(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    final Attributes attrs = ldap.getAttributes(
      "uid=818037,ou=People,dc=vt,dc=edu", null, new BinaryAttributeHandler());

    AssertJUnit.assertTrue(attrs.size() > 0);
    ldap.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleSearchBinaryAttributeHandler()
    throws Exception
  {
    final Ldap ldap = new Ldap(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    final FqdnSearchResultHandler handler = new FqdnSearchResultHandler();
    handler.setAttributeHandler(
      new AttributeHandler[]{new BinaryAttributeHandler()});
    final Iterator<SearchResult> iter = ldap.search(
      "ou=People,dc=vt,dc=edu", "sn=Fisher", null, null, handler);

    AssertJUnit.assertTrue(iter.hasNext());
    ldap.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleEntryDnHandler()
    throws Exception
  {
    final LdapConfig config = new LdapConfig(
      "ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu");
    config.setSearchResultHandlers(
      new SearchResultHandler[]{
        new FqdnSearchResultHandler(), new EntryDnSearchResultHandler(), });
    final Ldap ldap = new Ldap(config);
    final Iterator<SearchResult> iter = ldap.search(
      new SearchFilter("sn=Fisher"));

    AssertJUnit.assertTrue(iter.hasNext());
    ldap.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleBlockingLdapPool()
    throws Exception
  {
    final DefaultLdapFactory factory = new DefaultLdapFactory(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    final BlockingLdapPool pool = new BlockingLdapPool(factory);
    // wait for 5sec for an object to be available
    pool.setBlockWaitTime(5000);
    pool.initialize();
    Ldap ldap = null;
    try {
      ldap = pool.checkOut();

      final Iterator<SearchResult> i = ldap.search(
        new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});

      AssertJUnit.assertTrue(i.hasNext());

    } catch (BlockingTimeoutException e) {
      e.printStackTrace();
    } catch (PoolInterruptedException e) {
      e.printStackTrace();
    } catch (LdapPoolException e) {
      e.printStackTrace();
    } finally {
      pool.checkIn(ldap);
    }

    pool.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleSoftLimitLdapPool()
    throws Exception
  {
    final DefaultLdapFactory factory = new DefaultLdapFactory(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    final SoftLimitLdapPool pool = new SoftLimitLdapPool(factory);
    // wait for 5sec for an object to be available
    pool.setBlockWaitTime(5000);
    pool.initialize();
    Ldap ldap = null;
    try {
      ldap = pool.checkOut();

      final Iterator<SearchResult> i = ldap.search(
        new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});

      AssertJUnit.assertTrue(i.hasNext());

    } catch (BlockingTimeoutException e) {
      e.printStackTrace();
    } catch (PoolInterruptedException e) {
      e.printStackTrace();
    } catch (LdapPoolException e) {
      e.printStackTrace();
    } finally {
      pool.checkIn(ldap);
    }

    pool.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleSharedLdapPool()
    throws Exception
  {
    final DefaultLdapFactory factory = new DefaultLdapFactory(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    final SharedLdapPool pool = new SharedLdapPool(factory);
    pool.initialize();
    Ldap ldap = null;
    try {
      ldap = pool.checkOut();

      final Iterator<SearchResult> i = ldap.search(
        new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});

      AssertJUnit.assertTrue(i.hasNext());

    } catch (LdapPoolException e) {
      e.printStackTrace();
    } finally {
      pool.checkIn(ldap);
    }

    pool.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleActivatePassivatePool()
    throws Exception
  {
    final DefaultLdapFactory factory = new DefaultLdapFactory(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    factory.setConnectOnCreate(false);
    factory.setLdapActivator(new ConnectLdapActivator());
    factory.setLdapPassivator(new CloseLdapPassivator());
    final SoftLimitLdapPool pool = new SoftLimitLdapPool(factory);
    // wait for 5sec for an object to be available
    pool.setBlockWaitTime(5000);
    pool.initialize();
    Ldap ldap = null;
    try {
      ldap = pool.checkOut();

      final Iterator<SearchResult> i = ldap.search(
        new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});

      AssertJUnit.assertTrue(i.hasNext());

    } catch (LdapActivationException e) {
      e.printStackTrace();
    } catch (BlockingTimeoutException e) {
      e.printStackTrace();
    } catch (PoolInterruptedException e) {
      e.printStackTrace();
    } catch (LdapPoolException e) {
      e.printStackTrace();
    } finally {
      pool.checkIn(ldap);
    }

    pool.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void sampleValidatePool()
    throws Exception
  {
    final LdapPoolConfig config = new LdapPoolConfig();
    config.setValidateOnCheckOut(true);
    // perform a simple compare
    final DefaultLdapFactory factory = new DefaultLdapFactory(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    factory.setLdapValidator(
      new CompareLdapValidator(
        "ou=People,dc=vt,dc=edu", new SearchFilter("ou=People")));
    final SoftLimitLdapPool pool = new SoftLimitLdapPool(config, factory);
    // wait for 5sec for an object to be available
    pool.setBlockWaitTime(5000);
    pool.initialize();
    Ldap ldap = null;
    try {
      ldap = pool.checkOut();

      final Iterator<SearchResult> i = ldap.search(
        new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});

      AssertJUnit.assertTrue(i.hasNext());

    } catch (LdapValidationException e) {
      e.printStackTrace();
    } catch (BlockingTimeoutException e) {
      e.printStackTrace();
    } catch (PoolInterruptedException e) {
      e.printStackTrace();
    } catch (LdapPoolException e) {
      e.printStackTrace();
    } finally {
      pool.checkIn(ldap);
    }

    pool.close();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"wikitest"})
  public void samplePeriodicValidatePool()
    throws Exception
  {
    final LdapPoolConfig config = new LdapPoolConfig();
    // by default validate the pool every 30 min, if idle
    config.setValidatePeriodically(true);
    final DefaultLdapFactory factory = new DefaultLdapFactory(
      new LdapConfig("ldap://directory.vt.edu:389", "ou=People,dc=vt,dc=edu"));
    // perform a simple compare
    factory.setLdapValidator(
      new CompareLdapValidator(
        "ou=People,dc=vt,dc=edu", new SearchFilter("ou=People")));
    final SoftLimitLdapPool pool = new SoftLimitLdapPool(config, factory);
    pool.initialize();
    Ldap ldap = null;
    try {
      ldap = pool.checkOut();

      final Iterator<SearchResult> i = ldap.search(
        new SearchFilter("givenName=Daniel"), new String[]{"uid", "mail"});

      AssertJUnit.assertTrue(i.hasNext());

    } catch (LdapPoolException e) {
      e.printStackTrace();
    } finally {
      pool.checkIn(ldap);
    }

    pool.close();
  }
}
