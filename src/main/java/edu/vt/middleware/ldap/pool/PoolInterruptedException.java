/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.pool;

/**
 * Thrown when a pool thread is unexpectedly interrupted while blocking.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PoolInterruptedException extends PoolException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 3788775913431470860L;


  /**
   * Creates a new pool interrupted exception.
   *
   * @param  msg  describing this exception
   */
  public PoolInterruptedException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new pool interrupted exception.
   *
   * @param  e  pooling specific exception
   */
  public PoolInterruptedException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new pool interrupted exception.
   *
   * @param  msg  describing this exception
   * @param  e  pooling specific exception
   */
  public PoolInterruptedException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
