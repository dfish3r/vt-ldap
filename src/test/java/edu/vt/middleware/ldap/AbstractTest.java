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

/**
 * Contains functions common to all tests.
 *
 * @author  Middleware Services
 * @version  $Revision: 1633 $
 */
public abstract class AbstractTest
{

  /** Number of threads for threaded tests. */
  public static final int TEST_THREAD_POOL_SIZE = 2;

  /** Invocation count for threaded tests. */
  public static final int TEST_INVOCATION_COUNT = 10;

  /** Timeout for threaded tests. */
  public static final int TEST_TIME_OUT = 60000;


  /**
   * Creates the supplied ldap entry and confirms it exists in the ldap.
   *
   * @param  entry  to create.
   *
   * @throws  Exception  On failure.
   */
  public void createLdapEntry(final LdapEntry entry)
    throws Exception
  {
    Connection conn = TestUtil.createSetupConnection();
    conn.open();
    final AddOperation create = new AddOperation(conn);
    create.execute(new AddRequest(entry.getDn(), entry.getAttributes()));
    conn.close();
    conn = TestUtil.createConnection();
    conn.open();
    final CompareOperation compare = new CompareOperation(conn);
    final LdapAttribute la = new LdapAttribute();
    la.setName(entry.getDn().split(",ou=")[0].split("=", 2)[0]);
    la.addStringValue(
      entry.getDn().split(",ou=")[0].split("=", 2)[1].replaceAll("\\\\", ""));
    while (
      !compare.execute(new CompareRequest(entry.getDn(), la)).getResult()) {
      Thread.sleep(100);
    }
    conn.close();
  }


  /**
   * Deletes the supplied dn.
   *
   * @param  dn  to delete
   *
   * @throws  Exception  On failure.
   */
  public void deleteLdapEntry(final String dn)
    throws Exception
  {
    final Connection conn = TestUtil.createSetupConnection();
    conn.open();
    final DeleteOperation delete = new DeleteOperation(conn);
    delete.execute(new DeleteRequest(dn));
    conn.close();
  }
}
