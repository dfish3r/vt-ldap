/*
  $Id: NoopDnResolver.java 2105 2011-09-14 19:14:47Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 2105 $
  Updated: $Date: 2011-09-14 15:14:47 -0400 (Wed, 14 Sep 2011) $
*/
package edu.vt.middleware.ldap.auth;

import edu.vt.middleware.ldap.LdapException;

/**
 * Returns a DN that is the user identifier.
 *
 * @author  Middleware Services
 * @version  $Revision: 2105 $ $Date: 2011-09-14 15:14:47 -0400 (Wed, 14 Sep 2011) $
 */
public class NoOpDnResolver implements DnResolver
{


  /** Default constructor. */
  public NoOpDnResolver() {}


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
