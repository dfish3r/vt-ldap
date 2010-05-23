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
package edu.vt.middleware.ldap.auth;

import javax.naming.NamingException;

/**
 * <code>AuthorizationException</code> is thrown when an attempt to authorize a
 * user fails.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AuthorizationException extends NamingException
{

  /** serialVersionUID. */
  private static final long serialVersionUID = -6290236661997869406L;


  /** Default constructor. */
  public AuthorizationException()
  {
    super();
  }


  /**
   * This creates a new <code>AuthorizationException</code> with the supplied
   * <code>String</code>.
   *
   * @param  msg  <code>String</code>
   */
  public AuthorizationException(final String msg)
  {
    super(msg);
  }
}
