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
 * Thrown when the pool is empty and no new requests can be serviced.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PoolExhaustedException extends PoolException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 900885030182519501L;


  /**
   * Creates a new pool exhausted exception.
   *
   * @param  msg  describing this exception
   */
  public PoolExhaustedException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new pool exhausted exception.
   *
   * @param  e  pooling specific exception
   */
  public PoolExhaustedException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new pool exhausted exception.
   *
   * @param  msg  describing this exception
   * @param  e  pooling specific exception
   */
  public PoolExhaustedException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
