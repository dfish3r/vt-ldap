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
import edu.vt.middleware.ldap.AuthenticationType;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.provider.AbstractProviderConnectionFactory;

/**
 * Base class for JNDI connection factory implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 1442 $
 */
public abstract class AbstractJndiConnectionFactory
  extends AbstractProviderConnectionFactory
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
  protected boolean removeDnUrls = true;


  /** Default constructor. */
  public AbstractJndiConnectionFactory()
  {
    operationRetryResultCodes = new ResultCode[] {
      ResultCode.PROTOCOL_ERROR, ResultCode.BUSY, ResultCode.UNAVAILABLE,
    };
  }


  /**
   * Returns the ldap context environment properties that are used to make LDAP
   * connections.
   *
   * @return  context environment
   */
  public Hashtable<String, Object> getEnvironment()
  {
    return environment;
  }


  /**
   * Sets the ldap context environment properties that are used to make LDAP
   * connections.
   *
   * @param  env  context environment
   */
  public void setEnvironment(final Hashtable<String, Object> env)
  {
    environment = env;
  }


  /**
   * Returns the print stream used to print ASN.1 BER packets.
   *
   * @return  print stream
   */
  public PrintStream getTracePackets()
  {
    return tracePackets;
  }


  /**
   * Sets the print stream to print ASN.1 BER packets to.
   *
   * @param  stream  to print to
   */
  public void setTracePackets(final PrintStream stream)
  {
    tracePackets = stream;
  }


  /**
   * Returns whether the URL will be removed from any DNs which are not
   * relative. The default value is true.
   *
   * @return  whether the URL will be removed from DNs
   */
  public boolean getRemoveDnUrls()
  {
    return removeDnUrls;
  }


  /**
   * Sets whether the URL will be removed from any DNs which are not relative
   * The default value is true.
   *
   * @param  b  whether the URL will be removed from DNs
   */
  public void setRemoveDnUrls(final boolean b)
  {
    removeDnUrls = b;
  }


  /**
   * Returns the configuration environment for a JNDI ldap context using the
   * properties found in the supplied ldap connection config.
   *
   * @param  lcc  ldap connection config
   * @return  JNDI ldap context environment
   */
  protected static Hashtable<String, Object> createEnvironment(
    final ConnectionConfig lcc)
  {
    final Hashtable<String, Object> env = new Hashtable<String, Object>();

    env.put(CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

    if (lcc.isSslEnabled()) {
      env.put(PROTOCOL, "ssl");
      if (lcc.getSslSocketFactory() != null) {
        env.put(SOCKET_FACTORY, lcc.getSslSocketFactory().getClass().getName());
      }
    }

    if (lcc.getTimeout() > 0) {
      env.put(TIMEOUT, Long.toString(lcc.getTimeout()));
    }

    if (!lcc.getProviderProperties().isEmpty()) {
      for (Map.Entry<String, Object> entry :
           lcc.getProviderProperties().entrySet()) {
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
    case STRONG:
      s = "strong";
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
