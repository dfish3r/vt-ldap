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

import java.io.InputStream;
import java.util.Arrays;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.SearchScope;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.auth.handler.BindAuthenticationHandler;
import edu.vt.middleware.ldap.props.LdapConfigPropertyInvoker;
import edu.vt.middleware.ldap.props.LdapProperties;

/**
 * Contains all the configuration data needed to control authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticatorConfig extends LdapConfig
{

  /** Domain to look for ldap properties in, value is {@value}. */
  public static final String PROPERTIES_DOMAIN = "edu.vt.middleware.ldap.auth.";

  /** Invoker for ldap properties. */
  private static final LdapConfigPropertyInvoker PROPERTIES =
    new LdapConfigPropertyInvoker(AuthenticatorConfig.class, PROPERTIES_DOMAIN);

  /** Filter for searching for the user. */
  private String userFilter;

  /** Filter arguments for searching for the user. */
  private Object[] userFilterArgs;

  /** User to authenticate. */
  private String user;

  /** Credential for authenticating user. */
  private Credential credential;

  /** User attributes to return after successful authentication. Default value
      returns no attributes. */
  private String[] returnAttributes = new String[0];

  /** Filter for authorizing user. */
  private String authorizationFilter;

  /** Filter arguments for authorizing user. */
  private Object[] authorizationFilterArgs;

  /** Whether to throw an exception if multiple DNs are found. */
  private boolean allowMultipleDns;

  /** For finding user DNs. */
  private DnResolver dnResolver;

  /** Handler to process authentication. */
  private AuthenticationHandler authenticationHandler;

  /** Handlers to process authentication results. */
  private AuthenticationResultHandler[] authenticationResultHandlers;

  /** Handlers to process authorization. */
  private AuthorizationHandler[] authorizationHandlers;


  /** Default constructor. */
  public AuthenticatorConfig()
  {
    this.setSearchScope(SearchScope.ONELEVEL);
  }


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
   * Creates a new auth config.
   *
   * @param  ldapUrl  to connect to
   * @param  baseDn  to search
   */
  public AuthenticatorConfig(final String ldapUrl, final String baseDn)
  {
    this();
    this.setLdapUrl(ldapUrl);
    this.setBaseDn(baseDn);
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
   * Returns the user to authenticate.
   *
   * @return  user name
   */
  public String getUser()
  {
    return this.user;
  }


  /**
   * Sets the user to authenticate.
   *
   * @param  s  user name
   */
  public void setUser(final String s)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting user: " + s);
    }
    this.user = s;
  }


  /**
   * This returns the credential to authenticate.
   *
   * @return  user credential
   */
  public Credential getCredential()
  {
    return this.credential;
  }


  /**
   * Sets the credential to authenticate.
   *
   * @param  c  user credential
   */
  public void setCredential(final Credential c)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      if (this.getLogCredentials()) {
        this.logger.trace("setting credential: " + c);
      } else {
        this.logger.trace("setting credential: <suppressed>");
      }
    }
    this.credential = c;
  }


  /**
   * Returns the attributes that should be returned after authentication.
   *
   * @return  attribute names
   */
  public String[] getReturnAttributes()
  {
    return this.returnAttributes;
  }


  /**
   * Sets the attributes that should be returned after authentication.
   *
   * @param  s  attribute names
   */
  public void setReturnAttributes(final String[] s)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting returnAttributes: " + Arrays.toString(s));
    }
    this.returnAttributes = s;
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
    return SearchScope.SUBTREE == this.getSearchScope();
  }


  /**
   * Sets whether subtree searching will be used. If true, the DN used for
   * authenticating will be searched for over the entire {@link
   * LdapConfig#getBaseDn()}. Otherwise the DN will be search for in the {@link
   * LdapConfig#getBaseDn()} context.
   *
   * @param  b  whether the DN will be searched for over the entire base
   */
  public void setSubtreeSearch(final boolean b)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting subtreeSearch: " + b);
    }
    if (b) {
      this.setSearchScope(SearchScope.SUBTREE);
    } else {
      this.setSearchScope(SearchScope.ONELEVEL);
    }
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
   * Returns the handlers to use for processing authentication results.
   *
   * @return  authentication result handlers
   */
  public AuthenticationResultHandler[] getAuthenticationResultHandlers()
  {
    return this.authenticationResultHandlers;
  }


  /**
   * Sets the handlers for processing authentication results.
   *
   * @param  handlers  for processing authentication results
   */
  public void setAuthenticationResultHandlers(
    final AuthenticationResultHandler[] handlers)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting authenticationResultHandlers: " + handlers);
    }
    this.authenticationResultHandlers = handlers;
  }


  /**
   * Returns the handlers to use for processing authorization.
   *
   * @return  authorization handlers
   */
  public AuthorizationHandler[] getAuthorizationHandlers()
  {
    return this.authorizationHandlers;
  }


  /**
   * Sets the handlers for processing authorization.
   *
   * @param  handlers  for processing authorization
   */
  public void setAuthorizationHandlers(final AuthorizationHandler[] handlers)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting authorizationHandlers: " + handlers);
    }
    this.authorizationHandlers = handlers;
  }


  /** {@inheritDoc} */
  public String getPropertiesDomain()
  {
    return PROPERTIES_DOMAIN;
  }


  /** {@inheritDoc} */
  public void setProviderProperty(final String name, final String value)
  {
    checkImmutable();
    if (name != null && value != null) {
      if (PROPERTIES.hasProperty(name)) {
        PROPERTIES.setProperty(this, name, value);
      } else {
        super.setProviderProperty(name, value);
      }
    }
  }


  /** {@inheritDoc} */
  public boolean hasProviderProperty(final String name)
  {
    return PROPERTIES.hasProperty(name);
  }


  /**
   * Create an instance of this class initialized with properties from the input
   * stream. If the input stream is null, load properties from the default
   * properties file.
   *
   * @param  is  to load properties from
   *
   * @return  <code>AuthenticatorConfig</code> initialized ldap pool config
   */
  public static AuthenticatorConfig createFromProperties(final InputStream is)
  {
    final AuthenticatorConfig authenticatorConfig = new AuthenticatorConfig();
    LdapProperties properties = null;
    if (is != null) {
      properties = new LdapProperties(authenticatorConfig, is);
    } else {
      properties = new LdapProperties(authenticatorConfig);
      properties.useDefaultPropertiesFile();
    }
    properties.configure();
    return authenticatorConfig;
  }
}
