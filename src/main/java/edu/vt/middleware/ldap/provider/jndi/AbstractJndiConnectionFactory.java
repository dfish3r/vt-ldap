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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.provider.AbstractProviderConnectionFactory;
import edu.vt.middleware.ldap.sasl.DigestMd5Config;
import edu.vt.middleware.ldap.sasl.Mechanism;
import edu.vt.middleware.ldap.sasl.QualityOfProtection;
import edu.vt.middleware.ldap.sasl.SaslConfig;
import edu.vt.middleware.ldap.sasl.SecurityStrength;

/**
 * Base class for JNDI connection factory implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 1442 $
 */
public abstract class AbstractJndiConnectionFactory
  extends AbstractProviderConnectionFactory
  implements JndiProviderConnectionFactory
{
  /**
   * The value of this property is a string that specifies the authentication
   * mechanism(s) for the provider to use. The value of this constant is
   * {@value}.
   */
  public static final String AUTHENTICATION =
    "java.naming.security.authentication";

  /**
   * The value of this property is a fully qualified class name of the factory
   * class which creates the initial context for the LDAP service provider. The
   * value of this constant is {@value}.
   */
  public static final String CONTEXT_FACTORY = "java.naming.factory.initial";

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
   * The value of this property is a string that specifies the security protocol
   * for the provider to use. The value of this constant is {@value}.
   */
  public static final String PROTOCOL = "java.naming.security.protocol";

  /**
   * The value of this property is a URL string that specifies the hostname and
   * port number of the LDAP server, and the root distinguished name of the
   * naming context to use. The value of this constant is {@value}.
   */
  public static final String PROVIDER_URL = "java.naming.provider.url";

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

  /** Stream to print LDAP ASN.1 BER packets. */
  protected PrintStream tracePackets;

  /** Whether to remove the URL from any DNs which are not relative. */
  protected boolean removeDnUrls;

  /** ldap socket factory used for SSL and TLS. */
  protected SSLSocketFactory sslSocketFactory;

  /** hostname verifier for TLS connections. */
  protected HostnameVerifier hostnameVerifier;


  /** {@inheritDoc} */
  @Override
  public void initialize(final ConnectionConfig cc)
  {
    environment = createEnvironment(cc);
  }


  /** {@inheritDoc} */
  @Override
  public Map<String, Object> getEnvironment()
  {
    return environment;
  }


  /** {@inheritDoc} */
  @Override
  public void setEnvironment(final Map<String, Object> env)
  {
    logger.trace("setting environment: {}", env);
    environment = env;
  }


  /** {@inheritDoc} */
  @Override
  public PrintStream getTracePackets()
  {
    return tracePackets;
  }


  /** {@inheritDoc} */
  @Override
  public void setTracePackets(final PrintStream stream)
  {
    logger.trace("setting tracePackets: {}", stream);
    tracePackets = stream;
  }


  /** {@inheritDoc} */
  @Override
  public boolean getRemoveDnUrls()
  {
    return removeDnUrls;
  }


  /** {@inheritDoc} */
  @Override
  public void setRemoveDnUrls(final boolean b)
  {
    logger.trace("setting removeDnUrls: {}", b);
    removeDnUrls = b;
  }


  /** {@inheritDoc} */
  @Override
  public SSLSocketFactory getSslSocketFactory()
  {
    return sslSocketFactory;
  }


  /** {@inheritDoc} */
  @Override
  public void setSslSocketFactory(final SSLSocketFactory sf)
  {
    logger.trace("setting sslSocketFactory: {}", sf);
    sslSocketFactory = sf;
  }


  /** {@inheritDoc} */
  @Override
  public HostnameVerifier getHostnameVerifier()
  {
    return hostnameVerifier;
  }


  /** {@inheritDoc} */
  @Override
  public void setHostnameVerifier(final HostnameVerifier verifier)
  {
    logger.trace("setting hostnameVerifier: {}", verifier);
    hostnameVerifier = verifier;
  }


  /**
   * Returns the configuration environment for a JNDI ldap context using the
   * properties found in the supplied ldap connection config.
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

    if (!properties.isEmpty()) {
      for (Map.Entry<String, Object> entry : properties.entrySet()) {
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
        SASL_QOP, getQualityOfProtection(config.getQualityOfProtection()));
    }
    if (config.getSecurityStrength() != null) {
      env.put(
        SASL_STRENGTH,
        getSecurityStrength(config.getSecurityStrength()));
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


  /**
   * Returns the SASL quality of protection string for the supplied enum.
   *
   * @param  qop  quality of protection enum
   * @return  SASL quality of protection string
   */
  protected static String getQualityOfProtection(final QualityOfProtection qop)
  {
    String s = null;
    switch (qop) {
    case AUTH:
      s = "auth";
      break;
    case AUTH_INT:
      s = "auth-int";
      break;
    case AUTH_CONF:
      s = "auth-conf";
      break;
    default:
      throw new IllegalArgumentException(
        "Unknown SASL quality of protection: " + qop);
    }
    return s;
  }


  /**
   * Returns the SASL security strength string for the supplied enum.
   *
   * @param  ss  security strength enum
   * @return  SASL security strength string
   */
  protected static String getSecurityStrength(final SecurityStrength ss)
  {
    String s = null;
    switch (ss) {
    case HIGH:
      s = "high";
      break;
    case MEDIUM:
      s = "medium";
      break;
    case LOW:
      s = "low";
      break;
    default:
      throw new IllegalArgumentException(
        "Unknown SASL security strength: " + ss);
    }
    return s;
  }


  /**
   * Returns the JNDI authentication string for the supplied authentication
   * type.
   *
   * @param  m  sasl mechanism
   * @return  JNDI authentication string
   */
  protected static String getAuthenticationType(final Mechanism m)
  {
    String s = null;
    switch (m) {
    case EXTERNAL:
      s = "EXTERNAL";
      break;
    case DIGEST_MD5:
      s = "DIGEST-MD5";
      break;
    case CRAM_MD5:
      s = "CRAM-MD5";
      break;
    case GSSAPI:
      s = "GSSAPI";
      break;
    default:
      throw new IllegalArgumentException(
        "Unknown SASL authentication mechanism: " + m);
    }
    return s;
  }
}
