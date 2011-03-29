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
import edu.vt.middleware.ldap.LdapConnectionConfig;

/**
 * Reads properties specific to {@link LdapConnectionConfig} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class LdapConnectionConfigPropertySource
  extends AbstractPropertySource<LdapConnectionConfig>
{

  /** Invoker for ldap connection config. */
  private static final AdvancedPropertyInvoker INVOKER =
    new AdvancedPropertyInvoker(LdapConnectionConfig.class);


  /**
   * Creates a new ldap connection config property source using the default
   * properties file.
   */
  public LdapConnectionConfigPropertySource()
  {
    this(
      LdapConnectionConfigPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new ldap connection config property source.
   *
   * @param  is  to read properties from
   */
  public LdapConnectionConfigPropertySource(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new ldap connection config property source.
   *
   * @param  props  to read properties from
   */
  public LdapConnectionConfigPropertySource(final Properties props)
  {
    this(PropertyDomain.LDAP, props);
  }


  /**
   * Creates a new ldap connection config property source.
   *
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public LdapConnectionConfigPropertySource(
    final PropertyDomain domain, final Properties props)
  {
    this.object = initializeObject(
      INVOKER, new LdapConnectionConfig(), domain.value(), props);
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
