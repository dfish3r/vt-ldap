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
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.auth.handler.AuthenticationCriteria;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.auth.handler.CompareAuthorizationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for authenticator implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractAuthenticator
{
  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** For finding user DNs. */
  protected DnResolver dnResolver;

  /** Handler to process authentication. */
  protected AuthenticationHandler authenticationHandler;

  /** Handlers to process authentication results. */
  protected AuthenticationResultHandler[] authenticationResultHandlers;


  /**
   * Returns the DN resolver.
   *
   * @return  DN resolver
   */
  public DnResolver getDnResolver()
  {
    return dnResolver;
  }


  /**
   * Sets the DN resolver.
   *
   * @param  resolver  for finding DNs
   */
  public void setDnResolver(final DnResolver resolver)
  {
    dnResolver = resolver;
  }


  /**
   * Returns the authentication handler.
   *
   * @return  authentication handler
   */
  public AuthenticationHandler getAuthenticationHandler()
  {
    return authenticationHandler;
  }


  /**
   * Sets the authentication handler.
   *
   * @param  handler  for performing authentication
   */
  public void setAuthenticationHandler(final AuthenticationHandler handler)
  {
    authenticationHandler = handler;
  }


  /**
   * Returns the authentication result handlers.
   *
   * @return  authentication result handlers
   */
  public AuthenticationResultHandler[] getAuthenticationResultHandlers()
  {
    return authenticationResultHandlers;
  }


  /**
   * Sets the authentication result handlers.
   *
   * @param  arh  authentication result handlers
   */
  public void setAuthenticationResultHandlers(
    final AuthenticationResultHandler[] arh)
  {
    authenticationResultHandlers = arh;
  }


  /**
   * This will attempt to find the DN for the supplied user. {@link
   * DnResolver#resolve(String)} is invoked to perform this operation.
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
    return dnResolver.resolve(user);
  }


  /**
   * Invokes the authentication handler. If an authentication exception is
   * thrown from the handler, the authentication result handlers are processed
   * to record the failure.
   *
   * @param  authHandler  to perform authentication
   * @param  authResultHandler  to process authentication failures
   * @param  ac  needed by both the authentication handler and the result
   * handlers
   * @return  ldap connection that the bind occurred on
   * @throws  AuthenticationException  if the bind fails
   * @throws  LdapException  if an LDAP error occurs
   */
  protected LdapConnection authenticate(
    final AuthenticationHandler authHandler,
    final AuthenticationResultHandler[] authResultHandler,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    LdapConnection conn = null;
    try {
      conn = authHandler.authenticate(ac);
      logger.info("Authentication succeeded for dn: {}", ac.getDn());
    } catch (AuthenticationException e) {
      logger.info("Authentication failed for dn: {}", ac.getDn());
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
    final LdapConnection conn,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    if (authzHandler != null && authzHandler.length > 0) {
      for (AuthorizationHandler azh : authzHandler) {
        try {
          azh.process(ac, conn);
          logger.info(
            "Authorization succeeded for dn: {} with handler: {}",
            ac.getDn(),
            azh);
        } catch (AuthorizationException e) {
          logger.info(
            "Authorization failed for dn: {} with handler: {}",
            ac.getDn(),
            azh);
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
    final String dn,
    final AuthenticationRequest request,
    final LdapConnection conn)
    throws LdapException
  {
    logger.debug(
      "Returning attributes: {}",
      request.getReturnAttributes() == null ?
        "all attributes" : Arrays.toString(request.getReturnAttributes()));
    final SearchOperation search = new SearchOperation(conn);
    return search.execute(SearchRequest.newObjectScopeSearchRequest(
      dn, request.getReturnAttributes())).getResult();
  }


  /**
   * Creates authorization handlers based on the authentication request and the
   * authentication configuration. Defers to the request data if an option has
   * been set, otherwise uses the configuration data. If an authorization filter
   * has been supplied, this results in the compare authorization filter being
   * added to the handlers.
   *
   * @param  request  containing authentication data
   * @return  authorization handlers
   */
  protected AuthorizationHandler[] getAuthorizationHandlers(
    final AuthenticationRequest request)
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
