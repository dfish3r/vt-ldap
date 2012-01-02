/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
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
import edu.vt.middleware.ldap.DefaultConnectionFactory;
import edu.vt.middleware.ldap.pool.BlockingConnectionPool;
import edu.vt.middleware.ldap.pool.PoolConfig;

/**
 * Reads properties specific to {@link BlockingConnectionPool} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class BlockingConnectionPoolPropertySource
  extends AbstractPropertySource<BlockingConnectionPool>
{

  /** Invoker for connection factory. */
  private static final SimplePropertyInvoker INVOKER =
    new SimplePropertyInvoker(BlockingConnectionPool.class);


  /**
   * Creates a new blocking connection pool property source using the default
   * properties file.
   *
   * @param  cp  connection pool to invoke properties on
   */
  public BlockingConnectionPoolPropertySource(final BlockingConnectionPool cp)
  {
    this(
      cp,
      BlockingConnectionPoolPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new blocking connection pool property source.
   *
   * @param  cp  connection pool to invoke properties on
   * @param  is  to read properties from
   */
  public BlockingConnectionPoolPropertySource(
    final BlockingConnectionPool cp,
    final InputStream is)
  {
    this(cp, loadProperties(is));
  }


  /**
   * Creates a new blocking connection pool property source.
   *
   * @param  cp  connection pool to invoke properties on
   * @param  props  to read properties from
   */
  public BlockingConnectionPoolPropertySource(
    final BlockingConnectionPool cp,
    final Properties props)
  {
    this(cp, PropertyDomain.POOL, props);
  }


  /**
   * Creates a new blocking connection pool property source.
   *
   * @param  cp  connection pool to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public BlockingConnectionPoolPropertySource(
    final BlockingConnectionPool cp,
    final PropertyDomain domain,
    final Properties props)
  {
    super(cp, domain, props);
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    initializeObject(INVOKER);

    DefaultConnectionFactory cf = object.getConnectionFactory();
    if (cf == null) {
      cf = new DefaultConnectionFactory();

      final DefaultConnectionFactoryPropertySource cfPropSource =
        new DefaultConnectionFactoryPropertySource(
          (DefaultConnectionFactory) cf,
          propertiesDomain,
          properties);
      cfPropSource.initialize();
      object.setConnectionFactory(cf);
    }

    PoolConfig pc = object.getPoolConfig();
    if (pc == null) {
      pc = new PoolConfig();

      final PoolConfigPropertySource pcPropSource =
        new PoolConfigPropertySource(pc, propertiesDomain, properties);
      pcPropSource.initialize();
      object.setPoolConfig(pc);
    } else {
      final SimplePropertySource<PoolConfig> sPropSource =
        new SimplePropertySource<PoolConfig>(pc, propertiesDomain, properties);
      sPropSource.initialize();
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
