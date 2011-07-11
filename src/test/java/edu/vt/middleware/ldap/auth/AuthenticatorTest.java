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
package edu.vt.middleware.ldap.auth;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapConstants;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.auth.handler.CompareAuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.TestAuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.TestAuthorizationHandler;
import edu.vt.middleware.ldap.bean.LdapAttributes;
import edu.vt.middleware.ldap.bean.LdapEntry;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Authenticator}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthenticatorTest
{

  /** Invalid password test data. */
  public static final String INVALID_PASSWD = "not-a-password";

  /** Invalid filter test data. */
  public static final String INVALID_FILTER = "departmentNumber=1111";

  /** Entry created for auth tests. */
  private static LdapEntry testLdapEntry;

  /** Entry created for auth tests. */
  private static LdapEntry specialCharsLdapEntry;

  /** Ldap instance for concurrency testing. */
  private Authenticator singleTLSAuth;

  /** Ldap instance for concurrency testing. */
  private Authenticator singleSSLAuth;

  /** Ldap instance for concurrency testing. */
  private Authenticator singleTLSDnAuth;

  /** Ldap instance for concurrency testing. */
  private Authenticator singleSSLDnAuth;


  /**
   * Default constructor.
   *
   * @throws  Exception  if ldap cannot be constructed
   */
  public AuthenticatorTest()
    throws Exception
  {
    this.singleTLSAuth = TestUtil.createTLSAuthenticator();
    this.singleSSLAuth = TestUtil.createSSLAuthenticator();
    this.singleTLSDnAuth = TestUtil.createTLSDnAuthenticator();
    this.singleSSLDnAuth = TestUtil.createSSLDnAuthenticator();
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "createEntry3" })
  @BeforeClass(groups = {"authtest"})
  public void createAuthEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToEntry(ldif);

    Ldap ldap = TestUtil.createSetupLdap();
    ldap.create(
      testLdapEntry.getDn(),
      testLdapEntry.getLdapAttributes().toAttributes());
    ldap.close();
    ldap = TestUtil.createLdap();
    while (
      !ldap.compare(
          testLdapEntry.getDn(),
          new SearchFilter(testLdapEntry.getDn().split(",")[0]))) {
      Thread.sleep(100);
    }
    ldap.close();
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "createSpecialCharsEntry2" })
  @BeforeClass(groups = {"authtest"})
  public void createSpecialCharsEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    specialCharsLdapEntry = TestUtil.convertLdifToEntry(ldif);

    Ldap ldap = TestUtil.createSetupLdap();
    ldap.create(
      specialCharsLdapEntry.getDn(),
      specialCharsLdapEntry.getLdapAttributes().toAttributes());
    ldap.close();
    ldap = TestUtil.createLdap();
    while (
      !ldap.compare(
        specialCharsLdapEntry.getDn(),
          new SearchFilter(specialCharsLdapEntry.getDn().split(",ou")[0]))) {
      Thread.sleep(100);
    }
    ldap.close();
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"authtest"})
  public void deleteAuthEntry()
    throws Exception
  {
    final Ldap ldap = TestUtil.createSetupLdap();
    ldap.delete(testLdapEntry.getDn());
    ldap.delete(specialCharsLdapEntry.getDn());
    ldap.close();
  }


  /**
   * @param  createNew  whether to construct a new ldap instance.
   *
   * @return  <code>Authenticator</code>
   *
   * @throws  Exception  On ldap construction failure.
   */
  public Authenticator createTLSAuthenticator(final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtil.createTLSAuthenticator();
    }
    return singleTLSAuth;
  }


  /**
   * @param  createNew  whether to construct a new ldap instance.
   *
   * @return  <code>Authenticator</code>
   *
   * @throws  Exception  On ldap construction failure.
   */
  public Authenticator createTLSDnAuthenticator(final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtil.createTLSDnAuthenticator();
    }
    return singleTLSDnAuth;
  }


  /**
   * @param  createNew  whether to construct a new ldap instance.
   *
   * @return  <code>Authenticator</code>
   *
   * @throws  Exception  On ldap construction failure.
   */
  public Authenticator createSSLAuthenticator(final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtil.createSSLAuthenticator();
    }
    return singleSSLAuth;
  }


  /**
   * @param  createNew  whether to construct a new ldap instance.
   *
   * @return  <code>Authenticator</code>
   *
   * @throws  Exception  On ldap construction failure.
   */
  public Authenticator createSSLDnAuthenticator(final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtil.createSSLDnAuthenticator();
    }
    return singleSSLDnAuth;
  }


  /**
   * @param  ldapUrl  to check
   * @param  baseDn  to check
   */
  @Parameters({ "loadPropertiesUrl", "loadPropertiesBaseDn" })
  @Test(groups = {"authtest"})
  public void loadProperties(final String ldapUrl, final String baseDn)
  {
    final Authenticator a = new Authenticator();
    a.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.tls.properties"));
    AssertJUnit.assertEquals(ldapUrl, a.getAuthenticatorConfig().getLdapUrl());
    AssertJUnit.assertEquals(baseDn, a.getAuthenticatorConfig().getBaseDn());
  }


  /**
   * @param  uid  to get dn for.
   * @param  user  to get dn for.
   * @param  duplicateFilter  for user lookups
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "getDnUid", "getDnUser", "getDnDuplicateFilter" })
  @Test(groups = {"authtest"})
  public void getDn(
    final String uid,
    final String user,
    final String duplicateFilter)
    throws Exception
  {
    final Authenticator ldap = this.createTLSAuthenticator(true);

    // test input
    AssertJUnit.assertNull(ldap.getDn(null));
    AssertJUnit.assertNull(ldap.getDn(""));

    // test empty user field
    final String[] userField = ldap.getAuthenticatorConfig().getUserField();
    ldap.getAuthenticatorConfig().setUserField(new String[] {});
    AssertJUnit.assertNull(ldap.getDn(user));
    ldap.getAuthenticatorConfig().setUserField(userField);

    // test construct dn
    ldap.getAuthenticatorConfig().setConstructDn(true);
    AssertJUnit.assertEquals(ldap.getDn(uid), testLdapEntry.getDn());
    ldap.getAuthenticatorConfig().setConstructDn(false);

    // test subtree searching
    ldap.getAuthenticatorConfig().setSubtreeSearch(true);

    final String baseDn = ldap.getAuthenticatorConfig().getBaseDn();
    ldap.getAuthenticatorConfig().setBaseDn(
      baseDn.substring(baseDn.indexOf(",") + 1));
    AssertJUnit.assertEquals(ldap.getDn(user), testLdapEntry.getDn());
    ldap.getAuthenticatorConfig().setBaseDn(baseDn);
    ldap.getAuthenticatorConfig().setSubtreeSearch(false);

    // test one level searching
    AssertJUnit.assertEquals(ldap.getDn(user), testLdapEntry.getDn());

    // test duplicate DNs
    ldap.getAuthenticatorConfig().setUserFilter(duplicateFilter);
    try {
      ldap.getDn(user);
      AssertJUnit.fail("Should have thrown NamingException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), NamingException.class);
    }

    ldap.getAuthenticatorConfig().setAllowMultipleDns(true);
    ldap.getDn(user);

    ldap.close();
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateDn",
      "authenticateDnCredential",
      "authenticateDnReturnAttrs",
      "authenticateDnResults"
    }
  )
  @Test(
    groups = {"authtest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void authenticateDn(
    final String dn,
    final String credential,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    // test plain auth
    final Authenticator ldap = this.createTLSDnAuthenticator(false);
    AssertJUnit.assertFalse(ldap.authenticate(dn, INVALID_PASSWD));
    AssertJUnit.assertTrue(ldap.authenticate(dn, credential));

    // test auth with return attributes
    final Attributes attrs = ldap.authenticate(
      dn,
      credential,
      returnAttrs.split("\\|"));
    final LdapAttributes expected = TestUtil.convertStringToAttributes(results);
    AssertJUnit.assertEquals(expected, TestUtil.newLdapAttributes(attrs));
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateDn",
      "authenticateDnCredential",
      "authenticateDnReturnAttrs",
      "authenticateDnResults"
    }
  )
  @Test(
    groups = {"authtest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void authenticateDnSsl(
    final String dn,
    final String credential,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    // test plain auth
    final Authenticator ldap = this.createSSLDnAuthenticator(false);
    AssertJUnit.assertFalse(ldap.authenticate(dn, INVALID_PASSWD));
    AssertJUnit.assertTrue(ldap.authenticate(dn, credential));

    // test auth with return attributes
    final Attributes attrs = ldap.authenticate(
      dn,
      credential,
      returnAttrs.split("\\|"));
    final LdapAttributes expected = TestUtil.convertStringToAttributes(results);
    AssertJUnit.assertEquals(expected, TestUtil.newLdapAttributes(attrs));
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  filterArgs  to authorize with.
   * @param  returnAttrs  to search for.
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateDn",
      "authenticateDnCredential",
      "authenticateDnFilter",
      "authenticateDnFilterArgs",
      "authenticateDnReturnAttrs",
      "authenticateDnResults"
    }
  )
  @Test(
    groups = {"authtest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void authenticateDnAndAuthorize(
    final String dn,
    final String credential,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Authenticator ldap = this.createTLSDnAuthenticator(false);

    // test plain auth
    AssertJUnit.assertFalse(
      ldap.authenticate(dn, INVALID_PASSWD, new SearchFilter(filter)));
    AssertJUnit.assertFalse(
      ldap.authenticate(dn, credential, new SearchFilter(INVALID_FILTER)));
    AssertJUnit.assertTrue(
      ldap.authenticate(
        dn,
        credential,
        new SearchFilter(filter, filterArgs.split("\\|"))));

    // test auth with return attributes
    final Attributes attrs = ldap.authenticate(
      dn,
      credential,
      new SearchFilter(filter, filterArgs.split("\\|")),
      returnAttrs.split("\\|"));
    final LdapAttributes expected = TestUtil.convertStringToAttributes(results);
    AssertJUnit.assertEquals(expected, TestUtil.newLdapAttributes(attrs));
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  filterArgs  to authorize with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateDn",
      "authenticateDnCredential",
      "authenticateDnFilter",
      "authenticateDnFilterArgs"
    }
  )
  @Test(groups = {"authtest"})
  public void authenticateDnHandler(
    final String dn,
    final String credential,
    final String filter,
    final String filterArgs)
    throws Exception
  {
    // test authenticator handler
    final Authenticator ldap = this.createTLSDnAuthenticator(true);
    final TestAuthenticationResultHandler authHandler =
      new TestAuthenticationResultHandler();
    ldap.getAuthenticatorConfig().setAuthenticationResultHandlers(
      new AuthenticationResultHandler[] {authHandler});

    final TestAuthorizationHandler authzHandler =
      new TestAuthorizationHandler();
    ldap.getAuthenticatorConfig().setAuthorizationHandlers(
      new AuthorizationHandler[] {authzHandler});

    AssertJUnit.assertFalse(ldap.authenticate(dn, INVALID_PASSWD));
    AssertJUnit.assertFalse(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertFalse(!authzHandler.getResults().isEmpty());

    AssertJUnit.assertFalse(ldap.authenticate(dn, credential));
    AssertJUnit.assertFalse(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertFalse(!authzHandler.getResults().isEmpty());

    authzHandler.setSucceed(true);

    AssertJUnit.assertTrue(ldap.authenticate(dn, credential));
    AssertJUnit.assertTrue(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertTrue(authzHandler.getResults().get(0).equals(dn));

    authHandler.getResults().clear();
    authzHandler.getResults().clear();

    AssertJUnit.assertTrue(
      ldap.authenticate(
        dn,
        credential,
        new SearchFilter(filter, filterArgs.split("\\|"))));
    AssertJUnit.assertTrue(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertTrue(authzHandler.getResults().get(0).equals(dn));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "digestMd5User", "digestMd5Credential" })
  @Test(groups = {"authtest"})
  public void authenticateDigestMd5(final String user, final String credential)
    throws Exception
  {
    final Authenticator ldap = TestUtil.createDigestMD5Authenticator();
    AssertJUnit.assertFalse(ldap.authenticate(user, INVALID_PASSWD));
    AssertJUnit.assertTrue(ldap.authenticate(user, credential));
    ldap.close();
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "cramMd5User", "cramMd5Credential" })
  @Test(groups = {"authtest"})
  public void authenticateCramMd5(final String user, final String credential)
    throws Exception
  {
    final Authenticator ldap = TestUtil.createCramMD5Authenticator();
    AssertJUnit.assertFalse(ldap.authenticate(user, INVALID_PASSWD));
    AssertJUnit.assertTrue(ldap.authenticate(user, credential));
    ldap.close();
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateReturnAttrs",
      "authenticateResults"
    }
  )
  @Test(
    groups = {"authtest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void authenticate(
    final String user,
    final String credential,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Authenticator ldap = this.createTLSAuthenticator(false);

    // test plain auth
    AssertJUnit.assertFalse(ldap.authenticate(user, INVALID_PASSWD));
    AssertJUnit.assertTrue(ldap.authenticate(user, credential));

    // test auth with return attributes
    final Attributes attrs = ldap.authenticate(
      user,
      credential,
      returnAttrs.split("\\|"));
    final LdapAttributes expected = TestUtil.convertStringToAttributes(results);
    AssertJUnit.assertEquals(expected, TestUtil.newLdapAttributes(attrs));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateReturnAttrs",
      "authenticateResults"
    }
  )
  @Test(
    groups = {"authtest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void authenticateSsl(
    final String user,
    final String credential,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Authenticator ldap = this.createSSLAuthenticator(false);

    // test plain auth
    AssertJUnit.assertFalse(ldap.authenticate(user, INVALID_PASSWD));
    AssertJUnit.assertTrue(ldap.authenticate(user, credential));

    // test auth with return attributes
    final Attributes attrs = ldap.authenticate(
      user,
      credential,
      returnAttrs.split("\\|"));
    final LdapAttributes expected = TestUtil.convertStringToAttributes(results);
    AssertJUnit.assertEquals(expected, TestUtil.newLdapAttributes(attrs));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  returnAttrs  to search for.
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateFilter",
      "authenticateReturnAttrs",
      "authenticateResults"
    }
  )
  @Test(
    groups = {"authtest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void authenticateAndAuthorize(
    final String user,
    final String credential,
    final String filter,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Authenticator ldap = this.createTLSAuthenticator(false);

    // test plain auth
    AssertJUnit.assertFalse(
      ldap.authenticate(user, INVALID_PASSWD, new SearchFilter(filter)));
    AssertJUnit.assertFalse(
      ldap.authenticate(user, credential, new SearchFilter(INVALID_FILTER)));
    AssertJUnit.assertTrue(
      ldap.authenticate(user, credential, new SearchFilter(filter)));

    // test auth with return attributes
    final Attributes attrs = ldap.authenticate(
      user,
      credential,
      new SearchFilter(filter),
      returnAttrs.split("\\|"));
    final LdapAttributes expected = TestUtil.convertStringToAttributes(results);
    AssertJUnit.assertEquals(expected, TestUtil.newLdapAttributes(attrs));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  returnAttrs  to search for.
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateFilter",
      "authenticateReturnAttrs",
      "authenticateResults"
    }
  )
  @Test(
    groups = {"authtest"},
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void authenticateAndAuthorizeCompare(
    final String user,
    final String credential,
    final String filter,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Authenticator ldap = this.createTLSAuthenticator(true);
    ldap.getAuthenticatorConfig().setAuthenticationHandler(
      new CompareAuthenticationHandler());

    // test plain auth
    AssertJUnit.assertFalse(
      ldap.authenticate(user, INVALID_PASSWD, new SearchFilter(filter)));
    AssertJUnit.assertFalse(
      ldap.authenticate(user, credential, new SearchFilter(INVALID_FILTER)));
    AssertJUnit.assertTrue(
      ldap.authenticate(user, credential, new SearchFilter(filter)));

    // test auth with return attributes
    final Attributes attrs = ldap.authenticate(
      user,
      credential,
      new SearchFilter(filter),
      returnAttrs.split("\\|"));
    final LdapAttributes expected = TestUtil.convertStringToAttributes(results);
    AssertJUnit.assertEquals(expected, TestUtil.newLdapAttributes(attrs));

    ldap.close();
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential",
      "authenticateReturnAttrs"
    }
  )
  @Test(groups = {"authtest"})
  public void authenticateExceptions(
    final String user,
    final String credential,
    final String returnAttrs)
    throws Exception
  {
    final Authenticator ldap = this.createTLSAuthenticator(true);

    try {
      ldap.authenticate(user, new Object(), returnAttrs.split("\\|"));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), AuthenticationException.class);
    }

    try {
      ldap.authenticate(null, credential, returnAttrs.split("\\|"));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), AuthenticationException.class);
    }

    try {
      ldap.authenticate("", credential, returnAttrs.split("\\|"));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), AuthenticationException.class);
    }

    // must do this test after the search connection has been setup or
    // the anon auth will block the search
    ldap.getAuthenticatorConfig().setAuthtype(LdapConstants.NONE_AUTHTYPE);
    try {
      ldap.authenticate(user, credential, returnAttrs.split("\\|"));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), AuthenticationException.class);
    }

    ldap.getAuthenticatorConfig().setAuthtype(LdapConstants.SIMPLE_AUTHTYPE);
    try {
      ldap.authenticate(
        user,
        credential,
        new SearchFilter(INVALID_FILTER),
        returnAttrs.split("\\|"));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(e.getClass(), AuthorizationException.class);
    }

    ldap.close();
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateSpecialCharsUser",
      "authenticateSpecialCharsCredential"
    }
  )
  @Test(groups = {"authtest"})
  public void authenticateSpecialChars(
    final String user, final String credential)
    throws Exception
  {
    final Authenticator ldap = this.createTLSAuthenticator(true);

    // test without rewrite
    AssertJUnit.assertFalse(ldap.authenticate(user, INVALID_PASSWD));
    AssertJUnit.assertTrue(ldap.authenticate(user, credential));

    // test with rewrite
    ldap.getAuthenticatorConfig().setBaseDn("dc=blah");
    ldap.getAuthenticatorConfig().setSubtreeSearch(true);
    AssertJUnit.assertFalse(ldap.authenticate(user, INVALID_PASSWD));
    AssertJUnit.assertTrue(ldap.authenticate(user, credential));

    ldap.close();
  }
}
