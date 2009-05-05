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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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

  /** Default file to read properties from, value is {@value} */
  public static final String PROPERTIES_FILE = "/ldap.properties";

  /** Log for this class */
  private static final Log LOG = LogFactory.getLog(LdapProperties.class);

  /** Class with properties. */
  private PropertyConfig propertyConfig;

  /** Underlying properties. */
  private Properties config;

  /** Configured file to read properties from */
  private String propertiesFile;


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
   * @param  propertiesFile  <code>String</code> of classpath resource or
   * filename
   */
  public LdapProperties(final PropertyConfig pc, final String propertiesFile)
  {
    this.propertyConfig = pc;
    this.usePropertiesFile(propertiesFile);
  }


  /** This will load properties from the default properties file. */
  public void useDefaultPropertiesFile()
  {
    this.usePropertiesFile(PROPERTIES_FILE);
  }


  /**
   * This will load properties from the supplied properties file.
   *
   * @param  propertiesFile  <code>String</code> of classpath resource or
   * filename
   */
  public void usePropertiesFile(final String propertiesFile)
  {
    this.propertiesFile = propertiesFile;
    if (this.config == null) {
      this.config = loadProperties(this.propertiesFile);
    } else {
      this.config.putAll(loadProperties(this.propertiesFile));
    }
  }


  /**
   * This creates a <code>Properties</code> from the supplied properties file.
   *
   * @param  propertiesFile  <code>String</code>
   *
   * @return  <code>Properties</code>
   */
  private Properties loadProperties(final String propertiesFile)
  {
    // try to get properties from classpath
    InputStream is = LdapProperties.class.getResourceAsStream(propertiesFile);
    if (is == null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Did not find properties from resource");
      }

      // try to get properties from a system file
      File file;
      try {
        file = new File(URI.create(propertiesFile));
        if (LOG.isDebugEnabled()) {
          LOG.debug("Supplied properties is a URI");
        }
      } catch (IllegalArgumentException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Supplied properties is not a URI");
        }
        file = new File(propertiesFile);
      }
      try {
        is = new FileInputStream(file);
        if (LOG.isDebugEnabled()) {
          LOG.debug("Found properties from file system");
        }
      } catch (IOException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Did not find properties from file system");
        }
      }
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Found properties from classpath");
      }
    }

    final Properties properties = new Properties();
    if (is != null) {
      try {
        properties.load(is);
        if (LOG.isDebugEnabled()) {
          LOG.debug("Loaded ldap properties from input stream");
        }
        is.close();
      } catch (IOException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error using input stream", e);
        }
      }
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Input stream was null, no properties loaded");
      }
    }
    return properties;
  }


  /**
   * This returns the name of the properties file being used by this <code>
   * LdapProperties</code>. Returns null if no properties file is being used.
   *
   * @return  <code>String</code>
   */
  public String getPropertiesFile()
  {
    return this.propertiesFile;
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
