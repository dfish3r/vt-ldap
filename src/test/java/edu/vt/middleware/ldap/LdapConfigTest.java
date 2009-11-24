/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for {@link LdapConfig}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapConfigTest
{


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ldaptest"})
  public void nullProperties()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      LdapConfigTest.class.getResourceAsStream("/ldap.null.properties"));

    AssertJUnit.assertNull(l.getLdapConfig().getSslSocketFactory());
    AssertJUnit.assertNull(l.getLdapConfig().getHostnameVerifier());
    AssertJUnit.assertNull(l.getLdapConfig().getOperationRetryExceptions());
    AssertJUnit.assertNull(l.getLdapConfig().getSearchResultHandlers());
    AssertJUnit.assertNull(l.getLdapConfig().getHandlerIgnoreExceptions());
  }
}
