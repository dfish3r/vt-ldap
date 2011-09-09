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

import edu.vt.middleware.ldap.provider.ConnectionStrategy;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Connection}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1633 $
 */
public class ConnectionTest
{
  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry15")
  @BeforeClass(groups = {"conn"})
  public void add(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    final Connection conn = TestUtil.createConnection();
    conn.open();
    conn.add(testLdapEntry.getDn(), testLdapEntry.getAttributes());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void compare()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    AssertJUnit.assertTrue(conn.compare(
      testLdapEntry.getDn(), testLdapEntry.getAttribute("mail")));
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"conn"})
  public void delete()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    conn.delete(testLdapEntry.getDn());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void modify()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    conn.modify(
      testLdapEntry.getDn(),
      new AttributeModification[] {
        new AttributeModification(
          AttributeModificationType.ADD,
          new LdapAttribute("title", "President")), });
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void rename()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    conn.rename(testLdapEntry.getDn(), "uid=1500,ou=test,dc=vt,dc=edu");
    conn.rename("uid=1500,ou=test,dc=vt,dc=edu", testLdapEntry.getDn());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void search()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final LdapResult lr = conn.search(
      "ou=test,dc=vt,dc=edu", new SearchFilter("(uid=15)"), null);
    AssertJUnit.assertEquals(testLdapEntry.getDn(), lr.getEntry().getDn());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"}, timeOut = 5000)
  public void saslExternalConnect()
    throws Exception
  {
    final Connection conn = TestUtil.createSaslExternalConnection();
    conn.open();
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void strategyConnect()
    throws Exception
  {
    final ConnectionFactory connFactory = new ConnectionFactory(
      TestUtil.readConnectionConfig(
        TestUtil.class.getResourceAsStream("/ldap.conn.properties")));
    Connection conn = connFactory.getConnection();

    conn.open();
    conn.close();
    conn.open();
    conn.close();
    conn.open();
    conn.close();

    connFactory.getProvider().getProviderConfig().setConnectionStrategy(
      ConnectionStrategy.DEFAULT);
    conn = connFactory.getConnection();
    conn.open();
    conn.close();
    conn.open();
    conn.close();
    conn.open();
    conn.close();

    connFactory.getProvider().getProviderConfig().setConnectionStrategy(
      ConnectionStrategy.ACTIVE_PASSIVE);
    conn = connFactory.getConnection();
    conn.open();
    conn.close();
    conn.open();
    conn.close();
    conn.open();
    conn.close();

    connFactory.getProvider().getProviderConfig().setConnectionStrategy(
      ConnectionStrategy.RANDOM);
    conn = connFactory.getConnection();
    conn.open();
    conn.close();
    conn.open();
    conn.close();
    conn.open();
    conn.close();
  }


  /**
   * Sleeps at the end of all tests and checks open connections.
   *
   * @param  host  to check for connections with.
   * @param  sleepTime  time to sleep for.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "ldapHost", "sleepTime" })
  @AfterSuite(groups = {"conn"})
  public void sleep(final String host, final int sleepTime)
    throws Exception
  {
    Thread.sleep(sleepTime);

    /*
     * -- expected open connections --
     * SearchOperationTest: 1
     * SearchServletTest: 6
     * AttributeServletTest: 3
     */
    final int openConns = TestUtil.countOpenConnections(host);
    AssertJUnit.assertEquals(10, openConns);
  }
}
