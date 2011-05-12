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
import edu.vt.middleware.ldap.auth.handler.BindAuthenticationHandler;

/**
 * Reads properties specific to {@link BindAuthenticationHandler} and returns an
 * initialized object of that type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class BindAuthenticationHandlerPropertySource
  extends AbstractPropertySource<BindAuthenticationHandler>
{

  /** Invoker for bind authentication handler. */
  private static final AdvancedPropertyInvoker INVOKER =
    new AdvancedPropertyInvoker(BindAuthenticationHandler.class);


  /**
   * Creates a new bind authentication handler property source using the default
   * properties file.
   */
  public BindAuthenticationHandlerPropertySource()
  {
    this(
      SearchDnResolverPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new bind authentication handler property source.
   *
   * @param  is  to read properties from
   */
  public BindAuthenticationHandlerPropertySource(final InputStream is)
  {
    this(loadProperties(is));
  }


  /**
   * Creates a new bind authentication handler property source.
   *
   * @param  props  to read properties from
   */
  public BindAuthenticationHandlerPropertySource(final Properties props)
  {
    this(PropertyDomain.AUTH, props);
  }


  /**
   * Creates a new bind authentication handler property source.
   *
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public BindAuthenticationHandlerPropertySource(
    final PropertyDomain domain, final Properties props)
  {
    object = initializeObject(
      INVOKER, new BindAuthenticationHandler(), domain.value(), props);
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
