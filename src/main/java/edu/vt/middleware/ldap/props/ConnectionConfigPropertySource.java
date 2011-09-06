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

/**
 * Reads properties specific to {@link ConnectionConfig} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class ConnectionConfigPropertySource
  extends AbstractPropertySource<ConnectionConfig>
{

  /** Invoker for connection config. */
  private static final ConnectionConfigPropertyInvoker INVOKER =
    new ConnectionConfigPropertyInvoker(ConnectionConfig.class);


  /**
   * Creates a new connection config property source using the default
   * properties file.
   *
   * @param  cc  connection config to invoke properties on
   */
  public ConnectionConfigPropertySource(final ConnectionConfig cc)
  {
    this(
      cc,
      ConnectionConfigPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new connection config property source.
   *
   * @param  cc  connection config to invoke properties on
   * @param  is  to read properties from
   */
  public ConnectionConfigPropertySource(
    final ConnectionConfig cc, final InputStream is)
  {
    this(cc, loadProperties(is));
  }


  /**
   * Creates a new connection config property source.
   *
   * @param  cc  connection config to invoke properties on
   * @param  props  to read properties from
   */
  public ConnectionConfigPropertySource(
    final ConnectionConfig cc, final Properties props)
  {
    this(cc, PropertyDomain.LDAP, props);
  }


  /**
   * Creates a new connection config property source.
   *
   * @param  cc  connection config to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public ConnectionConfigPropertySource(
    final ConnectionConfig cc,
    final PropertyDomain domain,
    final Properties props)
  {
    object = cc;
    propertiesDomain = domain;
    properties = props;
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
