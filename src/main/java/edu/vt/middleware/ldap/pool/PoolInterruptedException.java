/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.pool;

/**
 * <code>PoolInterruptedException</code> is thrown when a pool thread is
 * unexpectedly interrupted while blocking.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class PoolInterruptedException extends LdapPoolException
{

  /** serialVersionUID */
  private static final long serialVersionUID = 3788775913431470860L;


  /**
   * This creates a new <code>PoolInterruptedException</code> with the supplied
   * <code>String</code>.
   *
   * @param  msg  <code>String</code>
   */
  public PoolInterruptedException(final String msg)
  {
    super(msg);
  }


  /**
   * This creates a new <code>PoolInterruptedException</code> with the supplied
   * <code>Exception</code>.
   *
   * @param  e  <code>Exception</code>
   */
  public PoolInterruptedException(final Exception e)
  {
    super(e);
  }


  /**
   * This creates a new <code>PoolInterruptedException</code> with the supplied
   * <code>String</code> and <code>Exception</code>.
   *
   * @param  msg  <code>String</code>
   * @param  e  <code>Exception</code>
   */
  public PoolInterruptedException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
