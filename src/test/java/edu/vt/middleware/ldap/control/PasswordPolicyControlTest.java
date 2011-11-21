/*
  $Id$

  Copyright (C) 2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.control;

import edu.vt.middleware.ldap.LdapUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link PasswordPolicyControl}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PasswordPolicyControlTest
{


  /**
   * Password policy control test data.
   *
   * @return  response test data
   */
  @DataProvider(name = "response")
  public Object[][] createResponseData()
  {
    final PasswordPolicyControl p1 = new PasswordPolicyControl();
    p1.setTimeBeforeExpiration(2513067);
    final PasswordPolicyControl p2 = new PasswordPolicyControl();
    p2.setGraceAuthNsRemaining(4);
    final PasswordPolicyControl p3 = new PasswordPolicyControl();
    p3.setError(PasswordPolicyControl.Error.PASSWORD_EXPIRED);
    final PasswordPolicyControl p4 = new PasswordPolicyControl();
    p4.setError(PasswordPolicyControl.Error.ACCOUNT_LOCKED);
    return new Object[][] {
      // Test case #1
      // only timeBeforeExpiration is set
      new Object[] {
        LdapUtil.base64Decode("MAegBYADJlir"),
        p1,
      },
      // Test case #2
      // only graceAuthNsRemaining is set
      new Object[] {
        LdapUtil.base64Decode("MAWgA4EBBA=="),
        p2,
      },
      // Test case #3
      // error=passwordExpired
      new Object[] {
        LdapUtil.base64Decode("MAOBAQA="),
        p3,
      },
      // Test case #4
      // error=accountLocked
      new Object[] {
        LdapUtil.base64Decode("MAOBAQE="),
        p4,
      },
    };
  }


  /**
   * @param  berValue  to decode.
   * @param  expected  ppolicy control to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"control"}, dataProvider = "response")
  public void testParsePassordPolicyControl(
    final byte[] berValue, final PasswordPolicyControl expected)
    throws Exception
  {
    final PasswordPolicyControl actual =
      PasswordPolicyControl.parsePasswordPolicy(berValue);

    Assert.assertEquals(
        actual.getTimeBeforeExpiration(), expected.getTimeBeforeExpiration());
    Assert.assertEquals(
      actual.getGraceAuthNsRemaining(), expected.getGraceAuthNsRemaining());
    Assert.assertEquals(actual.getError(), expected.getError());
  }
}
