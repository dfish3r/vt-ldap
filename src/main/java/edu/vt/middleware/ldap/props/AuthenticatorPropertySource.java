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
package edu.vt.middleware.ldap.props;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.DnResolver;
import edu.vt.middleware.ldap.auth.ManagedDnResolver;
import edu.vt.middleware.ldap.auth.SearchDnResolver;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.BindAuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.ManagedAuthenticationHandler;

/**
 * Reads properties specific to
 * {@link edu.vt.middleware.ldap.auth.Authenticator} and returns an initialized
 * object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AuthenticatorPropertySource
  extends AbstractPropertySource<Authenticator>
{

  /** Invoker for authenticator. */
  private static final AuthenticatorPropertyInvoker INVOKER =
    new AuthenticatorPropertyInvoker(Authenticator.class);


  /**
   * Creates a new authenticator property source using the default properties
   * file.
   *
   * @param  a  authenticator to set properties on
   */
  public AuthenticatorPropertySource(final Authenticator a)
  {
    this(
      a,
      AuthenticatorPropertySource.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new authenticator property source.
   *
   * @param  a  authenticator to set properties on
   * @param  is  to read properties from
   */
  public AuthenticatorPropertySource(
    final Authenticator a, final InputStream is)
  {
    this(a, loadProperties(is));
  }


  /**
   * Creates a new authenticator property source.
   *
   * @param  a  authenticator to set properties on
   * @param  props  to read properties from
   */
  public AuthenticatorPropertySource(
    final Authenticator a, final Properties props)
  {
    this(a, PropertyDomain.AUTH, props);
  }


  /**
   * Creates a new authenticator property source.
   *
   * @param  a  authenticator to set properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public AuthenticatorPropertySource(
    final Authenticator a, final PropertyDomain domain, final Properties props)
  {
    object = a;
    propertiesDomain = domain;
    properties = props;
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    initializeObject(INVOKER);

    ConnectionConfig connConfig = null;

    // initialize a SearchDnResolver by default
    DnResolver dnResolver = object.getDnResolver();
    if (dnResolver == null) {
      dnResolver = new SearchDnResolver();
      final SearchDnResolverPropertySource dnPropSource =
        new SearchDnResolverPropertySource(
          (SearchDnResolver) dnResolver, propertiesDomain, properties);
      dnPropSource.initialize();
      object.setDnResolver(dnResolver);
    } else {
      final SimplePropertySource<DnResolver> sPropSource =
        new SimplePropertySource<DnResolver>(
          dnResolver, propertiesDomain, properties);
      sPropSource.initialize();
    }
    if (dnResolver instanceof SearchDnResolver) {
      final SearchDnResolver sdr = (SearchDnResolver) dnResolver;
      if (sdr.getConnectionConfig() == null) {
        connConfig = new ConnectionConfig();
        final ConnectionConfigPropertySource ccPropSource =
          new ConnectionConfigPropertySource(
            connConfig, propertiesDomain, properties);
        ccPropSource.initialize();
        sdr.setConnectionConfig(connConfig);
      }
    }
    if (dnResolver instanceof ManagedDnResolver) {
      final ManagedDnResolver mdr = (ManagedDnResolver) dnResolver;
      try {
        mdr.initialize();
      } catch (LdapException e) {
        logger.error("Failed to initialize managed dn resolver", e);
      }
    }

    // initialize a BindAuthenticationHandler by default
    AuthenticationHandler authHandler = object.getAuthenticationHandler();
    if (authHandler == null) {
      authHandler = new BindAuthenticationHandler();
      final BindAuthenticationHandlerPropertySource ahPropSource =
        new BindAuthenticationHandlerPropertySource(
          (BindAuthenticationHandler) authHandler,
          propertiesDomain,
          properties);
      ahPropSource.initialize();
      object.setAuthenticationHandler(authHandler);
    } else {
      final SimplePropertySource<AuthenticationHandler> sPropSource =
        new SimplePropertySource<AuthenticationHandler>(
          authHandler, propertiesDomain, properties);
      sPropSource.initialize();
    }
    if (authHandler.getConnectionConfig() == null) {
      if (connConfig == null) {
        connConfig = new ConnectionConfig();
        final ConnectionConfigPropertySource ccPropSource =
          new ConnectionConfigPropertySource(
            connConfig, propertiesDomain, properties);
        ccPropSource.initialize();
      }
      authHandler.setConnectionConfig(connConfig);
    }
    if (authHandler instanceof ManagedAuthenticationHandler) {
      final ManagedAuthenticationHandler mah =
        (ManagedAuthenticationHandler) authHandler;
      try {
        mah.initialize();
      } catch (LdapException e) {
        logger.error("Failed to initialize managed authentication handler", e);
      }
    }
  }


  /**
   * Returns the property names for this property source.
   *
   * @return  all property names
   */
  public static Set<String> getProperties()
  {
    return INVOKER.getProperties();
  }
}
