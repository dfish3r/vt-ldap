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
    final AddOperation add = new AddOperation(conn);
    final Response<Void> response = add.execute(
      new AddRequest(testLdapEntry.getDn(), testLdapEntry.getAttributes()));
    AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void compare()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final CompareOperation compare = new CompareOperation(conn);
    AssertJUnit.assertTrue(
      compare.execute(
        new CompareRequest(
          testLdapEntry.getDn(),
          testLdapEntry.getAttribute("mail"))).getResult());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"conn"})
  public void delete()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final DeleteOperation delete = new DeleteOperation(conn);
    final Response<Void> response = delete.execute(
      new DeleteRequest(testLdapEntry.getDn()));
    AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void modify()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final ModifyOperation modify = new ModifyOperation(conn);
    final Response<Void> response = modify.execute(
      new ModifyRequest(testLdapEntry.getDn(),
        new AttributeModification[] {
          new AttributeModification(
            AttributeModificationType.ADD,
            new LdapAttribute("title", "President")), }));
    AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void rename()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final RenameOperation rename = new RenameOperation(conn);
    Response<Void> response = rename.execute(
      new RenameRequest(
        testLdapEntry.getDn(), "uid=1500,ou=test,dc=vt,dc=edu"));
    AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    response = rename.execute(
      new RenameRequest(
        "uid=1500,ou=test,dc=vt,dc=edu", testLdapEntry.getDn()));
    AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void search()
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult lr = search.execute(
      new SearchRequest(
        "ou=test,dc=vt,dc=edu", new SearchFilter("(uid=15)"))).getResult();
    AssertJUnit.assertEquals(testLdapEntry.getDn(), lr.getEntry().getDn());
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"conn"})
  public void strategyConnect()
    throws Exception
  {
    final DefaultConnectionFactory connFactory = new DefaultConnectionFactory(
      TestUtil.readConnectionConfig(
        TestUtil.class.getResourceAsStream(
          "/edu/vt/middleware/ldap/ldap.conn.properties")));
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
  @Parameters({ "ldapTestHost", "sleepTime" })
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
    final String newHost = TestUtil.getHostFromLdapUrl(host);
    final int openConns = TestUtil.countOpenConnections(
      newHost.substring(0, newHost.indexOf(".")));
    AssertJUnit.assertEquals(10, openConns);
  }
}
