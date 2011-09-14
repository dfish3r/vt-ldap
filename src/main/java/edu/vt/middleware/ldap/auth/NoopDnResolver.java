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

import edu.vt.middleware.ldap.LdapException;

/**
 * Returns a DN that is the user identifier.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NoopDnResolver implements DnResolver
{


  /** Default constructor. */
  public NoopDnResolver() {}


  /**
   * Returns the user as the DN.
   *
   * @param  user  to set as DN
   *
   * @return  user as DN
   *
   * @throws  LdapException  never
   */
  public String resolve(final String user)
    throws LdapException
  {
    return user;
  }
}
