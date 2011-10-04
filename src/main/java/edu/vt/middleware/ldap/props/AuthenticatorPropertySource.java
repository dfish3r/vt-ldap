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
import edu.vt.middleware.ldap.ConnectionFactoryManager;
import edu.vt.middleware.ldap.DefaultConnectionFactory;
import edu.vt.middleware.ldap.auth.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.BindAuthenticationHandler;
import edu.vt.middleware.ldap.auth.DnResolver;
import edu.vt.middleware.ldap.auth.SearchDnResolver;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;

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
    if (dnResolver instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager cfm =
        (PooledConnectionFactoryManager) dnResolver;
      if (cfm.getConnectionFactory() == null) {
        initPooledConnectionFactoryManager(cfm);
      }
    }
    if (dnResolver instanceof ConnectionFactoryManager) {
      final ConnectionFactoryManager cfm =
        (ConnectionFactoryManager) dnResolver;
      if (cfm.getConnectionFactory() == null) {
        initConnectionFactoryManager(cfm);
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
    if (authHandler instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager cfm =
        (PooledConnectionFactoryManager) authHandler;
      if (cfm.getConnectionFactory() == null) {
        initPooledConnectionFactoryManager(cfm);
      }
    }
    if (authHandler instanceof ConnectionFactoryManager) {
      final ConnectionFactoryManager cfm =
        (ConnectionFactoryManager) authHandler;
      if (cfm.getConnectionFactory() == null) {
        initConnectionFactoryManager(cfm);
      }
    }
  }


  /**
   * Initializes the supplied connection factory manager using the properties
   * in this property source.
   *
   * @param  cfm  to initialize
   */
  private void initConnectionFactoryManager(final ConnectionFactoryManager cfm)
  {
    final DefaultConnectionFactory cf = new DefaultConnectionFactory();
    final DefaultConnectionFactoryPropertySource cfPropSource =
      new DefaultConnectionFactoryPropertySource(
        cf, propertiesDomain, properties);
    cfPropSource.initialize();
    cfm.setConnectionFactory(cf);
  }


  /**
   * Initializes the supplied connection factory manager using the properties
   * in this property source.
   *
   * @param  cfm  to initialize
   */
  private void initPooledConnectionFactoryManager(
    final PooledConnectionFactoryManager cfm)
  {
    final PooledConnectionFactory cf = new PooledConnectionFactory();
    final PooledConnectionFactoryPropertySource cfPropSource =
      new PooledConnectionFactoryPropertySource(
        cf, propertiesDomain, properties);
    cfPropSource.initialize();
    cfm.setConnectionFactory(cf);
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
