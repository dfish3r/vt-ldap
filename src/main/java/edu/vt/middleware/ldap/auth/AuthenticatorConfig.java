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
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.auth.handler.BindAuthenticationHandler;

/**
 * Contains all the configuration data needed to control authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticatorConfig extends LdapConnectionConfig
{
  /** DN to search. */
  protected String baseDn = "";

  /** Filter for searching for the user. */
  private String userFilter;

  /** Filter arguments for searching for the user. */
  private Object[] userFilterArgs;

  /** Filter for authorizing user. */
  private String authorizationFilter;

  /** Filter arguments for authorizing user. */
  private Object[] authorizationFilterArgs;

  /** Whether to throw an exception if multiple DNs are found. */
  private boolean allowMultipleDns;

  /** Whether to use a subtree search when resolving DNs. */
  private boolean subtreeSearch;

  /** For finding user DNs. */
  private DnResolver dnResolver;

  /** Handler to process authentication. */
  private AuthenticationHandler authenticationHandler;

  /** Handlers to process authentication results. */
  private AuthenticationResultHandler[] authenticationResultHandlers;

  /** Handlers to authorize the user. */
  private AuthorizationHandler[] authorizationHandlers;


  /** Default constructor. */
  public AuthenticatorConfig() {}


  /**
   * Creates a new auth config.
   *
   * @param  ldapUrl  to connect to
   */
  public AuthenticatorConfig(final String ldapUrl)
  {
    this();
    this.setLdapUrl(ldapUrl);
  }


  /**
   * Returns the base DN.
   *
   * @return  base DN
   */
  public String getBaseDn()
  {
    return this.baseDn;
  }


  /**
   * Sets the base DN.
   *
   * @param  dn base DN
   */
  public void setBaseDn(final String dn)
  {
    this.baseDn = dn;
  }


  /**
   * Returns the filter used to search for the user.
   *
   * @return  filter  for searching
   */
  public String getUserFilter()
  {
    return this.userFilter;
  }


  /**
   * Sets the filter used to search for the user.
   *
   * @param  filter  for searching
   */
  public void setUserFilter(final String filter)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting userFilter: " + filter);
    }
    this.userFilter = filter;
  }


  /**
   * Returns the filter arguments used to search for the user.
   *
   * @return  filter arguments
   */
  public Object[] getUserFilterArgs()
  {
    return this.userFilterArgs;
  }


  /**
   * Sets the filter arguments used to search for the user.
   *
   * @param  filterArgs  filter arguments
   */
  public void setUserFilterArgs(final Object[] filterArgs)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace(
        "setting userFilterArgs: " + Arrays.toString(filterArgs));
    }
    this.userFilterArgs = filterArgs;
  }


  /**
   * Returns the filter used to authorize the user.
   *
   * @return  filter
   */
  public String getAuthorizationFilter()
  {
    return this.authorizationFilter;
  }


  /**
   * Sets the filter used to authorize the user. If not set, no authorization
   * is performed.
   *
   * @param  filter  for authorization
   */
  public void setAuthorizationFilter(final String filter)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting authorizationFilter: " + filter);
    }
    this.authorizationFilter = filter;
  }


  /**
   * Returns the filter arguments used to authorize the user.
   *
   * @return  filter arguments
   */
  public Object[] getAuthorizationFilterArgs()
  {
    return this.authorizationFilterArgs;
  }


  /**
   * Sets the filter arguments used to authorize the user.
   *
   * @param  filterArgs  filter arguments
   */
  public void setAuthorizationFilterArgs(final Object[] filterArgs)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace(
        "setting authorizationFilterArgs: " + Arrays.toString(filterArgs));
    }
    this.authorizationFilterArgs = filterArgs;
  }


  /**
   * Returns whether the {@link ConstructDnResolver} will be used.
   *
   * @return  whether the DN will be constructed
   */
  public boolean getConstructDn()
  {
    return
      this.dnResolver != null &&
        this.dnResolver.getClass().isAssignableFrom(ConstructDnResolver.class);
  }


  /**
   * Sets whether the {@link ConstructDnResolver} will be used. If true, the
   * {@link #dnResolver} is set to {@link ConstructDnResolver}. If false, the
   * {@link #dnResolver} is set to {@link SearchDnResolver}.
   *
   * @param  b  whether to construct DNs
   */
  public void setConstructDn(final boolean b)
  {
    if (b) {
      this.setDnResolver(new ConstructDnResolver());
    } else {
      this.setDnResolver(new SearchDnResolver());
    }
  }


  /**
   * Returns whether DN resolution should fail if multiple DNs are found.
   *
   * @return  whether an exception will be thrown if multiple DNs are found
   */
  public boolean getAllowMultipleDns()
  {
    return this.allowMultipleDns;
  }


  /**
   * Sets whether DN resolution should fail if multiple DNs are found
   * If false an exception will be thrown if {@link Authenticator#getDn(String)}
   * finds more than one DN matching it's filter. Otherwise the first DN found
   * is returned.
   *
   * @param  b  whether multiple DNs are allowed
   */
  public void setAllowMultipleDns(final boolean b)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting allowMultipleDns: " + b);
    }
    this.allowMultipleDns = b;
  }


  /**
   * Returns whether subtree searching will be used.
   *
   * @return  whether the DN will be searched for over the entire base
   */
  public boolean getSubtreeSearch()
  {
    return this.subtreeSearch;
  }


  /**
   * Sets whether subtree searching will be used. If true, the DN used for
   * authenticating will be searched for over the entire {@link #getBaseDn()}.
   * Otherwise the DN will be search for in the {@link #getBaseDn()} context.
   *
   * @param  b  whether the DN will be searched for over the entire base
   */
  public void setSubtreeSearch(final boolean b)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting subtreeSearch: " + b);
    }
    this.subtreeSearch = b;
  }


  /**
   * Returns the DN resolver. Lazily initializes the DN resolver to {@link
   * SearchDnResolver} if it is not set when this method is invoked.
   *
   * @return  DN resolver
   */
  public DnResolver getDnResolver()
  {
    if (this.dnResolver == null) {
      this.dnResolver = new SearchDnResolver(this);
    }
    return this.dnResolver;
  }


  /**
   * Sets the DN resolver.
   *
   * @param  resolver  for finding DNs
   */
  public void setDnResolver(final DnResolver resolver)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting dnResolver: " + resolver);
    }
    this.dnResolver = resolver;
    if (this.dnResolver != null) {
      this.dnResolver.setAuthenticatorConfig(this);
    }
  }


  /**
   * Returns the authentication handler. Lazily initializes the authentication
   * handler to {@link BindAuthenticationHandler} if it is not set when this
   * method is invoked.
   *
   * @return  authentication handler
   */
  public AuthenticationHandler getAuthenticationHandler()
  {
    if (this.authenticationHandler == null) {
      this.authenticationHandler = new BindAuthenticationHandler(this);
    }
    return this.authenticationHandler;
  }


  /**
   * Sets the authentication handler.
   *
   * @param  handler  for performing authentication
   */
  public void setAuthenticationHandler(final AuthenticationHandler handler)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting authenticationHandler: " + handler);
    }
    this.authenticationHandler = handler;
    if (this.authenticationHandler != null) {
      this.authenticationHandler.setAuthenticatorConfig(this);
    }
  }


  /**
   * Returns the authentication result handlers.
   *
   * @return  authentication result handlers
   */
  public AuthenticationResultHandler[] getAuthenticationResultHandlers()
  {
    return this.authenticationResultHandlers;
  }


  /**
   * Sets the authentication result handlers.
   *
   * @param  arh  authentication result handlers
   */
  public void setAuthenticationResultHandlers(
    final AuthenticationResultHandler[] arh)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace(
        "setting authenticationResultHandlers: " + Arrays.toString(arh));
    }
    this.authenticationResultHandlers = arh;
  }


  /**
   * Returns the authorization handlers.
   *
   * @return  authorization handlers
   */
  public AuthorizationHandler[] getAuthorizationHandlers()
  {
    return this.authorizationHandlers;
  }


  /**
   * Sets the authorization handlers.
   *
   * @param  ah  authorization handlers
   */
  public void setAuthorizationHandlers(final AuthorizationHandler[] ah)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace(
        "setting authorizationHandlers: " + Arrays.toString(ah));
    }
    this.authorizationHandlers = ah;
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
        "%s@%d: userFilter=%s, userFilterArgs=%s, authorizationFilter=%s, " +
        "authorizationFilterArgs=%s, allowMultipleDns=%s, subtreeSearch=%s, " +
        "dnResolver=%s, authenticationHandler=%s, " +
        "authenticationResultHandlers=%s, authorizationHandlers=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.userFilter,
        this.userFilterArgs != null ? Arrays.asList(this.userFilterArgs) : null,
        this.authorizationFilter,
        this.authorizationFilterArgs != null ?
          Arrays.asList(this.authorizationFilterArgs) : null,
        this.allowMultipleDns,
        this.subtreeSearch,
        this.dnResolver,
        this.authenticationHandler,
        this.authenticationResultHandlers != null ?
          Arrays.asList(this.authenticationResultHandlers) : null,
        this.authorizationHandlers != null ?
          Arrays.asList(this.authorizationHandlers) : null);
  }
}
