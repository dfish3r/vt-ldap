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

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * Contains functions that run before and after all tests.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class TestControl
{

  /** DN to block on. */
  public static final String DN = "uid=1,ou=test,dc=vt,dc=edu";

  /** Attribute to block on. */
  public static final LdapAttribute ATTR_IDLE =
    new LdapAttribute("mail", "test-idle@vt.edu");

  /** Attribute to block on. */
  public static final LdapAttribute ATTR_RUNNING =
    new LdapAttribute("mail", "test-running@vt.edu");

  /** Time to wait before checking if lock is available. */
  public static final int WAIT_TIME = 5000;


  /**
   * Obtains the lock before running all tests.
   *
   * @throws Exception on test failure
   */
  @BeforeSuite(alwaysRun = true)
  @Parameters({"ldapTestsIgnoreLock"}) 
  public void setup(final String ignoreLock)
    throws Exception
  {
    if (!Boolean.valueOf(ignoreLock)) {
      final Connection conn = TestUtil.createSetupConnection();
      conn.open();
      // wait for other tests to finish
      int i = 1;
      while (!conn.compare(DN, ATTR_IDLE)) {
        System.err.println("Waiting for test lock...");
        Thread.sleep(WAIT_TIME * i++);
      }
      conn.modify(
        DN,
        new AttributeModification[] {
          new AttributeModification(
            AttributeModificationType.REPLACE, ATTR_RUNNING), });
      conn.close();
    }
  }


  /**
   * Releases the lock after running all tests.
   *
   * @throws Exception on test failure
   */
  @AfterSuite(alwaysRun = true)
  public void teardown()
    throws Exception
  {
    final Connection conn = TestUtil.createSetupConnection();
    conn.open();
    // set attribute when tests are finished
    conn.modify(
      DN,
      new AttributeModification[] {
        new AttributeModification(
          AttributeModificationType.REPLACE, ATTR_IDLE), });
    conn.close();
  }
}
