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

import org.testng.AssertJUnit;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;

/**
 * Sleeps at the end of all tests and check open connections.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapConnTest
{


  /**
   * @param  host  to check for connections with.
   * @param  sleepTime  time to sleep for.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "ldapHost", "sleepTime" })
  @AfterSuite(groups = {"conntest"})
  public void sleep(final String host, final int sleepTime)
    throws Exception
  {
    Thread.sleep(sleepTime);

    /*
     * -- expected open connections --
     * LdapTest: 1
     * LdapCliTest:0
     * AuthenticatorTest: 2
     * AuthenticatorCliTest: 0
     * LdapResultTest: 0
     * LdapLoginModuleTest: 0
     * SessionManagerTest: 1
     * SearchServletTest: 6
     * AttributeServletTest: 3
     */
    final int openConns = TestUtil.countOpenConnections(host);
    AssertJUnit.assertEquals(13, openConns);
  }
}
