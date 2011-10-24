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
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;

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
   * Creates a new authenticator.
   *
   * @param  resolver  dn resolver
   * @param  handler  authentication handler
   */
  public Authenticator(
    final DnResolver resolver,
    final AuthenticationHandler handler)
  {
    setDnResolver(resolver);
    setAuthenticationHandler(handler);
  }


  /**
   * Authenticate the user in the supplied request.
   *
   * @param  request  authentication request
   * @return  response containing the ldap entry of the user authenticated
   * @throws  LdapException  if an LDAP error occurs
   */
  public AuthenticationResponse authenticate(
    final AuthenticationRequest request)
    throws LdapException
  {
    return authenticate(resolveDn(request.getUser()), request);
  }


  /**
   * Performs authentication by opening a new connection to the LDAP and binding
   * as the supplied DN. If return attributes have been request, the user entry
   * will be searched on the same connection. Authentication result handlers are
   * processed and then the connection is closed.
   *
   * @param  dn  to authenticate as
   * @param  request  containing authentication parameters
   * @return  ldap entry for the supplied DN
   * @throws  LdapException  if an LDAP error occurs
   */
  protected AuthenticationResponse authenticate(
    final String dn, final AuthenticationRequest request)
    throws LdapException
  {
    // check the credential
    final Credential credential = request.getCredential();
    if (credential == null || credential.getBytes() == null) {
      throw new IllegalArgumentException(
        "Cannot authenticate dn, credential cannot be null");
    }
    if (credential.getBytes().length == 0) {
      throw new IllegalArgumentException(
        "Cannot authenticate dn, credential cannot be empty");
    }

    // check the dn
    if (dn == null || "".equals(dn)) {
      throw new IllegalArgumentException(
        "Cannot authenticate dn, dn cannot be empty or null");
    }

    LdapEntry entry = null;

    AuthenticationHandlerResponse response = null;
    Connection conn = null;
    try {
      final AuthenticationCriteria ac = new AuthenticationCriteria(dn);
      ac.setCredential(request.getCredential());

      // attempt to bind as this dn
      response = authenticationHandler.authenticate(ac);
      conn = response.getConnection();
      if (response.getResult()) {
        // resolve entry
        entry = resolveEntry(request, conn, ac);

        logger.info("Authentication succeeded for dn: {}", ac.getDn());
        if (authenticationResultHandlers != null &&
            authenticationResultHandlers.length > 0) {
          for (AuthenticationResultHandler ah : authenticationResultHandlers) {
            ah.process(ac, true);
          }
        }
      } else {
        logger.info("Authentication failed for dn: {}", ac.getDn());
        if (authenticationResultHandlers != null &&
            authenticationResultHandlers.length > 0) {
          for (AuthenticationResultHandler ah : authenticationResultHandlers) {
            ah.process(ac, false);
          }
        }
      }

    } finally {
      if (conn != null) {
        conn.close();
      }
    }

    return new AuthenticationResponse(
      response.getResult(),
      response.getResultCode(),
      entry,
      response.getControls());
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
        "[%s@%d::dnResolver=%s, authenticationHandler=%s, " +
        "entryResolver=%s, authenticationResultHandlers=%s]",
        getClass().getName(),
        hashCode(),
        dnResolver,
        authenticationHandler,
        entryResolver,
        authenticationResultHandlers != null ?
          Arrays.asList(authenticationResultHandlers) : null);
  }
}
