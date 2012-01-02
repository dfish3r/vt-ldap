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
 * Thrown when a blocking operation times out. See {@link
 * ConnectionPool#getConnection()}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class BlockingTimeoutException extends PoolException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = -5152940431346111294L;


  /**
   * Creates a new blocking timeout exception.
   *
   * @param  msg  describing this exception
   */
  public BlockingTimeoutException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new blocking timeout exception.
   *
   * @param  e  pooling specific exception
   */
  public BlockingTimeoutException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new blocking timeout exception.
   *
   * @param  msg  describing this exception
   * @param  e  pooling specific exception
   */
  public BlockingTimeoutException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
