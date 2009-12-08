/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.auth.handler;

import javax.naming.NamingException;
import edu.vt.middleware.ldap.auth.AuthenticatorConfig;
import edu.vt.middleware.ldap.handler.ConnectionHandler;

/**
 * <code>AuthenticationHandler</code> provides an interface for LDAP
 * authentication implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface AuthenticationHandler
{


  /**
   * Sets the authenticator configuration.
   *
   * @param  ac  authenticator config
   */
  void setAuthenticatorConfig(AuthenticatorConfig ac);


  /**
   * Perform an ldap authentication. Implementations should throw
   * <code>AuthenticationException</code> to indicate an authentication failure.
   * The resulting <code>LdapContext</code> can be retrieved from the
   * connection handler if it is needed.
   *
   * @param  ch  <code>ConnectionHandler</code> to communicate with the LDAP
   * @param  ac  <code>AuthenticationCriteria</code> to perform the
   * authentication with
   *
   * @throws  AuthenticationException  if authentication fails
   * @throws  NamingException  if an LDAP error occurs
   */
  void authenticate(ConnectionHandler ch, AuthenticationCriteria ac)
    throws NamingException;


  /**
   * Returns a separate instance of this authentication handler.
   *
   * @return  authentication handler
   */
  AuthenticationHandler newInstance();
}
