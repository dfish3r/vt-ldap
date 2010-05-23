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
 * <code>LdapPoolExhaustedException</code> is thrown when the pool is empty and
 * no need requests can be serviced.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapPoolExhaustedException extends LdapPoolException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = 900885030182519501L;


  /**
   * This creates a new <code>LdapPoolExhaustedException</code> with the
   * supplied <code>String</code>.
   *
   * @param  msg  <code>String</code>
   */
  public LdapPoolExhaustedException(final String msg)
  {
    super(msg);
  }


  /**
   * This creates a new <code>LdapPoolExhaustedException</code> with the
   * supplied <code>Exception</code>.
   *
   * @param  e  <code>Exception</code>
   */
  public LdapPoolExhaustedException(final Exception e)
  {
    super(e);
  }


  /**
   * This creates a new <code>LdapPoolExhaustedException</code> with the
   * supplied <code>String</code> and <code>Exception</code>.
   *
   * @param  msg  <code>String</code>
   * @param  e  <code>Exception</code>
   */
  public LdapPoolExhaustedException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
