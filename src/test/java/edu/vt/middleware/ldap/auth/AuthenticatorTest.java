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

import java.util.Arrays;
import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.AttributeModification;
import edu.vt.middleware.ldap.AttributeModificationType;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.ConnectionFactoryManager;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.DefaultConnectionFactory;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.ModifyOperation;
import edu.vt.middleware.ldap.ModifyRequest;
import edu.vt.middleware.ldap.Response;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.control.PasswordPolicyControl;
import edu.vt.middleware.ldap.pool.BlockingConnectionPool;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;
import edu.vt.middleware.ldap.sasl.Mechanism;
import org.testng.AssertJUnit;
import org.testng.SkipException;
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
  public static final String INVALID_FILTER = "(departmentNumber=1111)";

  /** Entry created for auth tests. */
  private static LdapEntry testLdapEntry;

  /** Entry created for auth tests. */
  private static LdapEntry specialCharsLdapEntry;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleTLSAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleSSLAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleTLSDnAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleSSLDnAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator pooledTLSAuth;

  /**
   * Default constructor.
   *
   * @throws  Exception  if ldap cannot be constructed
   */
  public AuthenticatorTest()
    throws Exception
  {
    singleTLSAuth = TestUtil.createTLSAuthenticator();
    singleSSLAuth = TestUtil.createSSLAuthenticator();
    singleTLSDnAuth = TestUtil.createTLSDnAuthenticator();
    singleSSLDnAuth = TestUtil.createSSLDnAuthenticator();
    pooledTLSAuth = TestUtil.createTLSAuthenticator();
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry6")
  @BeforeClass(groups = {"auth"})
  public void createAuthEntry(final String ldifFile)
    throws Exception
  {
    final AuthenticationHandler ah = pooledTLSAuth.getAuthenticationHandler();
    final DefaultConnectionFactory cf =
      (DefaultConnectionFactory)
        ((ConnectionFactoryManager) ah).getConnectionFactory();
    final BlockingConnectionPool cp = new BlockingConnectionPool(cf);
    cp.initialize();
    final PooledConnectionFactory pcf = new PooledConnectionFactory(cp);
    pooledTLSAuth.setAuthenticationHandler(
      new PooledBindAuthenticationHandler(pcf));

    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createSpecialCharsEntry2")
  @BeforeClass(groups = {"auth"})
  public void createSpecialCharsEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    specialCharsLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(specialCharsLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"auth"})
  public void deleteAuthEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
    super.deleteLdapEntry(specialCharsLdapEntry.getDn());
    final AuthenticationHandler ah = pooledTLSAuth.getAuthenticationHandler();
    (((PooledConnectionFactoryManager)
      ah).getConnectionFactory().getConnectionPool()).close();
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
  @Test(groups = {"auth"})
  public void loadProperties(final String ldapUrl, final String baseDn)
  {
    final Authenticator auth = TestUtil.readAuthenticator(
      TestUtil.class.getResourceAsStream("/ldap.tls.properties"));
    final SearchDnResolver dnResolver = (SearchDnResolver) auth.getDnResolver();
    final DefaultConnectionFactory resolverCf =
      (DefaultConnectionFactory) dnResolver.getConnectionFactory();
    AssertJUnit.assertEquals(
      ldapUrl,
      resolverCf.getConnectionConfig().getLdapUrl());
    AssertJUnit.assertEquals(
      baseDn, ((SearchDnResolver) auth.getDnResolver()).getBaseDn());
  }


  /**
   * @param  uid  to get dn for.
   * @param  user  to get dn for.
   * @param  duplicateFilter  for user lookups
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "getDnUid", "getDnUser", "getDnDuplicateFilter" })
  @Test(groups = {"auth"})
  public void resolveDn(
    final String uid,
    final String user,
    final String duplicateFilter)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(true);

    // test input
    AssertJUnit.assertNull(auth.resolveDn(null));
    AssertJUnit.assertNull(auth.resolveDn(""));

    final SearchDnResolver resolver = (SearchDnResolver) auth.getDnResolver();

    // test format dn
    auth.setDnResolver(
      new FormatDnResolver("uid=%s,%s", new Object[] {resolver.getBaseDn()}));
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(uid));
    auth.setDnResolver(resolver);

    // test one level searching
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(user));

    // test duplicate DNs
    final String filter = resolver.getUserFilter();
    resolver.setUserFilter(duplicateFilter);
    try {
      auth.resolveDn(user);
      AssertJUnit.fail("Should have thrown LdapException");
    } catch (Exception e) {
      AssertJUnit.assertEquals(LdapException.class, e.getClass());
    }

    resolver.setAllowMultipleDns(true);
    auth.resolveDn(user);
    resolver.setUserFilter(filter);
    resolver.setAllowMultipleDns(false);

    // test subtree searching
    resolver.setSubtreeSearch(true);
    final String baseDn = resolver.getBaseDn();
    resolver.setBaseDn(baseDn.substring(baseDn.indexOf(",") + 1));
    AssertJUnit.assertEquals(testLdapEntry.getDn(), auth.resolveDn(user));
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
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticateDn(
    final String dn,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    // test plain auth
    final Authenticator auth = createTLSDnAuthenticator(false);
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
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticateDnSsl(
    final String dn,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    // test plain auth
    final Authenticator auth = createSSLDnAuthenticator(false);
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
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
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
    final Authenticator auth = createTLSDnAuthenticator(false);

    // test plain auth
    try {
      auth.authenticate(
        new AuthenticationRequest(
          dn,
          new Credential(INVALID_PASSWD),
          new AuthorizationHandler[] {
            new CompareAuthorizationHandler(new SearchFilter(filter)), }));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    try {
      auth.authenticate(
        new AuthenticationRequest(
          dn,
          new Credential(credential),
          new AuthorizationHandler[] {
            new CompareAuthorizationHandler(
              new SearchFilter(INVALID_FILTER)), }));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(
        dn,
        new Credential(credential),
        new AuthorizationHandler[] {
          new CompareAuthorizationHandler(
            new SearchFilter(filter, filterArgs.split("\\|"))), }));

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
  @Test(groups = {"auth"})
  public void authenticateDnHandler(
    final String dn,
    final String credential,
    final String filter,
    final String filterArgs)
    throws Exception
  {
    final Authenticator auth = createTLSDnAuthenticator(true);

    final TestAuthenticationResultHandler authHandler =
      new TestAuthenticationResultHandler();
    auth.setAuthenticationResultHandlers(
      new AuthenticationResultHandler[] {authHandler});

    final TestAuthorizationHandler testAuthzHandler =
      new TestAuthorizationHandler();
    final AuthorizationHandler[] authzHandlers =
      new AuthorizationHandler[] {testAuthzHandler};

    try {
      auth.authenticate(
        new AuthenticationRequest(
          dn, new Credential(INVALID_PASSWD), authzHandlers));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    AssertJUnit.assertTrue(!authHandler.getResults().isEmpty());
    AssertJUnit.assertFalse(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertTrue(testAuthzHandler.getResults().isEmpty());

    try {
      auth.authenticate(
        new AuthenticationRequest(
          dn, new Credential(credential), authzHandlers));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
    AssertJUnit.assertFalse(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertFalse(!testAuthzHandler.getResults().isEmpty());

    testAuthzHandler.setSucceed(true);

    auth.authenticate(
      new AuthenticationRequest(dn, new Credential(credential), authzHandlers));
    AssertJUnit.assertTrue(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertTrue(testAuthzHandler.getResults().get(0).equals(dn));

    authHandler.getResults().clear();
    testAuthzHandler.getResults().clear();

    final AuthenticationRequest authRequest = new AuthenticationRequest(
      dn, new Credential(credential), authzHandlers);
    authRequest.setAuthorizationFilter(filter);
    authRequest.setAuthorizationFilterArgs(filterArgs.split("\\|"));
    auth.authenticate(authRequest);
    AssertJUnit.assertTrue(authHandler.getResults().get(dn).booleanValue());
    AssertJUnit.assertTrue(testAuthzHandler.getResults().get(0).equals(dn));
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "digestMd5User", "digestMd5Credential" })
  @Test(groups = {"auth"})
  public void authenticateDigestMd5(final String user, final String credential)
    throws Exception
  {
    final String[] supportedMechanisms =
      DefaultConnectionFactory.getDefaultProvider().
        getSupportedSaslMechanisms();
    if (!Arrays.asList(
        supportedMechanisms).contains(Mechanism.DIGEST_MD5.toString())) {
      throw new SkipException("DIGEST-MD5 not supported.");
    }

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
  @Test(groups = {"auth"})
  public void authenticateCramMd5(final String user, final String credential)
    throws Exception
  {
    final String[] supportedMechanisms =
      DefaultConnectionFactory.getDefaultProvider().
        getSupportedSaslMechanisms();
    if (!Arrays.asList(
        supportedMechanisms).contains(Mechanism.CRAM_MD5.toString())) {
      throw new SkipException("CRAM-MD5 not supported.");
    }

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
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticate(
    final String user,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(false);

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
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticateSsl(
    final String user,
    final String credential,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = createSSLAuthenticator(false);

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
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticateAndAuthorize(
    final String user,
    final String credential,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(false);

    // test plain auth
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user,
          new Credential(INVALID_PASSWD),
          new AuthorizationHandler[] {
            new CompareAuthorizationHandler(new SearchFilter(filter)), }));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user,
          new Credential(credential),
          new AuthorizationHandler[] {
            new CompareAuthorizationHandler(
              new SearchFilter(INVALID_FILTER)), }));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        new AuthorizationHandler[] {
          new CompareAuthorizationHandler(new SearchFilter(filter)), }));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|"),
        new AuthorizationHandler[] {
          new CompareAuthorizationHandler(
            new SearchFilter(filter)), })).getResult();
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
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticateAndAuthorizeCompare(
    final String user,
    final String credential,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(true);
    final AuthenticationHandler ah = auth.getAuthenticationHandler();
    final ConnectionFactory authCf =
      ((ConnectionFactoryManager) ah).getConnectionFactory();
    auth.setAuthenticationHandler(new CompareAuthenticationHandler(authCf));

    // test plain auth
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user,
          new Credential(INVALID_PASSWD),
          new AuthorizationHandler[] {
            new CompareAuthorizationHandler(new SearchFilter(filter)), }));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }
    try {
      auth.authenticate(
        new AuthenticationRequest(
          user,
          new Credential(credential),
          new AuthorizationHandler[] {
            new CompareAuthorizationHandler(
              new SearchFilter(INVALID_FILTER)), }));
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
    auth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        new AuthorizationHandler[] {
          new CompareAuthorizationHandler(new SearchFilter(filter)), }));

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
    groups = {"auth"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void authenticatePooled(
    final String user,
    final String credential,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    // test plain auth
    try {
      pooledTLSAuth.authenticate(
        new AuthenticationRequest(user, new Credential(INVALID_PASSWD)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthenticationException.class, e.getClass());
    }

    pooledTLSAuth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));

    // test auth with return attributes
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapEntry entry = pooledTLSAuth.authenticate(
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
  @Test(groups = {"auth"})
  public void authenticateExceptions(
    final String user,
    final String credential,
    final String returnAttrs)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(true);

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

    final AuthenticationRequest authRequest = new AuthenticationRequest(
      user, new Credential(credential), returnAttrs.split("\\|"));
    authRequest.setAuthorizationFilter(INVALID_FILTER);
    try {
      auth.authenticate(authRequest);
      AssertJUnit.fail("Should have thrown AuthorizationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(AuthorizationException.class, e.getClass());
    }
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
  @Test(groups = {"auth"})
  public void authenticateSpecialChars(
    final String user, final String credential)
    throws Exception
  {
    final Authenticator auth = createTLSAuthenticator(true);

    // test without rewrite
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

    // test with rewrite
    ((SearchDnResolver) auth.getDnResolver()).setBaseDn("dc=blah");
    ((SearchDnResolver) auth.getDnResolver()).setSubtreeSearch(true);
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
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "authenticateUser",
      "authenticateCredential"
    }
  )
  @Test(groups = {"auth"})
  public void authenticatePasswordPolicy(
    final String user, final String credential)
    throws Exception
  {
    final String[] supportedControls =
      DefaultConnectionFactory.getDefaultProvider().getSupportedControls();
    if (!Arrays.asList(supportedControls).contains(PasswordPolicyControl.OID)) {
      throw new SkipException("Password Policy not supported.");
    }

    final Connection conn = TestUtil.createSetupConnection();
    conn.open();

    final Authenticator auth = createTLSAuthenticator(true);
    final BindAuthenticationHandler ah =
      (BindAuthenticationHandler) auth.getAuthenticationHandler();
    ah.setAuthenticationControls(new PasswordPolicyControl());

    // test bind sending ppolicy control
    final LdapEntry entry = auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential))).getResult();

    // test bind on locked account
    final ModifyOperation modify = new ModifyOperation(conn);
    modify.execute(
      new ModifyRequest(
        entry.getDn(),
        new AttributeModification[] {
          new AttributeModification(
            AttributeModificationType.ADD,
            new LdapAttribute("pwdAccountLockedTime", "000001010000Z")), }));

    try {
      auth.authenticate(
        new AuthenticationRequest(user, new Credential(credential)));
      AssertJUnit.fail("Should have thrown AuthenticationException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(
        AuthenticationException.class, e.getClass());
      final PasswordPolicyControl ppc =
        (PasswordPolicyControl) ((AuthenticationException) e).getControls()[0];
      AssertJUnit.assertEquals(
        PasswordPolicyControl.Error.ACCOUNT_LOCKED, ppc.getError());
    }

    // test bind with expiration time
    modify.execute(
      new ModifyRequest(
        entry.getDn(),
        new AttributeModification[] {
          new AttributeModification(
            AttributeModificationType.REMOVE,
            new LdapAttribute("pwdAccountLockedTime")), }));
    conn.close();

    final Response<LdapEntry> response = auth.authenticate(
      new AuthenticationRequest(user, new Credential(credential)));
    final PasswordPolicyControl ppc =
      (PasswordPolicyControl) response.getControls()[0];
    AssertJUnit.assertTrue(ppc.getTimeBeforeExpiration() > 0);
  }
}
