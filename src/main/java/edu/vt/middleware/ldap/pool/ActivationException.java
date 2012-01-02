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
 * Thrown when an attempt to activate a pooled object fails.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ActivationException extends PoolException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = -6185502955113178610L;


  /**
   * Creates a new activation exception.
   *
   * @param  msg  describing this exception
   */
  public ActivationException(final String msg)
  {
    super(msg);
  }


  /**
   * Creates a new activation exception.
   *
   * @param  e  pooling specific exception
   */
  public ActivationException(final Exception e)
  {
    super(e);
  }


  /**
   * Creates a new activation exception.
   *
   * @param  msg  describing this exception
   * @param  e  pooling specific exception
   */
  public ActivationException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
