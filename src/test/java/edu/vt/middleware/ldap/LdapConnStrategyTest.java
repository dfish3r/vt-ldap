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

import edu.vt.middleware.ldap.handler.ConnectionHandler;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for {@link ConnectionHandler} with different strategies.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapConnStrategyTest
{


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ldaptest"})
  public void connect()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      LdapConnStrategyTest.class.getResourceAsStream("/ldap.conn.properties"));

    AssertJUnit.assertTrue(l.connect());
    l.close();
    AssertJUnit.assertTrue(l.connect());
    l.close();
    AssertJUnit.assertTrue(l.connect());

    l.getLdapConfig().getConnectionHandler().setConnectionStrategy(
      ConnectionHandler.ConnectionStrategy.DEFAULT);
    AssertJUnit.assertTrue(l.connect());
    l.close();
    AssertJUnit.assertTrue(l.connect());
    l.close();
    AssertJUnit.assertTrue(l.connect());

    l.getLdapConfig().getConnectionHandler().setConnectionStrategy(
      ConnectionHandler.ConnectionStrategy.ACTIVE_PASSIVE);
    AssertJUnit.assertTrue(l.connect());
    l.close();
    AssertJUnit.assertTrue(l.connect());
    l.close();
    AssertJUnit.assertTrue(l.connect());

    l.getLdapConfig().getConnectionHandler().setConnectionStrategy(
      ConnectionHandler.ConnectionStrategy.RANDOM);
    AssertJUnit.assertTrue(l.connect());
    l.close();
    AssertJUnit.assertTrue(l.connect());
    l.close();
    AssertJUnit.assertTrue(l.connect());
    l.close();
  }
}
