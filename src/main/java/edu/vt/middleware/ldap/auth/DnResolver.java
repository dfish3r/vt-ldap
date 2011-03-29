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
 * Provides an interface for finding LDAP DNs with a user identifier.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface DnResolver
{


  /**
   * Attempts to find the LDAP DN for the supplied user.
   *
   * @param  user  to find DN for
   *
   * @return  user DN
   *
   * @throws  LdapException  if an LDAP error occurs
   */
  String resolve(String user) throws LdapException;
}
