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
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.LdapConstants;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.auth.handler.BindAuthenticationHandler;
import edu.vt.middleware.ldap.props.LdapConfigPropertyInvoker;
import edu.vt.middleware.ldap.props.LdapProperties;

/**
 * <code>AuthenticatorConfig</code> contains all the configuration data that the
 * <code>Authenticator</code> needs to control authentication.
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

  /** Directory user field. */
  private String[] userField = new String[] {
    LdapConstants.DEFAULT_USER_FIELD,
  };

  /** Filter for searching for the user. */
  private String userFilter;

  /** Filter arguments for searching for the user. */
  private Object[] userFilterArgs;

  /** User to authenticate. */
  private String user;

  /** Credential for authenticating user. */
  private Object credential;

  /** Filter for authorizing user. */
  private String authorizationFilter;

  /** Filter arguments for authorizing user. */
  private Object[] authorizationFilterArgs;

  /** Whether to throw an exception if multiple DNs are found. */
  private boolean allowMultipleDns = LdapConstants.DEFAULT_ALLOW_MULTIPLE_DNS;

  /** For finding LDAP DNs. */
  private DnResolver dnResolver = new SearchDnResolver(this);

  /** Handler to process authentication. */
  private AuthenticationHandler authenticationHandler =
    new BindAuthenticationHandler(this);

  /** Handlers to process authentications. */
  private AuthenticationResultHandler[] authenticationResultHandlers;

  /** Handlers to process authorization. */
  private AuthorizationHandler[] authorizationHandlers;


  /** Default constructor. */
  public AuthenticatorConfig()
  {
    this.setSearchScope(SearchScope.ONELEVEL);
  }


  /**
   * This will create a new <code>AuthenticatorConfig</code> with the supplied
   * ldap url and base Strings.
   *
   * @param  ldapUrl  <code>String</code> LDAP URL
   * @param  baseDn  <code>String</code> LDAP base DN
   */
  public AuthenticatorConfig(final String ldapUrl, final String baseDn)
  {
    this();
    this.setLdapUrl(ldapUrl);
    this.setBaseDn(baseDn);
  }


  /**
   * This returns the user field(s) of the <code>Authenticator</code>.
   *
   * @return  <code>String[]</code> - user field name(s)
   */
  public String[] getUserField()
  {
    return this.userField;
  }


  /**
   * This returns the filter used to search for the user.
   *
   * @return  <code>String</code> - filter
   */
  public String getUserFilter()
  {
    return this.userFilter;
  }


  /**
   * This returns the filter arguments used to search for the user.
   *
   * @return  <code>Object[]</code> - filter arguments
   */
  public Object[] getUserFilterArgs()
  {
    return this.userFilterArgs;
  }


  /**
   * This returns the user of the <code>Authenticator</code>.
   *
   * @return  <code>String</code> - user name
   */
  public String getUser()
  {
    return this.user;
  }


  /**
   * This returns the credential of the <code>Authenticator</code>.
   *
   * @return  <code>Object</code> - user credential
   */
  public Object getCredential()
  {
    return this.credential;
  }


  /**
   * This returns the filter used to authorize users.
   *
   * @return  <code>String</code> - filter
   */
  public String getAuthorizationFilter()
  {
    return this.authorizationFilter;
  }


  /**
   * This returns the filter arguments used to authorize users.
   *
   * @return  <code>Object[]</code> - filter arguments
   */
  public Object[] getAuthorizationFilterArgs()
  {
    return this.authorizationFilterArgs;
  }


  /**
   * This returns the constructDn of the <code>Authenticator</code>.
   *
   * @return  <code>boolean</code> - whether the DN will be constructed
   */
  public boolean getConstructDn()
  {
    return
      this.dnResolver != null &&
        this.dnResolver.getClass().isAssignableFrom(ConstructDnResolver.class);
  }


  /**
   * This returns the allowMultipleDns of the <code>Authenticator</code>.
   *
   * @return  <code>boolean</code> - whether an exception will be thrown if
   * multiple DNs are found
   */
  public boolean getAllowMultipleDns()
  {
    return this.allowMultipleDns;
  }


  /**
   * This returns the subtreeSearch of the <code>Authenticator</code>.
   *
   * @return  <code>boolean</code> - whether the DN will be searched for over
   * the entire base
   */
  public boolean getSubtreeSearch()
  {
    return SearchScope.SUBTREE == this.getSearchScope();
  }


  /**
   * This returns the DN resolver.
   *
   * @return  <code>DnResolver</code>
   */
  public DnResolver getDnResolver()
  {
    return this.dnResolver;
  }


  /**
   * This returns the authentication handler.
   *
   * @return  <code>AuthenticationHandler</code>
   */
  public AuthenticationHandler getAuthenticationHandler()
  {
    return this.authenticationHandler;
  }


  /**
   * This returns the handlers to use for processing authentications.
   *
   * @return  <code>AuthenticationResultHandler[]</code>
   */
  public AuthenticationResultHandler[] getAuthenticationResultHandlers()
  {
    return this.authenticationResultHandlers;
  }


  /**
   * This returns the handlers to use for processing authorization.
   *
   * @return  <code>AuthorizationHandler[]</code>
   */
  public AuthorizationHandler[] getAuthorizationHandlers()
  {
    return this.authorizationHandlers;
  }


  /**
   * This sets the user fields for the <code>Authenticator</code>. The user
   * field is used to lookup a user's dn.
   *
   * @param  userField  <code>String[]</code> username
   */
  public void setUserField(final String[] userField)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting userField: " + Arrays.toString(userField));
    }
    this.userField = userField;
  }


  /**
   * This sets the filter used to search for users. If not set, the user field
   * is used to build a search filter.
   *
   * @param  userFilter  <code>String</code>
   */
  public void setUserFilter(final String userFilter)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting userFilter: " + userFilter);
    }
    this.userFilter = userFilter;
  }


  /**
   * This sets the filter arguments used to search for users.
   *
   * @param  userFilterArgs  <code>Object[]</code>
   */
  public void setUserFilterArgs(final Object[] userFilterArgs)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace(
        "setting userFilterArgs: " + Arrays.toString(userFilterArgs));
    }
    this.userFilterArgs = userFilterArgs;
  }


  /**
   * This sets the username for the <code>Authenticator</code> to use for
   * authentication.
   *
   * @param  user  <code>String</code> username
   */
  public void setUser(final String user)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting user: " + user);
    }
    this.user = user;
  }

  /**
   * This sets the credential for the <code>Authenticator</code> to use for
   * authentication.
   *
   * @param  credential  <code>Object</code>
   */
  public void setCredential(final Object credential)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      if (this.getLogCredentials()) {
        this.logger.trace("setting credential: " + credential);
      } else {
        this.logger.trace("setting credential: <suppressed>");
      }
    }
    this.credential = credential;
  }


  /**
   * This sets the filter used to authorize users. If not set, no authorization
   * is performed.
   *
   * @param  authorizationFilter  <code>String</code>
   */
  public void setAuthorizationFilter(final String authorizationFilter)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting authorizationFilter: " + authorizationFilter);
    }
    this.authorizationFilter = authorizationFilter;
  }


  /**
   * This sets the filter arguments used to authorize users.
   *
   * @param  authorizationFilterArgs  <code>Object[]</code>
   */
  public void setAuthorizationFilterArgs(final Object[] authorizationFilterArgs)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace(
        "setting authorizationFilterArgs: " +
        Arrays.toString(authorizationFilterArgs));
    }
    this.authorizationFilterArgs = authorizationFilterArgs;
  }


  /**
   * This sets the constructDn for the <code>Authenticator</code>. If true, the
   * {@link #dnResolver} is set to {@link ConstructDnResolver}. If false, the
   * {@link #dnResolver} is set to {@link SearchDnResolver}.
   *
   * @param  constructDn  <code>boolean</code>
   */
  public void setConstructDn(final boolean constructDn)
  {
    if (constructDn) {
      this.setDnResolver(new ConstructDnResolver());
    } else {
      this.setDnResolver(new SearchDnResolver());
    }
  }


  /**
   * This sets the allowMultipleDns for the <code>Authentication</code>. If
   * false an exception will be thrown if {@link Authenticator#getDn(String)}
   * finds more than one DN matching it's filter. Otherwise the first DN found
   * is returned.
   *
   * @param  allowMultipleDns  <code>boolean</code>
   */
  public void setAllowMultipleDns(final boolean allowMultipleDns)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting allowMultipleDns: " + allowMultipleDns);
    }
    this.allowMultipleDns = allowMultipleDns;
  }


  /**
   * This sets the subtreeSearch for the <code>Authenticator</code>. If true,
   * the DN used for authenticating will be searched for over the entire {@link
   * LdapConfig#getBaseDn()}. Otherwise the DN will be search for in the {@link
   * LdapConfig#getBaseDn()} context.
   *
   * @param  subtreeSearch  <code>boolean</code>
   */
  public void setSubtreeSearch(final boolean subtreeSearch)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting subtreeSearch: " + subtreeSearch);
    }
    if (subtreeSearch) {
      this.setSearchScope(SearchScope.SUBTREE);
    } else {
      this.setSearchScope(SearchScope.ONELEVEL);
    }
  }


  /**
   * This sets the DN resolver.
   *
   * @param  resolver  <code>DnResolver</code>
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
   * This sets the authentication handler.
   *
   * @param  handler  <code>AuthenticationHandler</code>
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
   * This sets the handlers for processing authentications.
   *
   * @param  handlers  <code>AuthenticationResultHandler[]</code>
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
   * This sets the handlers for processing authorization.
   *
   * @param  handlers  <code>AuthorizationHandler[]</code>
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
  public void setEnvironmentProperties(final String name, final String value)
  {
    checkImmutable();
    if (name != null && value != null) {
      if (PROPERTIES.hasProperty(name)) {
        PROPERTIES.setProperty(this, name, value);
      } else {
        super.setEnvironmentProperties(name, value);
      }
    }
  }


  /** {@inheritDoc} */
  public boolean hasEnvironmentProperty(final String name)
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
    final AuthenticatorConfig authConfig = new AuthenticatorConfig();
    LdapProperties properties = null;
    if (is != null) {
      properties = new LdapProperties(authConfig, is);
    } else {
      properties = new LdapProperties(authConfig);
      properties.useDefaultPropertiesFile();
    }
    properties.configure();
    return authConfig;
  }
}
