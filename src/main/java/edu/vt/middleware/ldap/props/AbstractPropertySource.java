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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides methods common to object properties implementations.
 *
 * @param  <T>  type of object to invoke properties on
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractPropertySource<T> implements PropertySource<T>
{

  /** Default file to read properties from, value is {@value}. */
  public static final String PROPERTIES_FILE = "/ldap.properties";

  /** Object that is created an initialized with properties. */
  protected T object;


  /**
   * Creates properties from the supplied input stream. See
   * {@link Properties#load(InputStream)}.
   *
   * @param  is  input stream to read properties from
   *
   * @return  initialized properties object.
   */
  protected static Properties loadProperties(final InputStream is)
  {
    try {
      try {
        final Properties properties = new Properties();
        properties.load(is);
        return properties;
      } finally {
        is.close();
      }
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }


  /**
   * Iterates over the properties and uses the invoker to set those properties
   * on the supplied object. Any properties that do not belong to the object are
   * passed to a method called 'setProviderProperties(Map)', if one exists on
   * that object.
   *
   * @param  <T>  type of object to invoke properties on
   *
   * @param  invoker  to set properties on the object
   * @param  obj  to initialize
   * @param  domain  for properties on the object
   * @param  properties  to iterate over
   * @return  initialized object
   */
  protected static <T> T initializeObject(
    final PropertyInvoker invoker,
    final T obj,
    final String domain,
    final Properties properties)
  {
    final Map<String, String> props = new HashMap<String, String>();
    final Map<String, String> providerProps = new HashMap<String, String>();
    final Enumeration<?> en = properties.keys();
    if (en != null) {
      while (en.hasMoreElements()) {
        final String name = (String) en.nextElement();
        final String value = (String) properties.get(name);
        // add to provider specific properties if it isn't a vt-ldap property
        if (!name.startsWith(PropertyDomain.LDAP.value())) {
          providerProps.put(name, value);
        } else {
          // strip out the method name
          final int split = name.lastIndexOf('.') + 1;
          final String propName = name.substring(split);
          final String propDomain = name.substring(0, split);
          // if we have this property, set it last
          if (domain.equals(propDomain)) {
            if (invoker.hasProperty(propName)) {
              props.put(propName, value);
            }
          // check if this is a super class property
          // if it is, set it now, it may be overridden with the props map
          } else if (domain.startsWith(propDomain)) {
            if (invoker.hasProperty(propName)) {
              invoker.setProperty(obj, propName, value);
            }
          }
        }
      }
      for (Map.Entry<String, String> entry : props.entrySet()) {
        invoker.setProperty(obj, entry.getKey(), entry.getValue());
      }
      // set provider specific properties
      if (!providerProps.isEmpty() &&
          invoker.hasProperty("providerProperties")) {
        try {
          SimplePropertyInvoker.invokeMethod(
            obj.getClass().getMethod(
              "setProviderProperties", Map.class),
            obj,
            providerProps);
        } catch (NoSuchMethodException e) {
          throw new IllegalArgumentException(e);
        }
      }
    }
    return obj;
  }


  /** {@inheritDoc} */
  @Override
  public T get()
  {
    return object;
  }
}
