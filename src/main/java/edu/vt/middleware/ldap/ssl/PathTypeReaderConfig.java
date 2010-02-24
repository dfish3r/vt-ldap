/*
  $Id: LdapUtil.java 930 2009-10-26 20:44:26Z dfisher $

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 930 $
  Updated: $Date: 2009-10-26 16:44:26 -0400 (Mon, 26 Oct 2009) $
*/
package edu.vt.middleware.ldap.ssl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the configuration data associated with path type readers and ssl
 * socket factories.
 * The format of the property string should be like:
 * MySSLSocketFactory
 *   {KeyStorePathTypeReader
 *     {trustStore=/tmp/my.truststore, trustStorePathType=FILEPATH}}
 *
 * @author  Middleware Services
 * @version  $Revision: 930 $ $Date: 2009-10-26 16:44:26 -0400 (Mon, 26 Oct 2009) $
 */
public class PathTypeReaderConfig
{
  /** Property string for configuring a path type reader. */
  private static final Pattern FULL_CONFIG_PATTERN = Pattern.compile(
    "(.*)\\s*\\{(.*)\\{(.*)\\}\\s*\\}\\s*");

  /** Property string for configuring a path type reader. */
  private static final Pattern BRIEF_CONFIG_PATTERN = Pattern.compile(
    "(.*)\\s*\\{(.*)\\}\\s*");

  /** SSL socket factory class found in the config. */
  private String sslSocketFactoryClassName;

  /** Path type reader class found in the config. */
  private String pathTypeReaderClassName =
    "edu.vt.middleware.ldap.ssl.DefaultX509PathTypeReader";

  /** Properties found in the config to set on the path type reader. */
  private Map<String, String> properties = new HashMap<String, String>();


  /**
   * Creates a new <code>PathTypeReaderConfig</code> with the supplied
   * configuration string.
   *
   * @param  config  <code>String</code>
   */
  public PathTypeReaderConfig(final String config)
  {
    final Matcher fullMatcher = FULL_CONFIG_PATTERN.matcher(config);
    final Matcher briefMatcher = BRIEF_CONFIG_PATTERN.matcher(config);
    if (fullMatcher.matches()) {
      int i = 1;
      this.sslSocketFactoryClassName = fullMatcher.group(i++).trim();
      this.pathTypeReaderClassName = fullMatcher.group(i++).trim();
      if (!fullMatcher.group(i).trim().equals("")) {
        for (String input : fullMatcher.group(i).trim().split(",")) {
          final String[] s = input.split("=");
          this.properties.put(s[0].trim(), s[1].trim());
        }
      }
    } else if (briefMatcher.matches()) {
      int i = 1;
      this.sslSocketFactoryClassName = briefMatcher.group(i++).trim();
      if (!briefMatcher.group(i).trim().equals("")) {
        for (String input : briefMatcher.group(i).trim().split(",")) {
          final String[] s = input.split("=");
          this.properties.put(s[0].trim(), s[1].trim());
        }
      }
    }
  }


  /**
   * Returns the SSL socket factory class name from the configuration.
   *
   * @return  <code>String</code>  class name
   */
  public String getSslSocketFactoryClassName()
  {
    return this.sslSocketFactoryClassName;
  }


  /**
   * Returns the path type reader class name from the configuration.
   *
   * @return  <code>String</code>  class name
   */
  public String getPathTypeReaderClassName()
  {
    return this.pathTypeReaderClassName;
  }


  /**
   * Returns the properties from the configuration.
   *
   * @return  <code>Map</code>  of property name to value
   */
  public Map<String, String> getProperties()
  {
    return this.properties;
  }


  /**
   * Returns whether the supplied configuration data contains a path type
   * reader.
   *
   * @param  config  <code>String</code>
   * @return  <code>boolean</code>
   */
  public static boolean isPathTypeReaderConfig(final String config)
  {
    return FULL_CONFIG_PATTERN.matcher(config).matches() ||
           BRIEF_CONFIG_PATTERN.matcher(config).matches();
  }
}