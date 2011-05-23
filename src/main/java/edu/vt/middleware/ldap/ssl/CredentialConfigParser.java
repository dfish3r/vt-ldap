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
package edu.vt.middleware.ldap.ssl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.vt.middleware.ldap.props.SimplePropertyInvoker;

/**
 * Parses the configuration data associated with credential configs and ssl
 * socket factories. The format of the property string should be like:
 *
 * <pre>
   MySSLSocketFactory
     {KeyStoreCredentialConfig
       {{trustStore=/tmp/my.truststore}{trustStoreType=JKS}}}
 * </pre>
 *
 * <p>or</p>
 *
 * <pre>
   {KeyStoreCredentialConfig
     {{trustStore=/tmp/my.truststore}{trustStoreType=JKS}}}
 * </pre>
 *
 * <p>or</p>
 *
 * <pre>
   {{trustCertificates=/tmp/my.crt}}
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision: 930 $ $Date: 2009-10-26 16:44:26 -0400 (Mon, 26 Oct 2009) $
 */
public class CredentialConfigParser
{

  /** Property string for configuring a credential config. */
  private static final Pattern FULL_CONFIG_PATTERN = Pattern.compile(
    "([^\\{]+)\\s*\\{\\s*([^\\{]+)\\s*\\{\\s*(.*)\\}\\s*\\}\\s*");

  /** Property string for configuring a credential config. */
  private static final Pattern CREDENTIAL_ONLY_CONFIG_PATTERN = Pattern.compile(
    "\\s*\\{\\s*([^\\{]+)\\s*\\{\\s*(.*)\\}\\s*\\}\\s*");

  /** Property string for configuring a credential config. */
  private static final Pattern PARAMS_ONLY_CONFIG_PATTERN = Pattern.compile(
    "\\s*\\{\\s*(.*)\\s*\\}\\s*");

  /** Pattern for finding properties. */
  private static final Pattern PROPERTY_PATTERN = Pattern.compile(
    "([^\\}\\{])+");

  /** SSL socket factory class found in the config. */
  private String sslSocketFactoryClassName =
    "edu.vt.middleware.ldap.ssl.TLSSocketFactory";

  /** Credential config class found in the config. */
  private String credentialConfigClassName =
    "edu.vt.middleware.ldap.ssl.X509CredentialConfig";

  /** Properties found in the config to set on the credential config. */
  private Map<String, String> properties = new HashMap<String, String>();


  /**
   * Creates a new credential config parser.
   *
   * @param  config  containing configuration data
   */
  public CredentialConfigParser(final String config)
  {
    final Matcher fullMatcher = FULL_CONFIG_PATTERN.matcher(config);
    final Matcher credentialOnlyMatcher = CREDENTIAL_ONLY_CONFIG_PATTERN
        .matcher(config);
    final Matcher paramsOnlyMatcher = PARAMS_ONLY_CONFIG_PATTERN.matcher(
      config);
    Matcher m = null;
    if (fullMatcher.matches()) {
      int i = 1;
      sslSocketFactoryClassName = fullMatcher.group(i++).trim();
      credentialConfigClassName = fullMatcher.group(i++).trim();
      if (!"".equals(fullMatcher.group(i).trim())) {
        m = PROPERTY_PATTERN.matcher(fullMatcher.group(i).trim());
      }
    } else if (credentialOnlyMatcher.matches()) {
      int i = 1;
      credentialConfigClassName = credentialOnlyMatcher.group(i++).trim();
      if (!"".equals(credentialOnlyMatcher.group(i).trim())) {
        m = PROPERTY_PATTERN.matcher(credentialOnlyMatcher.group(i).trim());
      }
    } else if (paramsOnlyMatcher.matches()) {
      final int i = 1;
      if (!"".equals(paramsOnlyMatcher.group(i).trim())) {
        m = PROPERTY_PATTERN.matcher(paramsOnlyMatcher.group(i).trim());
      }
    }
    if (m != null) {
      while (m.find()) {
        final String input = m.group().trim();
        if (input != null && !"".equals(input)) {
          final String[] s = input.split("=");
          properties.put(s[0].trim(), s[1].trim());
        }
      }
    }
  }


  /**
   * Returns the SSL socket factory class name from the configuration.
   *
   * @return  class name
   */
  public String getSslSocketFactoryClassName()
  {
    return sslSocketFactoryClassName;
  }


  /**
   * Returns the credential config class name from the configuration.
   *
   * @return  class name
   */
  public String getCredentialConfigClassName()
  {
    return credentialConfigClassName;
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
   * Returns whether the supplied configuration data contains a credential
   * config.
   *
   * @param  config  containing configuration data
   *
   * @return  whether the supplied configuration data contains a credential
   * config
   */
  public static boolean isCredentialConfig(final String config)
  {
    return
      FULL_CONFIG_PATTERN.matcher(config).matches() ||
        CREDENTIAL_ONLY_CONFIG_PATTERN.matcher(config).matches() ||
        PARAMS_ONLY_CONFIG_PATTERN.matcher(config).matches();
  }


  /**
   * Initialize an instance of credential config with the properties contained
   * in this config.
   *
   * @return  object of the type CredentialConfig
   */
  public Object initializeType()
  {
    final Class<?> c = SimplePropertyInvoker.createClass(
      getCredentialConfigClassName());
    final Object o = SimplePropertyInvoker.instantiateType(
      c,
      getCredentialConfigClassName());
    setProperties(c, o);
    return o;
  }


  /**
   * Sets the properties on the supplied object.
   *
   * @param  c  class type of the supplied object
   * @param  o  object to invoke properties on
   */
  protected void setProperties(final Class<?> c, final Object o)
  {
    final SimplePropertyInvoker invoker = new SimplePropertyInvoker(c);
    for (Map.Entry<String, String> entry : getProperties().entrySet()) {
      invoker.setProperty(o, entry.getKey(), entry.getValue());
    }
  }
}
