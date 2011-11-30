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
 * Unit test for {@link ActiveDirectoryAccountState}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1808 $
 */
public class ActiveDirectoryAccountStateTest
{


  /**
   * AD test data.
   *
   * @return  error messages
   */
  @DataProvider(name = "errors")
  public Object[][] createTestParams()
  {
    return new Object[][] {
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 525, v893",
        ActiveDirectoryAccountState.Error.NO_SUCH_USER,
      },
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 52e, v893",
        ActiveDirectoryAccountState.Error.LOGON_FAILURE,
      },
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 530, v893",
        ActiveDirectoryAccountState.Error.INVALID_LOGON_HOURS,
      },
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 531, v893",
        ActiveDirectoryAccountState.Error.INVALID_WORKSTATION,
      },
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 532, v893",
        ActiveDirectoryAccountState.Error.PASSWORD_EXPIRED,
      },
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 533, v893",
        ActiveDirectoryAccountState.Error.ACCOUNT_DISABLED,
      },
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 701, v893",
        ActiveDirectoryAccountState.Error.ACCOUNT_EXPIRED,
      },
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 773, v893",
        ActiveDirectoryAccountState.Error.PASSWORD_MUST_CHANGE,
      },
      new Object[] {
        "80090308: LdapErr: DSID-0C09030B, comment: " +
          "AcceptSecurityContext error, data 775, v893",
        ActiveDirectoryAccountState.Error.ACCOUNT_LOCKED_OUT,
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
    final String message, final ActiveDirectoryAccountState.Error err)
    throws Exception
  {
    AssertJUnit.assertEquals(
      err, ActiveDirectoryAccountState.Error.parse(message));
  }
}
