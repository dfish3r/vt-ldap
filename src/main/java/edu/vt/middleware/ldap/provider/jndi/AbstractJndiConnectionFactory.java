/*
  $Id: DefaultConnectionHandler.java 1442 2010-07-01 18:05:58Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1442 $
  Updated: $Date: 2010-07-01 14:05:58 -0400 (Thu, 01 Jul 2010) $
*/
package edu.vt.middleware.ldap.provider.jndi;

import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.ldap.provider.AbstractConnectionFactory;
import edu.vt.middleware.ldap.sasl.DigestMd5Config;
import edu.vt.middleware.ldap.sasl.SaslConfig;

/**
 * Base class for JNDI connection factory implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 1442 $
 */
public abstract class AbstractJndiConnectionFactory
  extends AbstractConnectionFactory<JndiProviderConfig>
{

  /**
   * The value of this property is a string that specifies the authentication
   * mechanism(s) for the provider to use. The value of this constant is
   * {@value}.
   */
  public static final String AUTHENTICATION =
    "java.naming.security.authentication";

  /**
   * The value of this property is an object that specifies the credentials of
   * the principal to be authenticated. The value of this constant is {@value}.
   */
  public static final String CREDENTIALS = "java.naming.security.credentials";

  /**
   * The value of this property is a string that specifies the identity of the
   * principal to be authenticated. The value of this constant is {@value}.
   */
  public static final String PRINCIPAL = "java.naming.security.principal";

  /**
   * The value of this property is a URL string that specifies the hostname and
   * port number of the LDAP server, and the root distinguished name of the
   * naming context to use. The value of this constant is {@value}.
   */
  public static final String PROVIDER_URL = "java.naming.provider.url";

  /**
   * The value of this property is a java.io.OutputStream object into which a
   * hexadecimal dump of the incoming and outgoing LDAP ASN.1 BER packets is
   * written. The value of this constant is {@value}.
   */
  public static final String TRACE = "com.sun.jndi.ldap.trace.ber";

  /**
   * The value of this property is a string that specifies the protocol version
   * for the provider. The value of this constant is {@value}.
   */
  public static final String VERSION = "java.naming.ldap.version";

  /**
   * The value of this property is a string that specifies the sasl
   * authorization id. The value of this constant is {@value}.
   */
  public static final String SASL_AUTHZ_ID =
    "java.naming.security.sasl.authorizationId";

  /**
   * The value of this property is a string that specifies the sasl
   * quality of protection. The value of this constant is {@value}.
   */
  public static final String SASL_QOP = "javax.security.sasl.qop";

  /**
   * The value of this property is a string that specifies the sasl
   * security strength. The value of this constant is {@value}.
   */
  public static final String SASL_STRENGTH = "javax.security.sasl.strength";

  /**
   * The value of this property is a string that specifies the sasl
   * mutual authentication flag. The value of this constant is {@value}.
   */
  public static final String SASL_MUTUAL_AUTH =
    "javax.security.sasl.server.authentication";

  /**
   * The value of this property is a string that specifies the sasl realm. The
   * value of this constant is {@value}.
   */
  public static final String SASL_REALM = "java.naming.security.sasl.realm";

  /** Environment properties. */
  protected Map<String, Object> environment;


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::config=%s]",
        getClass().getName(),
        hashCode(),
        config);
  }


  /**
   * Returns the JNDI properties for the supplied sasl configuration.
   *
   * @param  config  sasl configuration
   * @return  JNDI properties for use in a context environment
   */
  protected static Map<String, Object> getSaslProperties(
    final SaslConfig config)
  {
    final Map<String, Object> env = new HashMap<String, Object>();
    if (config.getAuthorizationId() != null) {
      env.put(SASL_AUTHZ_ID, config.getAuthorizationId());
    }
    if (config.getQualityOfProtection() != null) {
      env.put(
        SASL_QOP,
        JndiUtil.getQualityOfProtection(config.getQualityOfProtection()));
    }
    if (config.getSecurityStrength() != null) {
      env.put(
        SASL_STRENGTH,
        JndiUtil.getSecurityStrength(config.getSecurityStrength()));
    }
    if (config.getMutualAuthentication() != null) {
      env.put(SASL_MUTUAL_AUTH, config.getMutualAuthentication().toString());
    }
    if (config instanceof DigestMd5Config) {
      if (((DigestMd5Config) config).getRealm() != null) {
        env.put(SASL_REALM, ((DigestMd5Config) config).getRealm());
      }
    }
    return env;
  }
}
