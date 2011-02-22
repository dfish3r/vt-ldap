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

/**
 * Parses the configuration data associated with classes that contain setter
 * properties. The format of the property string should be like:
 *
 * <pre>
   MyClass{{propertyOne=foo}{propertyTwo=bar}}
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PropertyValueParser
{

  /** Property string containing configuration. */
  private static final Pattern CONFIG_PATTERN = Pattern.compile(
    "([^\\{]+)\\s*\\{(.*)\\}\\s*");

  /** Pattern for finding properties. */
  private static final Pattern PROPERTY_PATTERN = Pattern.compile(
    "([^\\}\\{])+");

  /** Class found in the config. */
  private String className;

  /** Properties found in the config to set on the class. */
  private Map<String, String> properties = new HashMap<String, String>();


  /**
   * Creates a new <code>ConfigParser</code> with the supplied configuration
   * string.
   *
   * @param  config  <code>String</code>
   */
  public PropertyValueParser(final String config)
  {
    final Matcher matcher = CONFIG_PATTERN.matcher(config);
    if (matcher.matches()) {
      this.className = matcher.group(1).trim();

      final String props = matcher.group(2).trim();
      final Matcher m = PROPERTY_PATTERN.matcher(props);
      while (m.find()) {
        final String input = m.group().trim();
        if (input != null && !"".equals(input)) {
          final String[] s = input.split("=");
          this.properties.put(s[0].trim(), s[1].trim());
        }
      }
    }
  }


  /**
   * Returns the class name from the configuration.
   *
   * @return  <code>String</code> class name
   */
  public String getClassName()
  {
    return this.className;
  }


  /**
   * Returns the properties from the configuration.
   *
   * @return  <code>Map</code> of property name to value
   */
  public Map<String, String> getProperties()
  {
    return this.properties;
  }


  /**
   * Returns whether the supplied configuration data contains a config.
   *
   * @param  config  <code>String</code>
   *
   * @return  <code>boolean</code>
   */
  public static boolean isConfig(final String config)
  {
    return CONFIG_PATTERN.matcher(config).matches();
  }


  /**
   * Initialize an instance of the class type with the properties contained in
   * this config.
   *
   * @return  <code>Object</code> of the type the config parsed
   */
  public Object initializeType()
  {
    final Class<?> c = SimplePropertyInvoker.createClass(this.getClassName());
    final Object o = SimplePropertyInvoker.instantiateType(
      c,
      this.getClassName());
    this.setProperties(c, o);
    return o;
  }


  /**
   * Sets the properties on the supplied object.
   *
   * @param  c  <code>Class</code> type of the supplied object
   * @param  o  <code>Object</code> to invoke properties on
   */
  protected void setProperties(final Class<?> c, final Object o)
  {
    final SimplePropertyInvoker invoker = new SimplePropertyInvoker(c);
    for (Map.Entry<String, String> entry : this.getProperties().entrySet()) {
      invoker.setProperty(o, entry.getKey(), entry.getValue());
    }
    if (invoker.getProperties().contains("initialize")) {
      invoker.setProperty(o, "initialize", null);
    }
  }
}
