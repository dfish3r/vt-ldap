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
import java.util.Hashtable;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.AuthenticationType;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.provider.AbstractProviderConnectionFactory;

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

  /** Environment properties. */
  protected Hashtable<String, Object> environment;

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
  public Hashtable<String, Object> getEnvironment()
  {
    return environment;
  }


  /** {@inheritDoc} */
  @Override
  public void setEnvironment(final Hashtable<String, Object> env)
  {
    environment = env;
  }


  /** {@inheritDoc} */
  @Override
  public void setEnvironment(final ConnectionConfig cc)
  {
    environment = createEnvironment(cc);
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
    hostnameVerifier = verifier;
  }


  /**
   * Returns the configuration environment for a JNDI ldap context using the
   * properties found in the supplied ldap connection config.
   *
   * @param  cc  connection config
   * @return  JNDI ldap context environment
   */
  protected static Hashtable<String, Object> createEnvironment(
    final ConnectionConfig cc)
  {
    final Hashtable<String, Object> env = new Hashtable<String, Object>();

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

    if (!cc.getProviderProperties().isEmpty()) {
      for (Map.Entry<String, Object> entry :
           cc.getProviderProperties().entrySet()) {
        env.put(entry.getKey(), entry.getValue());
      }
    }

    return env;
  }


  /**
   * Returns the JNDI authentication string for the supplied authentication
   * type.
   *
   * @param  type  authentication type
   * @return  JNDI authentication string
   */
  protected static String getAuthenticationType(final AuthenticationType type)
  {
    String s = null;
    switch (type) {
    case ANONYMOUS:
      s = "none";
      break;
    case SIMPLE:
      s = "simple";
      break;
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
        "Unknown authentication type: " + type);
    }
    return s;
  }
}
