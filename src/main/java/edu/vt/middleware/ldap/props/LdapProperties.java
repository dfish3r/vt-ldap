/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
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
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>LdapProperties</code> attempts to load the configuration properties
 * from a properties file in the classpath for a <code>PropertyConfig</code>
 * object. The default properties file is '/ldap.properties'.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public final class LdapProperties
{

  /** Default file to read properties from, value is {@value}. */
  public static final String PROPERTIES_FILE = "/ldap.properties";

  /** Log for this class. */
  private final Log logger = LogFactory.getLog(LdapProperties.class);

  /** Class with properties. */
  private PropertyConfig propertyConfig;

  /** Underlying properties. */
  private Properties config;


  /**
   * This will create a new <code>LdapProperties</code> for the supplied
   * properties config.
   *
   * @param  pc  object to set properties for
   */
  public LdapProperties(final PropertyConfig pc)
  {
    this.propertyConfig = pc;
    this.config = new Properties();
  }


  /**
   * This will create a new <code>LdapProperties</code> with the supplied
   * properties properties config and file.
   *
   * @param  pc  object to set properties for
   * @param  is  <code>InputStream</code> containing properties
   */
  public LdapProperties(final PropertyConfig pc, final InputStream is)
  {
    this.propertyConfig = pc;
    this.useProperties(is);
  }


  /** This will load properties from the default properties file. */
  public void useDefaultPropertiesFile()
  {
    this.useProperties(
      LdapProperties.class.getResourceAsStream(PROPERTIES_FILE));
  }


  /**
   * This will load properties from the supplied input stream.
   *
   * @param  is  <code>InputStream</code> containing properties
   */
  public void useProperties(final InputStream is)
  {
    if (this.config == null) {
      this.config = loadProperties(is);
    } else {
      this.config.putAll(loadProperties(is));
    }
  }


  /**
   * This creates a <code>Properties</code> from the supplied input stream.
   *
   * @param  is  <code>InputStream</code>
   *
   * @return  <code>Properties</code>
   */
  private Properties loadProperties(final InputStream is)
  {
    final Properties properties = new Properties();
    if (is != null) {
      try {
        properties.load(is);
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Loaded ldap properties from input stream");
        }
        is.close();
      } catch (IOException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("Error using input stream", e);
        }
      }
    } else {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Input stream was null, no properties loaded");
      }
    }
    return properties;
  }


  /**
   * This returns the name of the properties being used by this <code>
   * LdapProperties</code>.
   *
   * @return  <code>Properties</code>
   */
  public Properties getProperties()
  {
    return this.config;
  }


  /**
   * This sets the supplied key and value in the ldap properties. The key will
   * be prepended with the appropriate namespace.
   *
   * @param  key  <code>String</code>
   * @param  value  <code>String</code>
   */
  public void setProperty(final String key, final String value)
  {
    if (
      this.propertyConfig.hasEnvironmentProperty(
          this.propertyConfig.getPropertiesDomain() + key)) {
      this.config.setProperty(
        this.propertyConfig.getPropertiesDomain() + key,
        value);
    } else {
      this.config.setProperty(key, value);
    }
  }


  /**
   * This returns whether the supplied key has already been set. The key will be
   * prepended with the appropriate namespace.
   *
   * @param  key  <code>String</code>
   *
   * @return  <code>boolean</code>
   */
  public boolean isPropertySet(final String key)
  {
    boolean exists = false;
    if (
      this.propertyConfig.hasEnvironmentProperty(
          this.propertyConfig.getPropertiesDomain() + key)) {
      exists = this.config.containsKey(
        this.propertyConfig.getPropertiesDomain() + key);
    } else {
      exists = this.config.containsKey(key);
    }
    return exists;
  }


  /** Calls {@link PropertyConfig#setEnvironmentProperties(Properties)}. */
  public void configure()
  {
    this.propertyConfig.setEnvironmentProperties(this.config);
  }
}
