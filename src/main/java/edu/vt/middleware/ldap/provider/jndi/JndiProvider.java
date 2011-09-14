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
package edu.vt.middleware.ldap.provider.jndi;

import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.provider.ConnectionFactory;
import edu.vt.middleware.ldap.provider.Provider;
import edu.vt.middleware.ldap.sasl.DigestMd5Config;
import edu.vt.middleware.ldap.sasl.SaslConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exposes a connection factory for creating connections with JNDI.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiProvider implements Provider<JndiProviderConfig>
{

  /**
   * The value of this property is a fully qualified class name of the factory
   * class which creates the initial context for the LDAP service provider. The
   * value of this constant is {@value}.
   */
  public static final String CONTEXT_FACTORY = "java.naming.factory.initial";

  /**
   * The value of this property is a string that specifies the security protocol
   * for the provider to use. The value of this constant is {@value}.
   */
  public static final String PROTOCOL = "java.naming.security.protocol";

  /**
   * The value of this property is a string identifying the class name of a
   * socket factory. The value of this constant is {@value}.
   */
  public static final String SOCKET_FACTORY = "java.naming.ldap.factory.socket";

  /**
   * The value of this property is a string that specifies the time in
   * milliseconds that a connection attempt will abort if the connection cannot
   * be made. The value of this constant is {@value}.
   */
  public static final String TIMEOUT = "com.sun.jndi.ldap.connect.timeout";

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

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Provider configuration. */
  private JndiProviderConfig config = new JndiProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory<JndiProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    ConnectionFactory<JndiProviderConfig> cf = null;
    if (cc.isTlsEnabled()) {
      cf = new JndiTlsConnectionFactory(cc.getLdapUrl(), createEnvironment(cc));
    } else {
      cf = new JndiConnectionFactory(cc.getLdapUrl(), createEnvironment(cc));
    }
    config.setSslSocketFactory(cc.getSslSocketFactory());
    config.setHostnameVerifier(cc.getHostnameVerifier());
    cf.setProviderConfig(config);
    return cf;
  }


  /** {@inheritDoc} */
  @Override
  public JndiProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final JndiProviderConfig jpc)
  {
    config = jpc;
  }


  /** {@inheritDoc} */
  @Override
  public JndiProvider newInstance()
  {
    return new JndiProvider();
  }


  /**
   * Returns the configuration environment for a JNDI ldap context using the
   * properties found in the supplied connection config.
   *
   * @param  cc  connection config
   * @return  JNDI ldap context environment
   */
  protected Map<String, Object> createEnvironment(
    final ConnectionConfig cc)
  {
    final Map<String, Object> env = new HashMap<String, Object>();
    env.put(CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    if (cc.isSslEnabled()) {
      env.put(PROTOCOL, "ssl");
      if (cc.getSslSocketFactory() != null) {
        env.put(SOCKET_FACTORY, cc.getSslSocketFactory().getClass().getName());
      }
    }
    if (cc.getTimeout() > 0) {
      env.put(TIMEOUT, Long.toString(cc.getTimeout()));
    }
    if (cc.getSaslConfig() != null) {
      env.putAll(getSaslProperties(cc.getSaslConfig()));
    }
    if (!config.getProperties().isEmpty()) {
      for (Map.Entry<String, Object> entry :
           config.getProperties().entrySet()) {
        env.put(entry.getKey(), entry.getValue());
      }
    }
    return env;
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
