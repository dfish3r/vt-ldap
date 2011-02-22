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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides methods common to object properties implementations.
 *
 * @param  <T>  type of object to invoke properties on
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractObjectProperties<T> implements ObjectProperties<T>
{

  /** Default file to read properties from, value is {@value}. */
  public static final String PROPERTIES_FILE = "/ldap.properties";

  /** Domain to look for ldap properties in, value is {@value}. */
  public static final String BASE_PROPERTIES_DOMAIN = "edu.vt.middleware.ldap.";

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Object that is created an initialized with properties. */
  protected T object;


  /** {@inheritDoc} */
  public abstract boolean hasProperty(final String name);


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
   * @param  invoker  to set properties on the object
   * @param  object  to initialize
   * @param  domain  for properties on the object
   * @param  properties  to iterate over
   */
  protected void initializeObject(
    final PropertyInvoker invoker,
    final T object,
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
        // if we have this property, set it last
        if (this.hasProperty(name)) {
          props.put(name, value);
        // add to provider specific properties if it isn't a vt-ldap property
        } else if (!name.startsWith(BASE_PROPERTIES_DOMAIN)) {
          providerProps.put(name, value);
        } else {
          // check if this is a super class property
          // if it is, set it now, it may be overridden with the props map
          final String newName =
            domain + name.substring(BASE_PROPERTIES_DOMAIN.length());
          if (this.hasProperty(newName)) {
            invoker.setProperty(object, newName, value);
          }
        }
      }
      for (Map.Entry<String, String> entry : props.entrySet()) {
        invoker.setProperty(object, entry.getKey(), entry.getValue());
      }
      // set provider specific properties
      if (!providerProps.isEmpty()) {
        if (this.hasProperty(domain + "providerProperties")) {
          try {
            SimplePropertyInvoker.invokeMethod(
              object.getClass().getMethod(
                "setProviderProperties", Map.class),
              object,
              providerProps);
          } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
          }
        }
      }
    }
  }
}
