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
package edu.vt.middleware.ldap.auth;

import java.util.Calendar;

/**
 * Represents the state of an LDAP account based account policies for that LDAP.
 * Note that only a warning or an error may be set, not both.
 *
 * @param  <T>  account state warning type
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class AccountState<T>
{

  /** account warning. */
  private final AccountState.Warning accountWarning;

  /** account error. */
  private final AccountState.Error accountError;


  /**
   * Creates a new account state.
   *
   * @param  warning  associated with the account
   */
  public AccountState(final AccountState.Warning warning)
  {
    accountWarning = warning;
    accountError = null;
  }


  /**
   * Creates a new account state.
   *
   * @param  error  associated with the account
   */
  public AccountState(final AccountState.Error error)
  {
    accountWarning = null;
    accountError = error;
  }


  /**
   * Returns the account state warning.
   *
   * @return  account state warning
   */
  public AccountState.Warning getWarning()
  {
    return accountWarning;
  }


  /**
   * Returns the account state error.
   *
   * @return  account state error
   */
  public AccountState.Error getError()
  {
    return accountError;
  }


  /**
   * Contains warning information for an account state. <T> indicates the type
   * of expirationTime the LDAP uses.
   */
  public static class Warning
  {

    /** expiration. */
    private final Calendar expiration;

    /** number of logins remaining before the account locks. */
    private final int loginsRemaining;


    /**
     * Creates a new warning.
     *
     * @param  exp  date of expiration
     * @param  remaining  number of logins
     */
    public Warning(final Calendar exp, final int remaining)
    {
      expiration = exp;
      loginsRemaining = remaining;
    }


    /**
     * Returns the expiration.
     *
     * @return  expiration
     */
    public Calendar getExpiration()
    {
      return expiration;
    }


    /**
     * Returns the number of logins remaining until the account locks.
     *
     * @return  number of logins remaining
     */
    public int getLoginsRemaining()
    {
      return loginsRemaining;
    }
  }


  /**
   * Contains error information for an account state.
   */
  public static class Error
  {

    /** error code. */
    private final int errorCode;

    /** error message. */
    private final String errorMessage;


    /**
     * Creates a new error.
     *
     * @param  code  for this error
     * @param  message  for this error
     */
    public Error(final int code, final String message)
    {
      errorCode = code;
      errorMessage = message;
    }


    /**
     * Returns the error code.
     *
     * @return  error code
     */
    public int getCode()
    {
      return errorCode;
    }


    /**
     * Returns the error message.
     *
     * @return  error message
     */
    public String getMessage()
    {
      return errorMessage;
    }
  }
}
