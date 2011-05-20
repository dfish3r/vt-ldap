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
    la.setName(entry.getDn().split(",")[0].split("=")[0]);
    la.addStringValue(entry.getDn().split(",")[0].split("=")[1]);
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
