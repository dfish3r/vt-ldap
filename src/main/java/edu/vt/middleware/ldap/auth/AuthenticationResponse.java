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
import edu.vt.middleware.ldap.control.Control;

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
  }


  /**
   * Creates a new ldap response.
   *
   * @param  success  authentication result
   * @param  rc  result code from the underlying ldap operation
   * @param  entry  of the authenticated user
   * @param  controls  response controls from the underlying ldap operation
   */
  public AuthenticationResponse(
    final Boolean success,
    final ResultCode rc,
    final LdapEntry entry,
    final Control[] controls)
  {
    super(success, rc, controls);
    ldapEntry = entry;
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
}
