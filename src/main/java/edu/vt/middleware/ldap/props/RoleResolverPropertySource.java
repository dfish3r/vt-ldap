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
import edu.vt.middleware.ldap.jaas.RoleResolver;

/**
 * Reads properties specific to
 * {@link edu.vt.middleware.ldap.jaas.RoleResolver} and returns an initialized
 * object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class RoleResolverPropertySource
  extends AbstractPropertySource<RoleResolver>
{

  /** Invoker for role resolver. */
  private static final SimplePropertyInvoker INVOKER =
    new SimplePropertyInvoker(RoleResolver.class);


  /**
   * Creates a new role resolver property source using the default properties
   * file.
   *
   * @param  rr  role resolver to set properties on
   */
  public RoleResolverPropertySource(final RoleResolver rr)
  {
    this(
      rr,
      RoleResolverPropertySource.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new role resolver property source.
   *
   * @param  rr  role resolver to set properties on
   * @param  is  to read properties from
   */
  public RoleResolverPropertySource(final RoleResolver rr, final InputStream is)
  {
    this(rr, loadProperties(is));
  }


  /**
   * Creates a new role resolver property source.
   *
   * @param  rr  role resolver to set properties on
   * @param  props  to read properties from
   */
  public RoleResolverPropertySource(
    final RoleResolver rr, final Properties props)
  {
    this(rr, PropertyDomain.AUTH, props);
  }


  /**
   * Creates a new role resolver property source.
   *
   * @param  rr  role resolver to set properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public RoleResolverPropertySource(
    final RoleResolver rr, final PropertyDomain domain, final Properties props)
  {
    object = rr;
    propertiesDomain = domain;
    properties = props;
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    initializeObject(INVOKER);

    final ConnectionConfig connConfig = new ConnectionConfig();
    final ConnectionConfigPropertySource ccPropSource =
      new ConnectionConfigPropertySource(
        connConfig, propertiesDomain, properties);
    ccPropSource.initialize();
    object.setConnectionConfig(connConfig);

    object.initialize();
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
