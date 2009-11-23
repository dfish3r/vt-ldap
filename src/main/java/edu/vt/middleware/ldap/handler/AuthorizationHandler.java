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
package edu.vt.middleware.ldap.handler;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

/**
 * AuthorizationHandler provides processing of authorization queries after
 * authentication has succeeded.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface AuthorizationHandler
{


  /**
   * Process an authorization after an ldap authentication. The supplied
   * LdapContext should <b>not</b> be closed in this method. Implementations
   * should throw <code>AuthenticationException</code> to indicate an
   * authorization failure.
   *
   * @param  ac  <code>AuthenticationCriteria</code> used to perform the
   * authentication
   * @param  ctx  <code>LdapContext</code> authenticated context used to perform
   * the bind
   *
   * @throws  AuthenticationException  if authorization fails
   * @throws  NamingException  if an LDAP error occurs
   */
  void process(AuthenticationCriteria ac, LdapContext ctx)
    throws NamingException;
}
