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
 * <code>BlockingTimeoutException</code> is thrown when a blocking operation
 * times out. See {@link BlockingLdapPool#checkOut()}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class BlockingTimeoutException extends LdapPoolException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = -5152940431346111294L;


  /**
   * This creates a new <code>BlockingTimeoutException</code> with the supplied
   * <code>String</code>.
   *
   * @param  msg  <code>String</code>
   */
  public BlockingTimeoutException(final String msg)
  {
    super(msg);
  }


  /**
   * This creates a new <code>BlockingTimeoutException</code> with the supplied
   * <code>Exception</code>.
   *
   * @param  e  <code>Exception</code>
   */
  public BlockingTimeoutException(final Exception e)
  {
    super(e);
  }


  /**
   * This creates a new <code>BlockingTimeoutException</code> with the supplied
   * <code>String</code> and <code>Exception</code>.
   *
   * @param  msg  <code>String</code>
   * @param  e  <code>Exception</code>
   */
  public BlockingTimeoutException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
