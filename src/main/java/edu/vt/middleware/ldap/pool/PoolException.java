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
 * Base exception thrown when a pool operation fails.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PoolException extends Exception
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 4077412841480524865L;


  /**
   * Creates a new pool exception.
   *
   * @param  msg  describing this exception
   */
  public PoolException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new pool exception.
   *
   * @param  e  pooling specific exception
   */
  public PoolException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new pool exception.
   *
   * @param  msg  describing this exception
   * @param  e  pooling specific exception
   */
  public PoolException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
