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
 * Reads properties specific to
 * {@link edu.vt.middleware.ldap.auth.AuthenticationRequest} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AuthenticationRequestPropertySource
  extends AbstractPropertySource<AuthenticationRequest>
{

  /** Invoker for authentication request. */
  private static final AuthenticationRequestPropertyInvoker INVOKER =
    new AuthenticationRequestPropertyInvoker(AuthenticationRequest.class);


  /**
   * Creates a new authentication request property source using the default
   * properties file.
   *
   * @param  ar  authentication request to set properties on
   */
  public AuthenticationRequestPropertySource(final AuthenticationRequest ar)
  {
    this(
      ar,
      AuthenticationRequestPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new authentication request property source.
   *
   * @param  ar  authentication request to set properties on
   * @param  is  to read properties from
   */
  public AuthenticationRequestPropertySource(
    final AuthenticationRequest ar, final InputStream is)
  {
    this(ar, loadProperties(is));
  }


  /**
   * Creates a new authentication request property source.
   *
   * @param  ar  authentication request to set properties on
   * @param  props  to read properties from
   */
  public AuthenticationRequestPropertySource(
    final AuthenticationRequest ar, final Properties props)
  {
    this(ar, PropertyDomain.AUTH, props);
  }


  /**
   * Creates a new authentication request property source.
   *
   * @param  ar  authentication request to set properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public AuthenticationRequestPropertySource(
    final AuthenticationRequest ar,
    final PropertyDomain domain,
    final Properties props)
  {
    object = ar;
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
