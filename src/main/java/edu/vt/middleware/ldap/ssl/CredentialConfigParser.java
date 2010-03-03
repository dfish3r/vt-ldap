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
 * Parses the configuration data associated with credential configs and ssl
 * socket factories.
 * The format of the property string should be like:
 * <pre>
 * MySSLSocketFactory
 *   {KeyStoreCredentialConfig
 *     {trustStore=/tmp/my.truststore, trustStoreType=JKS}}
 * </pre>
 * or
 * <pre>
 * {KeyStoreCredentialConfig{trustStore=/tmp/my.truststore, trustStoreType=JKS}}
 * </pre>
 * or
 * <pre>
 * {trustCertificates=/tmp/my.crt}
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision: 930 $ $Date: 2009-10-26 16:44:26 -0400 (Mon, 26 Oct 2009) $
 */
public class CredentialConfigParser
{
  /** Property string for configuring a credential config. */
  private static final Pattern FULL_CONFIG_PATTERN = Pattern.compile(
    "(.+)\\s*\\{(.+)\\s*\\{(.*)\\}\\s*\\}\\s*");

  /** Property string for configuring a credential config. */
  private static final Pattern CREDENTIAL_ONLY_CONFIG_PATTERN = Pattern.compile(
    "\\s*\\{(.+)\\s*\\{(.*)\\}\\s*\\}\\s*");

  /** Property string for configuring a credential config. */
  private static final Pattern PARAMS_ONLY_CONFIG_PATTERN = Pattern.compile(
    "\\s*\\{(.*)\\}\\s*");

  /** SSL socket factory class found in the config. */
  private String sslSocketFactoryClassName =
    "edu.vt.middleware.ldap.ssl.TLSSocketFactory";

  /** Credential config class found in the config. */
  private String credentialConfigClassName =
    "edu.vt.middleware.ldap.ssl.X509CredentialConfig";

  /** Properties found in the config to set on the credential config. */
  private Map<String, String> properties = new HashMap<String, String>();


  /**
   * Creates a new <code>CredentialConfigParser</code> with the supplied
   * configuration string.
   *
   * @param  config  <code>String</code>
   */
  public CredentialConfigParser(final String config)
  {
    final Matcher fullMatcher = FULL_CONFIG_PATTERN.matcher(config);
    final Matcher credentialOnlyMatcher =
      CREDENTIAL_ONLY_CONFIG_PATTERN.matcher(config);
    final Matcher paramsOnlyMatcher = PARAMS_ONLY_CONFIG_PATTERN.matcher(
      config);
    if (fullMatcher.matches()) {
      int i = 1;
      this.sslSocketFactoryClassName = fullMatcher.group(i++).trim();
      this.credentialConfigClassName = fullMatcher.group(i++).trim();
      if (!fullMatcher.group(i).trim().equals("")) {
        for (String input : fullMatcher.group(i).trim().split(",")) {
          final String[] s = input.split("=");
          this.properties.put(s[0].trim(), s[1].trim());
        }
      }
    } else if (credentialOnlyMatcher.matches()) {
      int i = 1;
      this.credentialConfigClassName = credentialOnlyMatcher.group(i++).trim();
      if (!credentialOnlyMatcher.group(i).trim().equals("")) {
        for (String input : credentialOnlyMatcher.group(i).trim().split(",")) {
          final String[] s = input.split("=");
          this.properties.put(s[0].trim(), s[1].trim());
        }
      }
    } else if (paramsOnlyMatcher.matches()) {
      int i = 1;
      if (!paramsOnlyMatcher.group(i).trim().equals("")) {
        for (String input : paramsOnlyMatcher.group(i).trim().split(",")) {
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
   * Returns the credential config class name from the configuration.
   *
   * @return  <code>String</code>  class name
   */
  public String getCredentialConfigClassName()
  {
    return this.credentialConfigClassName;
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
   * Returns whether the supplied configuration data contains a credential
   * config.
   *
   * @param  config  <code>String</code>
   * @return  <code>boolean</code>
   */
  public static boolean isCredentialConfig(final String config)
  {
    return FULL_CONFIG_PATTERN.matcher(config).matches() ||
           CREDENTIAL_ONLY_CONFIG_PATTERN.matcher(config).matches() ||
           PARAMS_ONLY_CONFIG_PATTERN.matcher(config).matches();
  }
}
