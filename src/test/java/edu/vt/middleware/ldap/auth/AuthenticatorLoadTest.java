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

import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.ConnectionFactoryManager;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.DefaultConnectionFactory;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.pool.BlockingConnectionPool;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Load test for {@link Authenticator}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthenticatorLoadTest extends AbstractTest
{

  /** Invalid password test data. */
  public static final String INVALID_PASSWD = "not-a-password";

  /** Invalid filter test data. */
  public static final String INVALID_FILTER = "(departmentNumber=1111)";

  /** Entries for auth tests. */
  private static Map<String, LdapEntry[]> entries =
    new HashMap<String, LdapEntry[]>();

  /**
   * Initialize the map of entries.
   */
  static {
    for (int i = 2; i <= 10; i++) {
      entries.put(String.valueOf(i), new LdapEntry[2]);
    }
  }

  /** Authenticator instance for concurrency testing. */
  private Authenticator singleTLSAuth;

  /** Authenticator instance for concurrency testing. */
  private Authenticator pooledTLSAuth;


  /**
   * Default constructor.
   *
   * @throws  Exception  if ldap cannot be constructed
   */
  public AuthenticatorLoadTest()
    throws Exception
  {
    singleTLSAuth = TestUtil.readAuthenticator(
      TestUtil.class.getResourceAsStream("/ldap.tls.load.properties"));
    pooledTLSAuth = TestUtil.readAuthenticator(
      TestUtil.class.getResourceAsStream("/ldap.tls.load.properties"));
  }


  /**
   * @param  ldifFile2  to create.
   * @param  ldifFile3  to create.
   * @param  ldifFile4  to create.
   * @param  ldifFile5  to create.
   * @param  ldifFile6  to create.
   * @param  ldifFile7  to create.
   * @param  ldifFile8  to create.
   * @param  ldifFile9  to create.
   * @param  ldifFile10  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "createEntry2",
      "createEntry3",
      "createEntry4",
      "createEntry5",
      "createEntry6",
      "createEntry7",
      "createEntry8",
      "createEntry9",
      "createEntry10"
    }
  )
  @BeforeClass(groups = {"authload"})
  public void createAuthEntry(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4,
    final String ldifFile5,
    final String ldifFile6,
    final String ldifFile7,
    final String ldifFile8,
    final String ldifFile9,
    final String ldifFile10)
    throws Exception
  {
    AuthenticationHandler ah = pooledTLSAuth.getAuthenticationHandler();
    final DefaultConnectionFactory cf =
      (DefaultConnectionFactory)
        ((ConnectionFactoryManager) ah).getConnectionFactory();
    final BlockingConnectionPool resolverCp = new BlockingConnectionPool(cf);
    resolverCp.initialize();
    final PooledConnectionFactory drFactory =
      new PooledConnectionFactory(resolverCp);
    final PooledSearchDnResolver dr = new PooledSearchDnResolver(drFactory);
    dr.setBaseDn(
      ((SearchDnResolver) singleTLSAuth.getDnResolver()).getBaseDn());
    dr.setUserFilter(
      ((SearchDnResolver) singleTLSAuth.getDnResolver()).getUserFilter());
    pooledTLSAuth.setDnResolver(dr);

    final BlockingConnectionPool ahCp = new BlockingConnectionPool(cf);
    ahCp.initialize();
    final PooledConnectionFactory ahFactory = new PooledConnectionFactory(ahCp);
    ah = new PooledBindAuthenticationHandler(ahFactory);
    pooledTLSAuth.setAuthenticationHandler(ah);

    entries.get("2")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile2)).getEntry();
    entries.get("3")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile3)).getEntry();
    entries.get("4")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile4)).getEntry();
    entries.get("5")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile5)).getEntry();
    entries.get("6")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile6)).getEntry();
    entries.get("7")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile7)).getEntry();
    entries.get("8")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile8)).getEntry();
    entries.get("9")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile9)).getEntry();
    entries.get("10")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile10)).getEntry();

    for (Map.Entry<String, LdapEntry[]> e : entries.entrySet()) {
      super.createLdapEntry(e.getValue()[0]);
    }
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"authload"})
  public void deleteAuthEntry()
    throws Exception
  {
    super.deleteLdapEntry(entries.get("2")[0].getDn());
    super.deleteLdapEntry(entries.get("3")[0].getDn());
    super.deleteLdapEntry(entries.get("4")[0].getDn());
    super.deleteLdapEntry(entries.get("5")[0].getDn());
    super.deleteLdapEntry(entries.get("6")[0].getDn());
    super.deleteLdapEntry(entries.get("7")[0].getDn());
    super.deleteLdapEntry(entries.get("8")[0].getDn());
    super.deleteLdapEntry(entries.get("9")[0].getDn());
    super.deleteLdapEntry(entries.get("10")[0].getDn());

    final DnResolver dr = pooledTLSAuth.getDnResolver();
    ((PooledConnectionFactoryManager)
      dr).getConnectionFactory().getConnectionPool().close();
    final AuthenticationHandler ah = pooledTLSAuth.getAuthenticationHandler();
    ((PooledConnectionFactoryManager)
      ah).getConnectionFactory().getConnectionPool().close();
  }


  /**
   * Sample authentication data.
   *
   * @return  user authentication data
   */
  @DataProvider(name = "auth-data")
  public Object[][] createAuthData()
  {
    return
      new Object[][] {
        {
          "jadams@vt.edu",
          "password2",
          "departmentNumber={1}",
          "0822",
          "cn",
          "cn=John Adams",
        },
        {
          "tjefferson@vt.edu",
          "password3",
          "departmentNumber={1}",
          "0823",
          "givenName|sn",
          "givenName=Thomas|sn=Jefferson",
        },
        {
          "jmadison@vt.edu",
          "password4",
          "departmentNumber={1}",
          "0824",
          "givenName|sn",
          "givenName=James|sn=Madison",
        },
        {
          "jmonroe@vt.edu",
          "password5",
          "departmentNumber={1}",
          "0825",
          "givenName|sn",
          "givenName=James|sn=Monroe",
        },
        {
          "jqadams@vt.edu",
          "password6",
          "departmentNumber={1}",
          "0826",
          "cn",
          "cn=John Quincy Adams",
        },
        {
          "ajackson@vt.edu",
          "password7",
          "departmentNumber={1}",
          "0827",
          "givenName|sn",
          "givenName=Andrew|sn=Jackson",
        },
        {
          "mvburen@vt.edu",
          "password8",
          "departmentNumber={1}",
          "0828",
          "givenName|sn",
          "givenName=Martin|sn=Buren",
        },
        {
          "whharrison@vt.edu",
          "password9",
          "departmentNumber={1}",
          "0829",
          "givenName|sn",
          "givenName=William|sn=Harrison",
        },
        {
          "jtyler@vt.edu",
          "password10",
          "departmentNumber={1}",
          "0830",
          "givenName|sn",
          "givenName=John|sn=Tyler",
        },
      };
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  filterArgs  to authorize with
   * @param  returnAttrs  to search for.
   * @param  expectedAttrs  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"authload"},
    dataProvider = "auth-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000
  )
  public void authenticateAndAuthorize(
    final String user,
    final String credential,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String expectedAttrs)
    throws Exception
  {
    // test auth with return attributes
    final LdapEntry expected = TestUtil.convertStringToEntry(
      null, expectedAttrs);
    final LdapEntry entry = singleTLSAuth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|"),
        new AuthorizationHandler[]{
          new CompareAuthorizationHandler(
            new SearchFilter(filter, filterArgs.split("\\|"))), })).getResult();
    expected.setDn(entry.getDn());
    AssertJUnit.assertEquals(expected, entry);
  }


  /**
   * @param  user  to authenticate.
   * @param  credential  to authenticate with.
   * @param  filter  to authorize with.
   * @param  filterArgs  to authorize with
   * @param  returnAttrs  to search for.
   * @param  expectedAttrs  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"authload"},
    dataProvider = "auth-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000,
    dependsOnMethods = {"authenticateAndAuthorize"}
  )
  public void authenticateAndAuthorizePooled(
    final String user,
    final String credential,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String expectedAttrs)
    throws Exception
  {
    // test auth with return attributes
    final LdapEntry expected = TestUtil.convertStringToEntry(
      null, expectedAttrs);
    final LdapEntry entry = pooledTLSAuth.authenticate(
      new AuthenticationRequest(
        user,
        new Credential(credential),
        returnAttrs.split("\\|"),
        new AuthorizationHandler[]{
          new CompareAuthorizationHandler(
            new SearchFilter(filter, filterArgs.split("\\|"))), })).getResult();
    expected.setDn(entry.getDn());
    AssertJUnit.assertEquals(expected, entry);
  }
}
