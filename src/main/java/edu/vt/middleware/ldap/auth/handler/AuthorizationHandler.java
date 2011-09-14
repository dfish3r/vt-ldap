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
import edu.vt.middleware.ldap.LdapException;

/**
 * Provides processing of authorization queries after authentication has
 * succeeded.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface AuthorizationHandler
{


  /**
   * Process an authorization after an ldap authentication. The supplied
   * connection should <b>not</b> be closed in this method. Implementations
   * should throw authorization exception to indicate an authorization failure.
   *
   * @param  conn  connection used to perform the bind
   * @param  criteria  authentication criteria used to perform the
   * authentication
   *
   * @throws  AuthorizationException  if authorization fails
   * @throws  LdapException  if ldap operation fails
   */
  void process(Connection conn, AuthenticationCriteria criteria)
    throws LdapException;
}
