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
import edu.vt.middleware.ldap.auth.BindAuthenticationHandler;

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
  private static final SimplePropertyInvoker INVOKER =
    new SimplePropertyInvoker(BindAuthenticationHandler.class);


  /**
   * Creates a new bind authentication handler property source using the default
   * properties file.
   *
   * @param  handler  bind authentication handler to invoke properties on
   */
  public BindAuthenticationHandlerPropertySource(
    final BindAuthenticationHandler handler)
  {
    this(
      handler,
      BindAuthenticationHandlerPropertySource.class.getResourceAsStream(
        PROPERTIES_FILE));
  }


  /**
   * Creates a new bind authentication handler property source.
   *
   * @param  handler  bind authentication handler to invoke properties on
   * @param  is  to read properties from
   */
  public BindAuthenticationHandlerPropertySource(
    final BindAuthenticationHandler handler, final InputStream is)
  {
    this(handler, loadProperties(is));
  }


  /**
   * Creates a new bind authentication handler property source.
   *
   * @param  handler  bind authentication handler to invoke properties on
   * @param  props  to read properties from
   */
  public BindAuthenticationHandlerPropertySource(
    final BindAuthenticationHandler handler, final Properties props)
  {
    this(handler, PropertyDomain.AUTH, props);
  }


  /**
   * Creates a new bind authentication handler property source.
   *
   * @param  handler  bind authentication handler to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public BindAuthenticationHandlerPropertySource(
    final BindAuthenticationHandler handler,
    final PropertyDomain domain,
    final Properties props)
  {
    object = handler;
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
