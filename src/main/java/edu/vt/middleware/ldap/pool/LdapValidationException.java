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
 * <code>LdapValidationException</code> is thrown when an attempt to validate a
 * ldap object fails. See {@link LdapFactory#validate}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class LdapValidationException extends LdapPoolException
{

  /** serialVersionUID */
  private static final long serialVersionUID = -3130116579807362686L;


  /**
   * This creates a new <code>LdapValidationException</code> with the supplied
   * <code>String</code>.
   *
   * @param  msg  <code>String</code>
   */
  public LdapValidationException(final String msg)
  {
    super(msg);
  }


  /**
   * This creates a new <code>LdapValidationException</code> with the supplied
   * <code>Exception</code>.
   *
   * @param  e  <code>Exception</code>
   */
  public LdapValidationException(final Exception e)
  {
    super(e);
  }


  /**
   * This creates a new <code>LdapValidationException</code> with the supplied
   * <code>String</code> and <code>Exception</code>.
   *
   * @param  msg  <code>String</code>
   * @param  e  <code>Exception</code>
   */
  public LdapValidationException(final String msg, final Exception e)
  {
    super(msg, e);
  }
}
