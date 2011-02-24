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
import edu.vt.middleware.ldap.auth.AuthenticatorConfig;

/**
 * Reads properties specific to {@link Authenticator} and returns an initialized
 * object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AuthenticatorConfigPropertySource
  extends AbstractPropertySource<AuthenticatorConfig>
{

  /** Domain to look for ldap properties in, value is {@value}. */
  public static final String PROPERTIES_DOMAIN =
    BASE_PROPERTIES_DOMAIN + "auth.";

  /** Invoker for authenticator config. */
  private static final AdvancedPropertyInvoker AUTHENTICATOR_CONFIG_INVOKER =
    new AdvancedPropertyInvoker(AuthenticatorConfig.class, PROPERTIES_DOMAIN);



  /**
   * Creates a new authenticator config properties using the default properties
   * file.
   */
  public AuthenticatorConfigPropertySource()
  {
    this(
      AuthenticatorConfigPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new authenticator config properties.
   *
   * @param  is  to read properties from
   */
  public AuthenticatorConfigPropertySource(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new authenticator config properties.
   *
   * @param  props  to read properties from
   */
  public AuthenticatorConfigPropertySource(final Properties props)
  {
    this.object = new AuthenticatorConfig();
    this.initializeObject(
      AUTHENTICATOR_CONFIG_INVOKER, this.object, getDomain(), props);
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


  /** {@inheritDoc} */
  public boolean hasProperty(final String name)
  {
    return AUTHENTICATOR_CONFIG_INVOKER.hasProperty(name);
  }
}
