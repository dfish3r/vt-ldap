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
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.auth.handler.AuthenticationCriteria;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.auth.handler.CompareAuthorizationHandler;
import edu.vt.middleware.ldap.provider.Connection;
import edu.vt.middleware.ldap.provider.ConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for authenticator implementations.
 *
 * @param  <T>  type of authenticator config
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractAuthenticator<T extends AuthenticatorConfig>
{
  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Authenticator configuration environment. */
  protected T config;


  /**
   * Returns the authenticator config.
   *
   * @return  authenticator config
   */
  public T getAuthenticatorConfig()
  {
    return this.config;
  }


  /**
   * Sets the authenticator config.
   *
   * @param  ac  authenticator config
   */
  public void setAuthenticatorConfig(final T ac)
  {
    if (this.config != null) {
      this.config.checkImmutable();
    }
    this.config = ac;
  }


  /**
   * This will attempt to find the DN for the supplied user. {@link
   * AuthenticatorConfig#getDnResolver()} is invoked to perform this operation.
   *
   * @param  user  to find DN for
   *
   * @return  user DN
   *
   * @throws  LdapException  if an LDAP error occurs during resolution
   */
  public String resolveDn(final String user)
    throws LdapException
  {
    return this.config.getDnResolver().resolve(user);
  }


  /**
   * Invokes the authentication handler. If an authentication exception is
   * thrown from the handler, the authentication result handlers are processed
   * to record the failure.
   *
   * @param  authHandler  to perform authentication
   * @param  authResultHandler  to process authentication failures
   * @param  cf  connection factory to pass to the authentication handler
   * @param  ac  needed by both the authentication handler and the result
   * handlers
   * @return  ldap connection that the bind occurred on
   * @throws  AuthenticationException  if the bind fails
   * @throws  LdapException  if an LDAP error occurs
   */
  protected Connection authenticate(
    final AuthenticationHandler authHandler,
    final AuthenticationResultHandler[] authResultHandler,
    final ConnectionFactory cf,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    Connection conn = null;
    try {
      conn = authHandler.authenticate(cf, ac);
      if (this.logger.isInfoEnabled()) {
        this.logger.info("Authentication succeeded for dn: " + ac.getDn());
      }
    } catch (AuthenticationException e) {
      if (this.logger.isInfoEnabled()) {
        this.logger.info("Authentication failed for dn: " + ac.getDn());
      }
      if (authResultHandler != null && authResultHandler.length > 0) {
        for (AuthenticationResultHandler ah : authResultHandler) {
          ah.process(ac, false);
        }
      }
      throw e;
    }
    return conn;
  }


  /**
   * Iterates over the supplied authorization handlers and invokes each one. If
   * an authorization exception is thrown from the handler, the authentication
   * result handlers are processed to record the failure.
   *
   * @param  authzHandler  to process
   * @param  authResultHandler  to process authorization failures
   * @param  conn  to perform authorization on
   * @param  ac  needed by both the authorization handlers and the result
   * handlers
   * @throws  AuthorizationException  if any authorization handler fails
   * @throws  LdapException  if an LDAP error occurs
   */
  protected void authorize(
    final AuthorizationHandler[] authzHandler,
    final AuthenticationResultHandler[] authResultHandler,
    final Connection conn,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    if (authzHandler != null && authzHandler.length > 0) {
      for (AuthorizationHandler azh : authzHandler) {
        try {
          azh.process(ac, conn);
          if (this.logger.isInfoEnabled()) {
            this.logger.info(
              "Authorization succeeded for dn: " + ac.getDn() +
              " with handler: " + azh);
          }
        } catch (AuthorizationException e) {
          if (this.logger.isInfoEnabled()) {
            this.logger.info(
              "Authorization failed for dn: " + ac.getDn() +
              " with handler: " + azh);
          }
          if (authResultHandler != null && authResultHandler.length > 0) {
            for (AuthenticationResultHandler ah : authResultHandler) {
              ah.process(ac, false);
            }
          }
          throw e;
        }
      }
    }
  }


  /**
   * Retrieves the ldap entry for the supplied DN from the ldap.
   *
   * @param  dn  of the ldap entry to retrieve
   * @param  request  containing the return attributes to include
   * @param  conn  to perform the search on
   * @return  ldap result containing the ldap entry
   * @throws  LdapException  if an LDAP error occurs
   */
  protected LdapResult getLdapEntry(
    final String dn, final AuthenticationRequest request, final Connection conn)
    throws LdapException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Returning attributes: ");
      this.logger.debug(
        "    " +
        (request.getReturnAttributes() == null ?
          "all attributes" : Arrays.toString(request.getReturnAttributes())));
    }
    return conn.search(SearchRequest.newObjectScopeSearchRequest(
      dn, request.getReturnAttributes()));
  }


  /**
   * Creates authorization handlers based on the authentication request and the
   * authentication configuration. Defers to the request data if an option has
   * been set, otherwise uses the configuration data. If an authorization filter
   * has been supplied, this results in the compare authorization filter being
   * added to the handlers.
   *
   * @param  request  containing authentication data
   * @param  ac  configuration containing authentication data
   * @return  authorization handlers
   */
  protected AuthorizationHandler[] getAuthorizationHandlers(
    final AuthenticationRequest request, final AuthenticatorConfig ac)
  {
    SearchFilter filter = null;
    if (request.getAuthorizationFilter() != null) {
      filter = new SearchFilter(
        request.getAuthorizationFilter(), request.getAuthorizationFilterArgs());
    }

    AuthorizationHandler[] ah = null;
    if (request.getAuthorizationHandlers() != null) {
      ah = request.getAuthorizationHandlers();
    }

    int size = 0;
    if (filter != null) {
      size += 1;
    }
    if (ah != null) {
      size += ah.length;
    }
    final AuthorizationHandler[] authzHandler = new AuthorizationHandler[size];
    if (filter != null) {
      authzHandler[0] = new CompareAuthorizationHandler(filter);
    }
    if (ah != null) {
      if (authzHandler[0] != null) {
        System.arraycopy(ah, 0, authzHandler, 1, ah.length);
      } else {
        System.arraycopy(ah, 0, authzHandler, 0, ah.length);
      }
    }
    return authzHandler;
  }
}
