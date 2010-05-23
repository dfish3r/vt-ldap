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

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.AuthenticatorConfig;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;

/**
 * <code>JaasAuthenticator</code> is the default implementation for JAAS
 * authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JaasAuthenticator extends Authenticator
{

  /** serial version uid. */
  private static final long serialVersionUID = -7884185473690369247L;


  /** Default constructor. */
  public JaasAuthenticator() {}


  /**
   * This will create a new <code>JaasAuthenticator</code> with the supplied
   * <code>AuthenticatorConfig</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public JaasAuthenticator(final AuthenticatorConfig authConfig)
  {
    this.setAuthenticatorConfig(authConfig);
  }


  /** {@inheritDoc} */
  public Attributes authenticate(
    final String user,
    final Object credential,
    final String[] retAttrs)
    throws NamingException
  {
    return super.authenticate(user, credential, retAttrs);
  }


  /** {@inheritDoc} */
  public Attributes authenticate(
    final String user,
    final Object credential,
    final String[] retAttrs,
    final AuthenticationResultHandler[] authHandler,
    final AuthorizationHandler[] authzHandler)
    throws NamingException
  {
    if (retAttrs != null && retAttrs.length == 0) {
      return
        this.authenticateAndAuthorize(
          this.getDn(user),
          credential,
          false,
          retAttrs,
          authHandler,
          authzHandler);
    } else {
      return
        this.authenticateAndAuthorize(
          this.getDn(user),
          credential,
          true,
          retAttrs,
          authHandler,
          authzHandler);
    }
  }
}
