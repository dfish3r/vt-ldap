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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses the configuration data associated with classes that contain setter
 * properties. The format of the property string should be like:
 *
 * <pre>
   MyClass{{propertyOne=foo}{propertyTwo=bar}}
 * </pre>
 *
 * If the class name is supplied to the constructor, the property string need
 * not contain the class declaration.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PropertyValueParser
{

  /** Property string containing configuration. */
  private static final Pattern CONFIG_PATTERN = Pattern.compile(
    "([^\\{]+)\\s*\\{(.*)\\}\\s*");

  /** Property string for configuring a config where the class is known. */
  private static final Pattern PARAMS_ONLY_CONFIG_PATTERN = Pattern.compile(
    "\\s*\\{\\s*(.*)\\s*\\}\\s*");

  /** Pattern for finding properties. */
  private static final Pattern PROPERTY_PATTERN = Pattern.compile(
    "([^\\}\\{])+");

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Class found in the config. */
  private String className;

  /** Properties found in the config to set on the class. */
  private Map<String, String> properties = new HashMap<String, String>();


  /**
   * Creates a new config parser.
   *
   * @param  config  containing configuration data
   */
  public PropertyValueParser(final String config)
  {
    final Matcher matcher = CONFIG_PATTERN.matcher(config);
    if (matcher.matches()) {
      className = matcher.group(1).trim();

      final String props = matcher.group(2).trim();
      final Matcher m = PROPERTY_PATTERN.matcher(props);
      while (m.find()) {
        final String input = m.group().trim();
        if (input != null && !"".equals(input)) {
          final String[] s = input.split("=", 2);
          properties.put(s[0].trim(), s[1].trim());
        }
      }
    }
  }


  /**
   * Creates a new config parser.
   *
   * @param  config  containing configuration data
   * @param  clazz  fully qualified class name
   */
  public PropertyValueParser(final String config, final String clazz)
  {
    final Matcher matcher = PARAMS_ONLY_CONFIG_PATTERN.matcher(config);
    className = clazz;
    if (matcher.matches()) {
      final String props = matcher.group(1).trim();
      final Matcher m = PROPERTY_PATTERN.matcher(props);
      while (m.find()) {
        final String input = m.group().trim();
        if (input != null && !"".equals(input)) {
          final String[] s = input.split("=", 2);
          properties.put(s[0].trim(), s[1].trim());
        }
      }
    }
  }


  /**
   * Returns the class name from the configuration.
   *
   * @return  class name
   */
  public String getClassName()
  {
    return className;
  }


  /**
   * Returns the properties from the configuration.
   *
   * @return  map of property name to value
   */
  public Map<String, String> getProperties()
  {
    return properties;
  }


  /**
   * Returns whether the supplied configuration data contains a config.
   *
   * @param  config  containing configuration data
   *
   * @return  whether the supplied configuration data contains a config
   */
  public static boolean isConfig(final String config)
  {
    return CONFIG_PATTERN.matcher(config).matches();
  }


  /**
   * Returns whether the supplied configuration data contains a params only
   * config.
   *
   * @param  config  containing configuration data
   *
   * @return  whether the supplied configuration data contains a params only
   * config
   */
  public static boolean isParamsOnlyConfig(final String config)
  {
    return PARAMS_ONLY_CONFIG_PATTERN.matcher(config).matches();
  }


  /**
   * Initialize an instance of the class type with the properties contained in
   * this config.
   *
   * @return  object of the type the config parsed
   */
  public Object initializeType()
  {
    final Class<?> c = SimplePropertyInvoker.createClass(getClassName());
    final Object o = SimplePropertyInvoker.instantiateType(
      c,
      getClassName());
    setProperties(c, o);
    return o;
  }


  /**
   * Sets the properties on the supplied object.
   *
   * @param  c  type of the supplied object
   * @param  o  to invoke properties on
   */
  protected void setProperties(final Class<?> c, final Object o)
  {
    final SimplePropertyInvoker invoker = new SimplePropertyInvoker(c);
    for (Map.Entry<String, String> entry : getProperties().entrySet()) {
      invoker.setProperty(o, entry.getKey(), entry.getValue());
    }
    if (invoker.getProperties().contains("initialize")) {
      try {
        invoker.setProperty(o, "initialize", null);
      } catch (Throwable t) {
        logger.debug("Error invoking initialize method", t);
      }
    }
  }
}
