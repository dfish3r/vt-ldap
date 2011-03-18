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
import edu.vt.middleware.ldap.auth.AuthenticationRequest;

/**
 * Reads properties specific to {@link AuthenticationRequest} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AuthenticationRequestPropertySource
  extends AbstractPropertySource<AuthenticationRequest>
{

  /** Domain to look for ldap properties in, value is {@value}. */
  public static final String PROPERTIES_DOMAIN = "edu.vt.middleware.ldap.auth.";

  /** Invoker for authentication request. */
  private static final AdvancedPropertyInvoker AUTHENTICATION_REQUEST_INVOKER =
    new AdvancedPropertyInvoker(AuthenticationRequest.class, PROPERTIES_DOMAIN);


  /**
   * Creates a new authentication request property source using the default
   * properties file.
   */
  public AuthenticationRequestPropertySource()
  {
    this(
      AuthenticationRequestPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new authentication request property source.
   *
   * @param  is  to read properties from
   */
  public AuthenticationRequestPropertySource(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new authentication request property source.
   *
   * @param  props  to read properties from
   */
  public AuthenticationRequestPropertySource(final Properties props)
  {
    this.object = new AuthenticationRequest();
    this.initializeObject(
      AUTHENTICATION_REQUEST_INVOKER, this.object, getDomain(), props);
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
    return AUTHENTICATION_REQUEST_INVOKER.getProperties();
  }


  /** {@inheritDoc} */
  public boolean hasProperty(final String name)
  {
    return AUTHENTICATION_REQUEST_INVOKER.hasProperty(name);
  }
}
