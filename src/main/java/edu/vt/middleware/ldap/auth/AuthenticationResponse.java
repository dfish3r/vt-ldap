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

import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.Response;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.control.ResponseControl;

/**
 * Response object for authenticator.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticationResponse extends Response<Boolean>
{

  /** Ldap entry of authenticated user. */
  private final LdapEntry ldapEntry;

  /** Authentication message. */
  private final String message;

  /** Account state. */
  private AccountState<?> accountState;


  /**
   * Creates a new authentication response.
   *
   * @param  success  authentication result
   * @param  rc  result code from the underlying ldap operation
   * @param  entry  of the authenticated user
   */
  public AuthenticationResponse(
    final Boolean success, final ResultCode rc, final LdapEntry entry)
  {
    super(success, rc);
    ldapEntry = entry;
    message = null;
  }


  /**
   * Creates a new authentication response.
   *
   * @param  success  authentication result
   * @param  rc  result code from the underlying ldap operation
   * @param  entry  of the authenticated user
   * @param  msg  authentication message
   */
  public AuthenticationResponse(
    final Boolean success,
    final ResultCode rc,
    final LdapEntry entry,
    final String msg)
  {
    super(success, rc);
    ldapEntry = entry;
    message = msg;
  }


  /**
   * Creates a new authentication response.
   *
   * @param  success  authentication result
   * @param  rc  result code from the underlying ldap operation
   * @param  entry  of the authenticated user
   * @param  msg  authentication message
   * @param  controls  response controls from the underlying ldap operation
   */
  public AuthenticationResponse(
    final Boolean success,
    final ResultCode rc,
    final LdapEntry entry,
    final String msg,
    final ResponseControl[] controls)
  {
    super(success, rc, controls);
    ldapEntry = entry;
    message = msg;
  }


  /**
   * Returns the ldap entry of the authenticated user.
   *
   * @return  ldap entry
   */
  public LdapEntry getLdapEntry()
  {
    return ldapEntry;
  }


  /**
   * Returns any message associated with the authentication of a user.
   *
   * @return  message
   */
  public String getMessage()
  {
    return message;
  }


  /**
   * Returns the account state associated with the authenticated user.
   *
   * @return  account state
   */
  public AccountState<?> getAccountState()
  {
    return accountState;
  }


  /**
   * Sets the account state for the authenticated user.
   *
   * @param  state  for this user
   */
  public void setAccountState(final AccountState<?> state)
  {
    accountState = state;
  }
}
