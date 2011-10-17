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

import edu.vt.middleware.ldap.control.Control;

/**
 * Base exception for all ldap related exceptions. Provider specific exception
 * can be found using {@link #getCause()}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $
 */
public class LdapException extends Exception implements Message
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 7149010199182440257L;

  /** ldap result code. */
  private final ResultCode resultCode;

  /** response controls. */
  private final Control[] controls;


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   */
  public LdapException(final String msg)
  {
    super(msg);
    resultCode = null;
    controls = null;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   * @param  code  result code
   */
  public LdapException(final String msg, final ResultCode code)
  {
    super(msg);
    resultCode = code;
    controls = null;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   * @param  code  result code
   * @param  c  response controls
   */
  public LdapException(
    final String msg, final ResultCode code, final Control[] c)
  {
    super(msg);
    resultCode = code;
    controls = c;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  e  provider specific exception
   */
  public LdapException(final Exception e)
  {
    super(e);
    resultCode = null;
    controls = null;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  e  provider specific exception
   * @param  code  result code
   */
  public LdapException(final Exception e, final ResultCode code)
  {
    super(e);
    resultCode = code;
    controls = null;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  e  provider specific exception
   * @param  code  result code
   * @param  c  response controls
   */
  public LdapException(
    final Exception e, final ResultCode code, final Control[] c)
  {
    super(e);
    resultCode = code;
    controls = c;
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
    resultCode = null;
    controls = null;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   * @param  code  result code
   */
  public LdapException(
    final String msg, final Exception e, final ResultCode code)
  {
    super(msg, e);
    resultCode = code;
    controls = null;
  }


  /**
   * Creates a new ldap exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   * @param  code  result code
   * @param  c  response controls
   */
  public LdapException(
    final String msg,
    final Exception e,
    final ResultCode code,
    final Control[] c)
  {
    super(msg, e);
    resultCode = code;
    controls = c;
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


  /** {@inheritDoc} */
  @Override
  public Control[] getControls()
  {
    return controls;
  }
}
