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
import edu.vt.middleware.ldap.pool.PoolConfig;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.provider.ProviderConfig;

/**
 * Reads properties specific to {@link PooledConnectionFactory} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class PooledConnectionFactoryPropertySource
  extends AbstractPropertySource<PooledConnectionFactory>
{

  /** Invoker for connection factory. */
  private static final PooledConnectionFactoryPropertyInvoker INVOKER =
    new PooledConnectionFactoryPropertyInvoker(PooledConnectionFactory.class);


  /**
   * Creates a new pooled connection factory property source using the default
   * properties file.
   *
   * @param  cf  connection factory to invoke properties on
   */
  public PooledConnectionFactoryPropertySource(final PooledConnectionFactory cf)
  {
    this(
      cf,
      ConnectionFactoryPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new pooled connection factory property source.
   *
   * @param  cf  connection factory to invoke properties on
   * @param  is  to read properties from
   */
  public PooledConnectionFactoryPropertySource(
    final PooledConnectionFactory cf, final InputStream is)
  {
    this(cf, loadProperties(is));
  }


  /**
   * Creates a new pooled connection factory property source.
   *
   * @param  cf  connection factory to invoke properties on
   * @param  props  to read properties from
   */
  public PooledConnectionFactoryPropertySource(
    final PooledConnectionFactory cf, final Properties props)
  {
    this(cf, PropertyDomain.LDAP, props);
  }


  /**
   * Creates a new pooled connection factory property source.
   *
   * @param  cf  connection factory to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public PooledConnectionFactoryPropertySource(
    final PooledConnectionFactory cf,
    final PropertyDomain domain,
    final Properties props)
  {
    object = cf;
    propertiesDomain = domain;
    properties = props;
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    initializeObject(INVOKER);

    final ConnectionConfig cc = new ConnectionConfig();
    final ConnectionConfigPropertySource ccPropSource =
      new ConnectionConfigPropertySource(cc, propertiesDomain, properties);
    ccPropSource.initialize();
    object.setConnectionConfig(cc);

    final ProviderConfig providerConfig = new ProviderConfig();
    final ProviderConfigPropertySource providerPropSource =
      new ProviderConfigPropertySource(
        providerConfig, propertiesDomain, properties);
    providerPropSource.initialize();
    object.getProvider().getProviderConfig().setConnectionStrategy(
      providerConfig.getConnectionStrategy());
    object.getProvider().getProviderConfig().setLogCredentials(
      providerConfig.getLogCredentials());
    object.getProvider().getProviderConfig().setOperationRetryResultCodes(
      providerConfig.getOperationRetryResultCodes());
    object.getProvider().getProviderConfig().setProperties(extraProps);

    final PoolConfig poolConfig = new PoolConfig();
    final PoolConfigPropertySource poolPropSource =
      new PoolConfigPropertySource(poolConfig, propertiesDomain, properties);
    poolPropSource.initialize();
    object.setPoolConfig(poolConfig);
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
