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
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link LdapConnection}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1633 $
 */
public class LdapConnectionTest
{


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ldapconntest"}, timeOut = 5000)
  public void saslExternalConnect()
    throws Exception
  {
    final LdapConnection conn = TestUtil.createSaslExternalLdapConnection();
    conn.open();
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ldapconntest"})
  public void strategyConnect()
    throws Exception
  {
    final LdapConnection conn = new LdapConnection(
      LdapConfig.createFromProperties(
        TestUtil.class.getResourceAsStream("/ldap.conn.properties")));

    conn.open();
    conn.close();
    conn.open();
    conn.close();
    conn.open();
    conn.close();

    conn.getLdapConfig().getConnectionFactory().setConnectionStrategy(
      ConnectionStrategy.DEFAULT);
    conn.open();
    conn.close();
    conn.open();
    conn.close();
    conn.open();
    conn.close();

    conn.getLdapConfig().getConnectionFactory().setConnectionStrategy(
      ConnectionStrategy.ACTIVE_PASSIVE);
    conn.open();
    conn.close();
    conn.open();
    conn.close();
    conn.open();
    conn.close();

    conn.getLdapConfig().getConnectionFactory().setConnectionStrategy(
      ConnectionStrategy.RANDOM);
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
  @AfterSuite(groups = {"ldapconntest"})
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
