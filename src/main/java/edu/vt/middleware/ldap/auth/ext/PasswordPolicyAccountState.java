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
package edu.vt.middleware.ldap.auth.ext;

import java.util.Calendar;
import edu.vt.middleware.ldap.auth.AccountState;
import edu.vt.middleware.ldap.control.PasswordPolicyControl;

/**
 * Represents the state of an account as described by a password policy control.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class PasswordPolicyAccountState extends AccountState<Integer>
{

  /** password policy specific enum. */
  private final PasswordPolicyControl.Error ppError;


  /**
   * Creates a new password policy account state.
   *
   * @param  exp  account expiration
   * @param  remaining  number of logins available
   */
  public PasswordPolicyAccountState(final Calendar exp, final int remaining)
  {
    super(new AccountState.Warning(exp, remaining));
    ppError = null;
  }


  /**
   * Creates a new password policy account state.
   *
   * @param  error  containing password policy error details
   */
  public PasswordPolicyAccountState(final PasswordPolicyControl.Error error)
  {
    super(new AccountState.Error(error.value(), error.name()));
    ppError = error;
  }


  /**
   * Returns the password policy error for this account state.
   *
   * @return  password policy error
   */
  public PasswordPolicyControl.Error getPasswordPolicyError()
  {
    return ppError;
  }
}
