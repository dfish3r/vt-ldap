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
import edu.vt.middleware.ldap.pool.PoolConfig;

/**
 * Reads properties specific to {@link PoolConfig} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class PoolConfigPropertySource
  extends AbstractPropertySource<PoolConfig>
{

  /** Invoker for ldap pool config. */
  private static final SimplePropertyInvoker INVOKER =
    new SimplePropertyInvoker(PoolConfig.class);


  /**
   * Creates a new ldap pool config property source using the default properties
   * file.
   *
   * @param  pc  pool config to invoke properties on
   */
  public PoolConfigPropertySource(final PoolConfig pc)
  {
    this(
      pc, PoolConfigPropertySource.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new ldap pool config property source.
   *
   * @param  pc  pool config to invoke properties on
   * @param  is  to read properties from
   */
  public PoolConfigPropertySource(final PoolConfig pc, final InputStream is)
  {
    this(pc, loadProperties(is));
  }


  /**
   * Creates a new ldap pool config property source.
   *
   * @param  pc  pool config to invoke properties on
   * @param  props  to read properties from
   */
  public PoolConfigPropertySource(final PoolConfig pc, final Properties props)
  {
    this(pc, PropertyDomain.POOL, props);
  }


  /**
   * Creates a new ldap pool config property source.
   *
   * @param  pc  pool config to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public PoolConfigPropertySource(
    final PoolConfig pc, final PropertyDomain domain, final Properties props)
  {
    super(pc, domain, props);
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    initializeObject(INVOKER);
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
