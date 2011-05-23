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
package edu.vt.middleware.ldap.pool;

/**
 * Thrown when an attempt to validate an ldap connection fails. See
 * {@link ConnectionFactory#validate}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ValidationException extends PoolException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = -3130116579807362686L;


  /**
   * Creates a new validation exception.
   *
   * @param  msg  describing this exception
   */
  public ValidationException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new validation exception.
   *
   * @param  e  pooling specific exception
   */
  public ValidationException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new validation exception.
   *
   * @param  msg  describing this exception
   * @param  e  pooling specific exception
   */
  public ValidationException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
