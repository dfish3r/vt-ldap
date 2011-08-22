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
 * Provides an interface for DN resolution that is stateful and manages one or
 * more resources.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ManagedDnResolver extends DnResolver
{


  /**
   * Prepares this resolver for use. Must be called before
   * {@link #resolve(String)}.
   *
   * @throws LdapException  if an error occurs initializing resources
   */
  void initialize() throws LdapException;


  /**
   * Frees any resources associated with this resolver.
   */
  void close();
}
