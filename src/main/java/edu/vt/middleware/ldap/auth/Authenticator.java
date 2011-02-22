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

import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResponse;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.auth.handler.AuthenticationCriteria;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.provider.Connection;
import edu.vt.middleware.ldap.provider.ConnectionFactory;

/**
 * Provides functionality to authenticate users against an ldap directory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Authenticator extends AbstractAuthenticator<AuthenticatorConfig>
{


  /** Default constructor. */
  public Authenticator() {}


  /**
   * Creates a new authenticator.
   *
   * @param  ac  authentication config
   */
  public Authenticator(final AuthenticatorConfig ac)
  {
    this.setAuthenticatorConfig(ac);
  }


  /**
   * Authenticate the user in the supplied request.
   *
   * @param  request  authentication request
   * @return  response containing the ldap entry of the user authenticated
   * @throws  AuthenticationException  if authentication fails
   * @throws  AuthorizationException  if authorization fails
   * @throws  LdapException  if an LDAP error occurs
   */
  public LdapResponse<LdapEntry> authenticate(
    final AuthenticationRequest request)
    throws LdapException
  {
    this.initializeRequest(request, this.config);
    return new LdapResponse<LdapEntry>(
      this.authenticate(this.resolveDn(request.getUser()), request));
  }


  /**
   * Performs authentication by opening a new connection to the LDAP and binding
   * as the supplied DN. Authorization handlers are invoking directly after a
   * successful bind. If return attributes have been request, the user entry
   * will be searched on the same connection. Authentication result handlers are
   * processed and then the connection is closed.
   *
   * @param  dn  to authenticate as
   * @param  request  containing authentication parameters
   * @return  ldap entry for the supplied DN
   * @throws  AuthenticationException  if authentication fails
   * @throws  AuthorizationException  if authorization fails
   * @throws  LdapException  if an LDAP error occurs
   */
  protected LdapEntry authenticate(
    final String dn, final AuthenticationRequest request)
    throws LdapException
  {
    // check the credential
    final Credential credential = request.getCredential();
    if (credential == null || credential.getBytes() == null) {
      throw new AuthenticationException(
        "Cannot authenticate dn, credential cannot be null");
    }
    if (credential.getBytes().length == 0) {
      throw new AuthenticationException(
        "Cannot authenticate dn, credential cannot be empty");
    }

    // check the dn
    if (dn == null || "".equals(dn)) {
      throw new AuthenticationException(
        "Cannot authenticate dn, dn cannot be empty or null");
    }

    LdapResult result = null;

    final ConnectionFactory cf = this.config.getConnectionFactory();
    final AuthenticationResultHandler[] authResultHandler =
      this.config.getAuthenticationResultHandlers();

    Connection conn = null;
    try {
      final AuthenticationCriteria ac = new AuthenticationCriteria(dn);
      ac.setCredential(request.getCredential());
      final AuthenticationHandler authHandler =
        this.config.getAuthenticationHandler().newInstance();

      // attempt to bind as this dn
      conn = this.authenticate(authHandler, authResultHandler, cf, ac);

      // authentication succeeded, perform authorization if supplied
      final AuthorizationHandler[] authzHandler =
        this.getAuthorizationHandlers(request, this.config);
      this.authorize(authzHandler, authResultHandler, conn, ac);

      // retrieve requested attributes
      if (request.getReturnAttributes() == null ||
          request.getReturnAttributes().length > 0) {
        result = this.getLdapEntry(dn, request, conn);
      }

      // authentication and authorization succeeded, report result
      if (authResultHandler != null && authResultHandler.length > 0) {
        for (AuthenticationResultHandler ah : authResultHandler) {
          ah.process(ac, true);
        }
      }
    } finally {
      cf.destroy(conn);
    }

    if (result != null) {
      return result.getEntry();
    } else {
      return new LdapEntry(dn);
    }
  }


  /** {@inheritDoc} */
  protected void initializeRequest(
    final AuthenticationRequest request, final AuthenticatorConfig config) {}
}
