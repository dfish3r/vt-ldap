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

import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.provider.Connection;

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
   * Tear down a connection that was created by a call to
   * {@link #authenticate(AuthenticationCriteria)}.
   *
   * @param  conn  connection to destroy
   *
   * @throws  LdapException  if an LDAP error occurs
   */
  void destroy(Connection conn) throws LdapException;


  /**
   * Returns the ldap connection configuration.
   *
   * @return  ldap connection configuration
   */
  LdapConnectionConfig getLdapConnectionConfig();


  /**
   * Sets the ldap connection configuration.
   *
   * @param  config  ldap connection config
   */
  void setLdapConnectionConfig(final LdapConnectionConfig config);


  /**
   * Returns a separate instance of this authentication handler.
   *
   * @return  authentication handler
   */
  AuthenticationHandler newInstance();
}
