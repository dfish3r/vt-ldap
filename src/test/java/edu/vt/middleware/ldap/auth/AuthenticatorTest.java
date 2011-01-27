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

import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.auth.handler.CompareAuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.TestAuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.TestAuthorizationHandler;
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
public class AuthenticatorTest extends AbstractTest
{

  /** Invalid password test data. */
  public static final String INVALID_PASSWD = "not-a-password";

  /** Invalid filter test data. */
  public static final String INVALID_FILTER = "departmentNumber=1111";

  /** Entry created for auth tests. */
  private static LdapEntry testLdapEntry;

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
  @Parameters({ "createEntry6" })
  @BeforeClass(groups = {"authtest"})
  public void createAuthEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"authtest"})
  public void deleteAuthEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  createNew  whether to construct a new authenticator.
   *
   * @return  authenticator
   *
   * @throws  Exception  On authenticator construction failure.
   */
  public Authenticator createTLSAuthenticator(
    final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtil.createTLSAuthenticator();
    }
    return singleTLSAuth;
  }


  /**
   * @param  createNew  whether to construct a new authenticator.
   *
   * @return  authenticator
   *
   * @throws  Exception  On authenticator construction failure.
   */
  public Authenticator createTLSDnAuthenticator(
    final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtil.createTLSDnAuthenticator();
    }
    return singleTLSDnAuth;
  }


  /**
   * @param  createNew  whether to construct a new authenticator.
   *
   * @return  authenticator
   *
   * @throws  Exception  On authenticator construction failure.
   */
  public Authenticator createSSLAuthenticator(
    final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtil.createSSLAuthenticator();
    }
    return singleSSLAuth;
  }


  /**
   * @param  createNew  whether to construct a new authenticator.
   *
   * @return  authenticator
   *
   * @throws  Exception  On authenticator construction failure.
   */
  public Authenticator createSSLDnAuthenticator(
    final boolean createNew)
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
    final AuthenticatorConfig ac = AuthenticatorConfig.createFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.tls.properties"));
    AssertJUnit.assertEquals(ldapUrl, ac.getLdapUrl());
    AssertJUnit.assertEquals(baseDn, ac.getBaseDn());
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
  public void resolveDn(
    final String uid,
    final String user,
    final String duplicateFilter)
    throws Exception
  {
    final Authenticator auth = this.createTLSAuthenticator(true);

    // test input
    AssertJUnit.assertNull(auth.resolveDn(null));
    AssertJUnit.assertNull(auth.resolveDn(""));

    // test construct dn
    auth.getAuthenticatorConfig().setConstructDn(true);
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(uid));
    auth.getAuthenticatorConfig().setConstructDn(false);

    // test subtree searching
    auth.getAuthenticatorConfig().setSubtreeSearch(true);
    final String baseDn = auth.getAuthenticatorConfig().getBaseDn();
    auth.getAuthenticatorConfig().setBaseDn(
      baseDn.substring(baseDn.indexOf(",") + 1));
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(user));
    auth.getAuthenticatorConfig().setBaseDn(baseDn);
    auth.getAuthenticatorConfig().setSubtreeSearch(false);

    // test one level searching
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(user));

    // test duplicate DNs
    auth.getAuthenticatorConfig().setUserFilter(duplicateFilter);
    try {
      auth.resolveDn(user);
      AssertJUnit.fail("Should have thrown LdapException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(LdapException.class, e.getClass());
    }

    auth.getAuthenticatorConfig().setAllowMultipleDns(true);
    auth.resolveDn(user);
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
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
    threadPoolSize = 1,
    invocationCount = 1,
    timeOut = 60000
  )
  public void authenticateDn(
    final String dn,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    // test plain auth
    final Authenticator auth = this.createTLSDnAuthenticator(false);
    try {
      auth.authenticate(
        new AuthenticationRequest(dn, new Credential(INVALID_PASSWD)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(dn, new Credential(credential)));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(
        dn, new Credential(credential), returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertLdifToResult(expected), new LdapResult(entry));
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
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
    final String ldifFile)
    throws Exception
  {
    // test plain auth
    final Authenticator auth = this.createSSLDnAuthenticator(false);
    try {
      auth.authenticate(
        new AuthenticationRequest(dn, new Credential(INVALID_PASSWD)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(dn, new Credential(credential)));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(
        dn, new Credential(credential), returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertLdifToResult(expected), new LdapResult(entry));
  }


  /**
   * @param  dn  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  filterArgs  to authorize with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
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
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = this.createTLSDnAuthenticator(false);

    // test plain auth
    try {
      auth.authenticate(
        new AuthenticationRequest(
          dn, new Credential(INVALID_PASSWD), new SearchFilter(filter)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    try {
      auth.authenticate(
        new AuthenticationRequest(
          dn, new Credential(credential), new SearchFilter(INVALID_FILTER)));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(
        dn,
        new Credential(credential),
        new SearchFilter(filter, filterArgs.split("\\|"))));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(
        dn,
        new Credential(credential),
        returnAttrs.split("\\|"),
        new SearchFilter(filter, filterArgs.split("\\|")))).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertLdifToResult(expected), new LdapResult(entry));
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
    final Authenticator auth = this.createTLSDnAuthenticator(true);

    final TestAuthenticationResultHandler authHandler =
      new TestAuthenticationResultHandler();
    auth.getAuthenticatorConfig().setAuthenticationResultHandlers(
      new AuthenticationResultHandler[] {authHandler});

    final TestAuthorizationHandler authzHandler =
      new TestAuthorizationHandler();
    auth.getAuthenticatorConfig().setAuthorizationHandlers(
      new AuthorizationHandler[] {authzHandler});

    try {
      auth.authenticate(
        new AuthenticationRequest(dn, new Credential(INVALID_PASSWD)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    AssertJUnit.assertTrue(!authHandler.getResults().isEmpty());
    AssertJUnit.assertFalse(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertTrue(authzHandler.getResults().isEmpty());

    try {
      auth.authenticate(
        new AuthenticationRequest(dn, new Credential(credential)));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
    AssertJUnit.assertFalse(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertFalse(!authzHandler.getResults().isEmpty());

    authzHandler.setSucceed(true);

    auth.authenticate(
      new AuthenticationRequest(dn, new Credential(credential)));
    AssertJUnit.assertTrue(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertTrue(authzHandler.getResults().get(0).equals(dn));

    authHandler.getResults().clear();
    authzHandler.getResults().clear();

    auth.authenticate(
      new AuthenticationRequest(
        dn,
        new Credential(credential),
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
    final Authenticator auth = TestUtil.createDigestMD5Authenticator();
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, new Credential(INVALID_PASSWD), new String[0]));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), new String[0]));
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
    final Authenticator auth = TestUtil.createCramMD5Authenticator();
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, new Credential(INVALID_PASSWD), new String[0]));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), new String[0]));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
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
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = this.createTLSAuthenticator(false);

    // test plain auth
    try {
      auth.authenticate(
        new AuthenticationRequest(user, new Credential(INVALID_PASSWD)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(
        AuthenticationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertLdifToResult(expected), new LdapResult(entry));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
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
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = this.createSSLAuthenticator(false);

    // test plain auth
    try {
      auth.authenticate(
        new AuthenticationRequest(user, new Credential(INVALID_PASSWD)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertLdifToResult(expected), new LdapResult(entry));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
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
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = this.createTLSAuthenticator(false);

    // test plain auth
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, new Credential(INVALID_PASSWD), new SearchFilter(filter)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, new Credential(credential), new SearchFilter(INVALID_FILTER)));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), new SearchFilter(filter)));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|"),
        new SearchFilter(filter))).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertLdifToResult(expected), new LdapResult(entry));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  returnAttrs  to search for.
   * @param  ldifFile  to expect from the search.
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
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = this.createTLSAuthenticator(true);
    auth.getAuthenticatorConfig().setAuthenticationHandler(
      new CompareAuthenticationHandler());

    // test plain auth
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, new Credential(INVALID_PASSWD), new SearchFilter(filter)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, new Credential(credential), new SearchFilter(INVALID_FILTER)));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(
        user, new Credential(credential), new SearchFilter(filter)));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|"),
        new SearchFilter(filter))).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertLdifToResult(expected), new LdapResult(entry));
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
    final Authenticator auth = this.createTLSAuthenticator(true);

    try {
      auth.authenticate(
        new AuthenticationRequest(
          user, new Credential(""), returnAttrs.split("\\|")));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }

    try {
      auth.authenticate(
        new AuthenticationRequest(
          null, new Credential(credential), returnAttrs.split("\\|")));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }

    try {
      auth.authenticate(
        new AuthenticationRequest(
          "", new Credential(credential), returnAttrs.split("\\|")));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }

    try {
      auth.authenticate(
        new AuthenticationRequest(
          user,
          new Credential(credential),
          returnAttrs.split("\\|"),
          new SearchFilter(INVALID_FILTER)));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
  }
}
