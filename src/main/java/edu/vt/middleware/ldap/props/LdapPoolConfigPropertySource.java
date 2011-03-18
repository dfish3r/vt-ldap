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
import edu.vt.middleware.ldap.pool.LdapPoolConfig;

/**
 * Reads properties specific to {@link LdapPoolConfig} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class LdapPoolConfigPropertySource
  extends AbstractPropertySource<LdapPoolConfig>
{

  /** Domain to look for ldap properties in, value is {@value}. */
  public static final String PROPERTIES_DOMAIN =
    BASE_PROPERTIES_DOMAIN + "pool.";

  /** Invoker for ldap pool config. */
  private static final SimplePropertyInvoker POOL_CONFIG_INVOKER =
    new SimplePropertyInvoker(LdapPoolConfig.class, PROPERTIES_DOMAIN);


  /**
   * Creates a new ldap pool config property source using the default properties
   * file.
   */
  public LdapPoolConfigPropertySource()
  {
    this(
      LdapPoolConfigPropertySource.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new ldap pool config property source.
   *
   * @param  is  to read properties from
   */
  public LdapPoolConfigPropertySource(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new ldap pool config property source.
   *
   * @param  props  to read properties from
   */
  public LdapPoolConfigPropertySource(final Properties props)
  {
    this.object = new LdapPoolConfig();
    this.initializeObject(
      POOL_CONFIG_INVOKER, this.object, getDomain(), props);
  }


  /**
   * Returns the properties domain for this invoker.
   *
   * @return  properties domain
   */
  public static String getDomain()
  {
    return PROPERTIES_DOMAIN;
  }


  /**
   * Returns the property names for this object.
   *
   * @return  all property names
   */
  public static Set<String> getProperties()
  {
    return POOL_CONFIG_INVOKER.getProperties();
  }


  /** {@inheritDoc} */
  public boolean hasProperty(final String name)
  {
    return POOL_CONFIG_INVOKER.hasProperty(name);
  }
}
