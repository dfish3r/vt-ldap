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
 * <code>LdapActivationException</code> is thrown when an attempt to activate a
 * ldap object fails. See {@link LdapFactory#activate}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapActivationException extends LdapPoolException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = -6185502955113178610L;


  /**
   * This creates a new <code>LdapActivationException</code> with the supplied
   * <code>String</code>.
   *
   * @param  msg  <code>String</code>
   */
  public LdapActivationException(final String msg)
  {
    super(msg);
  }


  /**
   * This creates a new <code>LdapActivationException</code> with the supplied
   * <code>Exception</code>.
   *
   * @param  e  <code>Exception</code>
   */
  public LdapActivationException(final Exception e)
  {
    super(e);
  }


  /**
   * This creates a new <code>LdapActivationException</code> with the supplied
   * <code>String</code> and <code>Exception</code>.
   *
   * @param  msg  <code>String</code>
   * @param  e  <code>Exception</code>
   */
  public LdapActivationException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
