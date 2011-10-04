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
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import edu.vt.middleware.ldap.pool.BlockingConnectionPool;
import edu.vt.middleware.ldap.pool.ConnectionPool;
import edu.vt.middleware.ldap.pool.ConnectionPoolType;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.pool.SoftLimitConnectionPool;

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

  /** Connection pool type. */
  private ConnectionPoolType poolType = ConnectionPoolType.BLOCKING;


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
      DefaultConnectionFactoryPropertySource.class.getResourceAsStream(
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
    this(cf, PropertyDomain.POOL, props);
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


  /**
   * Returns the pool type.
   *
   * @return  pool type
   */
  public ConnectionPoolType getPoolType()
  {
    return poolType;
  }


  /**
   *
   * @param  pt  pool type
   */
  public void setPoolType(final ConnectionPoolType pt)
  {
    poolType = pt;
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    ConnectionPool cp = null;
    if (poolType == ConnectionPoolType.BLOCKING) {
      cp = new BlockingConnectionPool();
      final BlockingConnectionPoolPropertySource cpPropSource =
        new BlockingConnectionPoolPropertySource(
          (BlockingConnectionPool) cp, propertiesDomain, properties);
      cpPropSource.initialize();
    } else if (poolType == ConnectionPoolType.SOFTLIMIT) {
      cp = new SoftLimitConnectionPool();
      final BlockingConnectionPoolPropertySource cpPropSource =
        new BlockingConnectionPoolPropertySource(
          (SoftLimitConnectionPool) cp, propertiesDomain, properties);
      cpPropSource.initialize();
    } else {
      throw new IllegalStateException("Unknown pool type: " + poolType);
    }

    cp.initialize();
    object.setConnectionPool(cp);
  }


  /**
   * Returns the property names for this property source.
   *
   * @return  all property names
   */
  public static Set<String> getProperties()
  {
    return Collections.emptySet();
  }
}
