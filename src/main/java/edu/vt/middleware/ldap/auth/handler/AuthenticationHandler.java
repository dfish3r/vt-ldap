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
package edu.vt.middleware.ldap.auth.handler;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapException;

/**
 * Provides an interface for LDAP authentication implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface AuthenticationHandler
{


  /**
   * Perform an ldap authentication. Implementations should throw authentication
   * exception to indicate an authentication failure. The
   * resulting connection should be returned so that other operations can be
   * performed on it.
   *
   * @param  ac  to perform the authentication with
   *
   * @return  connection created by the authentication operation
   *
   * @throws  AuthenticationException  if authentication fails
   * @throws  LdapException  if ldap operation fails
   */
  Connection authenticate(AuthenticationCriteria ac)
    throws LdapException;


  /**
   * Returns the connection configuration.
   *
   * @return  connection configuration
   */
  ConnectionConfig getConnectionConfig();


  /**
   * Sets the connection configuration.
   *
   * @param  cc  connection config
   */
  void setConnectionConfig(final ConnectionConfig cc);
}
