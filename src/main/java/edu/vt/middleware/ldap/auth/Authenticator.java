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

import java.util.Arrays;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResponse;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.auth.handler.AuthenticationCriteria;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.auth.handler.BindAuthenticationHandler;
import edu.vt.middleware.ldap.provider.Connection;

/**
 * Provides functionality to authenticate users against an ldap directory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Authenticator extends AbstractAuthenticator
{


  /** Default constructor. */
  public Authenticator() {}


  /**
   * Creates a new authenticator. See
   * {@link #Authenticator(LdapConnectionConfig)}.
   *
   * @param  ldapUrl  to connect to
   */
  public Authenticator(final String ldapUrl)
  {
    this(new LdapConnectionConfig(ldapUrl));
  }


  /**
   * Creates a new authenticator. Defaults the DN resolver to
   * {@link SearchDnResolver} and the authentication handler to
   * {@link BindAuthenticationHandler}.
   *
   * @param  lcc  ldap connection config
   */
  public Authenticator(final LdapConnectionConfig lcc)
  {
    this(new SearchDnResolver(lcc), new BindAuthenticationHandler(lcc));
  }


  /**
   * Creates a new authenticator.
   *
   * @param  dr  dn resolver
   * @param  ah  authentication handler
   */
  public Authenticator(
    final DnResolver dr,
    final AuthenticationHandler ah)
  {
    this.setDnResolver(dr);
    this.setAuthenticationHandler(ah);
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

    AuthenticationHandler authHandler = null;
    Connection conn = null;
    try {
      final AuthenticationCriteria ac = new AuthenticationCriteria(dn);
      ac.setCredential(request.getCredential());
      authHandler = this.authenticationHandler.newInstance();

      // attempt to bind as this dn
      conn = this.authenticate(
        authHandler, this.authenticationResultHandlers, ac);

      // authentication succeeded, perform authorization if supplied
      final AuthorizationHandler[] authzHandler =
        this.getAuthorizationHandlers(request);
      this.authorize(authzHandler, this.authenticationResultHandlers, conn, ac);

      // retrieve requested attributes
      if (request.getReturnAttributes() == null ||
          request.getReturnAttributes().length > 0) {
        result = this.getLdapEntry(dn, request, conn);
      }

      // authentication and authorization succeeded, report result
      if (this.authenticationResultHandlers != null &&
          this.authenticationResultHandlers.length > 0) {
        for (AuthenticationResultHandler ah :
             this.authenticationResultHandlers) {
          ah.process(ac, true);
        }
      }
    } finally {
      if (authHandler != null) {
        authHandler.destroy(conn);
      }
    }

    if (result != null) {
      return result.getEntry();
    } else {
      return new LdapEntry(dn);
    }
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "%s@%d: dnResolver=%s, authenticationHandler=%s, " +
        "authenticationResultHandlers=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.dnResolver,
        this.authenticationHandler,
        this.authenticationResultHandlers != null ?
          Arrays.asList(this.authenticationResultHandlers) : null);
  }
}
