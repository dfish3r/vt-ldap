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
import edu.vt.middleware.ldap.provider.ProviderConfig;

/**
 * Reads properties specific to {@link ProviderConfig} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class ProviderConfigPropertySource
  extends AbstractPropertySource<ProviderConfig>
{

  /** Invoker for provider config. */
  private static final SimplePropertyInvoker INVOKER =
    new SimplePropertyInvoker(ProviderConfig.class);


  /**
   * Creates a new provider config property source using the default
   * properties file.
   *
   * @param  pc  provider config to invoke properties on
   */
  public ProviderConfigPropertySource(final ProviderConfig pc)
  {
    this(
      pc,
      ProviderConfigPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new provider config property source.
   *
   * @param  pc  provider config to invoke properties on
   * @param  is  to read properties from
   */
  public ProviderConfigPropertySource(
    final ProviderConfig pc, final InputStream is)
  {
    this(pc, loadProperties(is));
  }


  /**
   * Creates a new provider config property source.
   *
   * @param  pc  provider config to invoke properties on
   * @param  props  to read properties from
   */
  public ProviderConfigPropertySource(
    final ProviderConfig pc, final Properties props)
  {
    this(pc, PropertyDomain.LDAP, props);
  }


  /**
   * Creates a new provider config property source.
   *
   * @param  pc  provider config to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public ProviderConfigPropertySource(
    final ProviderConfig pc,
    final PropertyDomain domain,
    final Properties props)
  {
    super(pc, domain, props);
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    initializeObject(INVOKER);
    object.setProperties(extraProps);
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
