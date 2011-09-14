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

import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ResultCode;

/**
 * Base exception for all authentication related exceptions. Provider specific
 * exception can be found using {@link #getCause()}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $
 */
public class AuthenticationException extends LdapException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = -4356650077210226663L;


  /**
   * Creates a new authentication exception.
   *
   * @param  msg  describing this exception
   */
  public AuthenticationException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new authentication exception.
   *
   * @param  msg  describing this exception
   * @param  code  result code
   */
  public AuthenticationException(final String msg, final ResultCode code)
  {
    super(msg, code);
  }


  /**
   * Creates a new authentication exception.
   *
   * @param  e  provider specific exception
   */
  public AuthenticationException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new authentication exception.
   *
   * @param  e  provider specific exception
   * @param  code  result code
   */
  public AuthenticationException(final Exception e, final ResultCode code)
  {
    super(e, code);
  }


  /**
   * Creates a new authentication exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   */
  public AuthenticationException(final String msg, final Exception e)
  {
    super(msg, e);
  }


  /**
   * Creates a new authentication exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   * @param  code  result code
   */
  public AuthenticationException(
    final String msg, final Exception e, final ResultCode code)
  {
    super(msg, e, code);
  }
}
