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
package edu.vt.middleware.ldap.jaas;

import java.util.Set;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Looks up a user's roles using an LDAP search.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface RoleResolver
{


  /**
   * Executes a search request and converts any attributes to ldap roles.
   *
   * @param  request  to execute
   *
   * @return  ldap roles
   *
   * @throws  LdapException  if the ldap operation fails
   */
  Set<LdapRole> search(final SearchRequest request) throws LdapException;
}
