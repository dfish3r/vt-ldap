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

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;

/**
 * Provides an interface for finding a user's ldap entry after a successful
 * authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface EntryResolver
{


  /**
   * Attempts to find the LDAP entry for the supplied DN, using the supplied
   * connection.
   *
   * @param  connection  that authentication occurred on
   * @param  dn  that authenticated
   *
   * @return  ldap entry
   *
   * @throws  LdapException  if an LDAP error occurs
   */
  LdapEntry resolve(Connection connection, String dn) throws LdapException;
}
