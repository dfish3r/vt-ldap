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
 * Base exception for all authorization related exceptions. Provider specific
 * exception can be found using {@link #getCause()}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthorizationException extends LdapException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 1391473597668333032L;


  /**
   * Creates a new authorization exception.
   *
   * @param  msg  describing this exception
   */
  public AuthorizationException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new authorization exception.
   *
   * @param  msg  describing this exception
   * @param  rc  result code
   */
  public AuthorizationException(final String msg, final ResultCode rc)
  {
    super(msg, rc);
  }


  /**
   * Creates a new authorization exception.
   *
   * @param  e  provider specific exception
   */
  public AuthorizationException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new authorization exception.
   *
   * @param  e  provider specific exception
   * @param  rc  result code
   */
  public AuthorizationException(final Exception e, final ResultCode rc)
  {
    super(e, rc);
  }


  /**
   * Creates a new authorization exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   */
  public AuthorizationException(final String msg, final Exception e)
  {
    super(msg, e);
  }


  /**
   * Creates a new authorization exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   * @param  rc  result code
   */
  public AuthorizationException(
    final String msg, final Exception e, final ResultCode rc)
  {
    super(msg, e, rc);
  }
}
