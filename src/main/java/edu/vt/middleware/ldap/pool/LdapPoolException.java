/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.pool;

/**
 * <code>LdapPoolException</code> is the base exception thrown when a pool
 * operation fails.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class LdapPoolException extends Exception
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 4077412841480524865L;


  /**
   * This creates a new <code>LdapPoolException</code> with the supplied <code>
   * String</code>.
   *
   * @param  msg  <code>String</code>
   */
  public LdapPoolException(final String msg)
  {
    super(msg);
  }


  /**
   * This creates a new <code>LdapPoolException</code> with the supplied <code>
   * Exception</code>.
   *
   * @param  e  <code>Exception</code>
   */
  public LdapPoolException(final Exception e)
  {
    super(e);
  }


  /**
   * This creates a new <code>LdapPoolException</code> with the supplied <code>
   * String</code> and <code>Exception</code>.
   *
   * @param  msg  <code>String</code>
   * @param  e  <code>Exception</code>
   */
  public LdapPoolException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
