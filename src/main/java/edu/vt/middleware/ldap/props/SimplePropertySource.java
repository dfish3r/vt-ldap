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

/**
 * Reads simple properties and returns an initialized object of the supplied
 * type.
 *
 * @param  <T>  type of object to invoke properties on
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class SimplePropertySource<T> extends AbstractPropertySource<T>
{

  /** Invoker for simple properties. */
  private SimplePropertyInvoker invoker;


  /**
   * Creates a new simple property source using the default properties file.
   *
   * @param  t  object to invoke properties on
   */
  public SimplePropertySource(final T t)
  {
    this(t, SimplePropertySource.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * Creates a new simple property source.
   *
   * @param  t  object to invoke properties on
   * @param  is  to read properties from
   */
  public SimplePropertySource(final T t, final InputStream is)
  {
    this(t, loadProperties(is));
  }


  /**
   * Creates a new simple property source.
   *
   * @param  t  object to invoke properties on
   * @param  props  to read properties from
   */
  public SimplePropertySource(final T t, final Properties props)
  {
    this(t, PropertyDomain.LDAP, props);
  }


  /**
   * Creates a new simple property source.
   *
   * @param  t  object to invoke properties on
   * @param  domain  that properties are in
   * @param  props  to read properties from
   */
  public SimplePropertySource(
    final T t, final PropertyDomain domain, final Properties props)
  {
    super(t, domain, props);
    invoker = new SimplePropertyInvoker(t.getClass());
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    initializeObject(invoker);
  }
}
