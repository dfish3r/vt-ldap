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
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.provider.ProviderConfig;

/**
 * Reads properties specific to
 * {@link edu.vt.middleware.ldap.DefaultConnectionFactory} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class DefaultConnectionFactoryPropertySource
  extends AbstractPropertySource<ConnectionFactory>
{

  /** Invoker for connection factory. */
  private static final DefaultConnectionFactoryPropertyInvoker INVOKER =
    new DefaultConnectionFactoryPropertyInvoker(ConnectionFactory.class);


  /**
   * Creates a new connection factory property source using the default
   * properties file.
   *
   * @param  cf  connection factory to invoke properties on
   */
  public DefaultConnectionFactoryPropertySource(final ConnectionFactory cf)
  {
    this(
      cf,
      DefaultConnectionFactoryPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new connection factory property source.
   *
   * @param  cf  connection factory to invoke properties on
   * @param  is  to read properties from
   */
  public DefaultConnectionFactoryPropertySource(
    final ConnectionFactory cf, final InputStream is)
  {
    this(cf, loadProperties(is));
  }


  /**
   * Creates a new connection factory property source.
   *
   * @param  cf  connection factory to invoke properties on
   * @param  props  to read properties from
   */
  public DefaultConnectionFactoryPropertySource(
    final ConnectionFactory cf, final Properties props)
  {
    this(cf, PropertyDomain.LDAP, props);
  }


  /**
   * Creates a new connection factory property source.
   *
   * @param  cf  connection factory to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public DefaultConnectionFactoryPropertySource(
    final ConnectionFactory cf,
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

    final ProviderConfig pc = new ProviderConfig();
    final ProviderConfigPropertySource pcPropSource =
      new ProviderConfigPropertySource(pc, propertiesDomain, properties);
    pcPropSource.initialize();
    object.getProvider().getProviderConfig().setConnectionStrategy(
      pc.getConnectionStrategy());
    object.getProvider().getProviderConfig().setLogCredentials(
      pc.getLogCredentials());
    object.getProvider().getProviderConfig().setOperationRetryResultCodes(
      pc.getOperationRetryResultCodes());
    object.getProvider().getProviderConfig().setProperties(extraProps);
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
