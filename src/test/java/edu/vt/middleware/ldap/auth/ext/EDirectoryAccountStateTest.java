/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1808 $
  Updated: $Date: 2011-01-26 21:36:10 -0500 (Wed, 26 Jan 2011) $
*/
package edu.vt.middleware.ldap.auth.ext;

import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link EDirectoryAccountState}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1808 $
 */
public class EDirectoryAccountStateTest
{


  /**
   * EDirectory test data.
   *
   * @return  error messages
   */
  @DataProvider(name = "errors")
  public Object[][] createTestParams()
  {
    return new Object[][] {
      new Object[] {
        "LDAP: error code 49 - NDS error: failed authentication (-669)",
        EDirectoryAccountState.Error.FAILED_AUTHENTICATION,
      },
      new Object[] {
        "LDAP: error code 49 - NDS error: password expired (-223)",
        EDirectoryAccountState.Error.PASSWORD_EXPIRED,
      },
      new Object[] {
        "LDAP: error code 49 - NDS error: bad password (-222)",
        EDirectoryAccountState.Error.BAD_PASSWORD,
      },
      new Object[] {
        "LDAP: error code 49 - NDS error: log account expired (-220)",
        EDirectoryAccountState.Error.ACCOUNT_EXPIRED,
      },
      new Object[] {
        "LDAP: error code 49 - NDS error: " +
          "maximum logins exceeded or Q stn not server (-217)",
        EDirectoryAccountState.Error.MAXIMUM_LOGINS_EXCEEDED,
      },
      new Object[] {
        "LDAP: error code 49 - NDS error: bad login time or Q halted (-218)",
        EDirectoryAccountState.Error.LOGIN_TIME_LIMITED,
      },
      new Object[] {
        "LDAP: error code 49 - NDS error: login lockout (-197)",
        EDirectoryAccountState.Error.LOGIN_LOCKOUT,
      },
    };
  }


  /**
   * Tests message parsing.
   *
   * @param  message  error message
   * @param  err  corresponding error enum
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"auth-ext"}, dataProvider = "errors")
  public void parseMessage(
    final String message, final EDirectoryAccountState.Error err)
    throws Exception
  {
    AssertJUnit.assertEquals(err, EDirectoryAccountState.Error.parse(message));
  }
}
