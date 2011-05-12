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
package edu.vt.middleware.ldap;


/**
 * Base exception for all ldap related exceptions. Provider specific exception
 * can be found using {@link #getCause()}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $
 */
public class LdapException extends Exception
{
  /** serialVersionUID. */
  private static final long serialVersionUID = 7149010199182440257L;

  /** ldap result code. */
  private ResultCode resultCode;


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   */
  public LdapException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   * @param  rc  result code
   */
  public LdapException(final String msg, final ResultCode rc)
  {
    super(msg);
    resultCode = rc;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  e  provider specific exception
   */
  public LdapException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  e  provider specific exception
   * @param  rc  result code
   */
  public LdapException(final Exception e, final ResultCode rc)
  {
    super(e);
    resultCode = rc;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   */
  public LdapException(final String msg, final Exception e)
  {
    super(msg, e);
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   * @param  rc  result code
   */
  public LdapException(final String msg, final Exception e, final ResultCode rc)
  {
    super(msg, e);
    resultCode = rc;
  }


  /**
   * Returns the ldap result code associated with this exception. May be null
   * if the provider did not set this value or could not determine this value.
   *
   * @return  ldap result code
   */
  public ResultCode getResultCode()
  {
    return resultCode;
  }
}
