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
package edu.vt.middleware.ldap.provider;

import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ResultCode;

/**
 * Exception thrown when an ldap connection attempt fails.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $
 */
public class ConnectionException extends LdapException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 4580519762257348563L;


  /**
   * Creates a new connection exception.
   *
   * @param  msg  describing this exception
   */
  public ConnectionException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new connection exception.
   *
   * @param  msg  describing this exception
   * @param  rc  result code
   */
  public ConnectionException(final String msg, final ResultCode rc)
  {
    super(msg, rc);
  }


  /**
   * Creates a new connection exception.
   *
   * @param  e  provider specific exception
   */
  public ConnectionException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new connection exception.
   *
   * @param  e  provider specific exception
   * @param  rc  result code
   */
  public ConnectionException(final Exception e, final ResultCode rc)
  {
    super(e, rc);
  }


  /**
   * Creates a new connection exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   */
  public ConnectionException(final String msg, final Exception e)
  {
    super(msg, e);
  }


  /**
   * Creates a new connection exception.
   *
   * @param  msg  describing this exception
   * @param  e  provider specific exception
   * @param  rc  result code
   */
  public ConnectionException(
    final String msg, final Exception e, final ResultCode rc)
  {
    super(msg, e, rc);
  }
}
