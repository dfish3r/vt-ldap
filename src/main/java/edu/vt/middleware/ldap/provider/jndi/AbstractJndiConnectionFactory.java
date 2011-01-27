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
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.provider.AbstractConnectionFactory;
import edu.vt.middleware.ldap.provider.Connection;

/**
 * Base class for JNDI connection factory implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 1442 $
 */
public abstract class AbstractJndiConnectionFactory
  extends AbstractConnectionFactory
{
  /**
   * The value of this property is a string that specifies the authentication
   * mechanism(s) for the provider to use. The value of this constant is
   * {@value}.
   */
  public static final String AUTHENTICATION =
    "java.naming.security.authentication";

  /**
   * The value of this property is a string of decimal digits that specifies the
   * batch size of search results returned by the server. The value of this
   * constant is {@value}.
   */
  public static final String BATCH_SIZE = "java.naming.batchsize";

  /**
   * The value of this property is a string that specifies additional binary
   * attributes. The value of this constant is {@value}.
   */
  public static final String BINARY_ATTRIBUTES =
    "java.naming.ldap.attributes.binary";

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
   * The value of this property is a string that specifies how aliases shall be
   * handled by the provider. The value of this constant is {@value}.
   */
  public static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";

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
   * The value of this property is a string that specifies how referrals shall
   * be handled by the provider. The value of this constant is {@value}.
   */
  public static final String REFERRAL = "java.naming.referral";

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
   * The value of this property is a string that specifies to only return
   * attribute type names, no values. The value of this constant is {@value}.
   */
  public static final String TYPES_ONLY = "java.naming.ldap.typesOnly";

  /**
   * The value of this property is a string that specifies the protocol version
   * for the provider. The value of this constant is {@value}.
   */
  public static final String VERSION = "java.naming.ldap.version";

  /** Authentication mechanism. */
  protected String authentication;

  /** Environment properties. */
  protected Hashtable<String, Object> environment;

  /** Whether to log authentication credentials. */
  protected boolean logCredentials;

  /** Stream to print LDAP ASN.1 BER packets. */
  protected PrintStream tracePackets;

  /** Whether to remove the URL from any DNs which are not relative. */
  protected boolean removeDnUrls = true;

  /** Exceptions indicating that an operation should be retried. */
  protected Class<?>[] operationRetryExceptions = new Class<?>[] {
    CommunicationException.class, ServiceUnavailableException.class,
  };


  /**
   * Returns the value to use for java.naming.security.authentication.
   *
   * @return  authentication mechanism
   */
  public String getAuthentication()
  {
    return this.authentication;
  }


  /**
   * Sets the value to use for java.naming.security.authentication.
   *
   * @param  auth  authentication mechanism
   */
  public void setAuthentication(final String auth)
  {
    this.authentication = auth;
  }


  /**
   * Returns the ldap context environment properties that are used to make LDAP
   * connections.
   *
   * @return  context environment
   */
  public Hashtable<String, Object> getEnvironment()
  {
    return this.environment;
  }


  /**
   * Sets the ldap context environment properties that are used to make LDAP
   * connections.
   *
   * @param  env  context environment
   */
  public void setEnvironment(final Hashtable<String, Object> env)
  {
    this.environment = env;
  }


  /**
   * Returns whether authentication credentials will be logged.
   *
   * @return  whether authentication credentials will be logged
   */
  public boolean getLogCredentials()
  {
    return this.logCredentials;
  }


  /**
   * Sets whether authentication credentials will be logged.
   *
   * @param  b  whether authentication credentials will be logged
   */
  public void setLogCredentials(final boolean b)
  {
    this.logCredentials = b;
  }


  /**
   * Returns the print stream used to print ASN.1 BER packets.
   *
   * @return  print stream
   */
  public PrintStream getTracePackets()
  {
    return this.tracePackets;
  }


  /**
   * Sets the print stream to print ASN.1 BER packets to.
   *
   * @param  stream  to print to
   */
  public void setTracePackets(final PrintStream stream)
  {
    this.tracePackets = stream;
  }


  /**
   * Returns whether the URL will be removed from any DNs which are not
   * relative. The default value is true.
   *
   * @return  whether the URL will be removed from DNs
   */
  public boolean getRemoveDnUrls()
  {
    return this.removeDnUrls;
  }


  /**
   * Sets whether the URL will be removed from any DNs which are not relative
   * The default value is true.
   *
   * @param  b  whether the URL will be removed from DNs
   */
  public void setRemoveDnUrls(final boolean b)
  {
    this.removeDnUrls = b;
  }


  /**
   * Returns the naming exceptions to retry operations on.
   *
   * @return  naming exceptions
   */
  public Class<?>[] getOperationRetryExceptions()
  {
    return this.operationRetryExceptions;
  }


  /**
   * Sets the naming exceptions to retry operations on.
   *
   * @param  exceptions  naming exceptions
   */
  public void setOperationRetryExceptions(final Class<?>[] exceptions)
  {
    this.operationRetryExceptions = exceptions;
  }


  /** {@inheritDoc} */
  public void destroy(final Connection conn)
    throws LdapException
  {
    if (conn != null) {
      final JndiConnection jndiConn = (JndiConnection) conn;
      try {
        if (jndiConn.getLdapContext() != null) {
          jndiConn.getLdapContext().close();
        }
      } catch (NamingException e) {
        throw new LdapException(
          e, NamingExceptionUtil.getResultCode(e.getClass()));
      } finally {
        jndiConn.clear();
      }
    }
  }


  /**
   * Returns the configuration environment for a JNDI ldap context using the
   * properties found in the supplied ldap config.
   *
   * @param  lc  ldap config
   * @return  JNDI ldap context environment
   */
  protected static Hashtable<String, Object> createEnvironment(
    final LdapConfig lc)
  {
    final Hashtable<String, Object> env = new Hashtable<String, Object>();

    env.put(CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

    if (lc.getBatchSize() != -1) {
      env.put(BATCH_SIZE, Integer.toString(lc.getBatchSize()));
    }

    if (lc.getReferralBehavior() != null) {
      env.put(REFERRAL, lc.getReferralBehavior().name().toLowerCase());
    }

    if (lc.getDerefAliases() != null) {
      env.put(DEREF_ALIASES, lc.getDerefAliases().name().toLowerCase());
    }

    if (lc.getBinaryAttributes() != null) {
      final String[] a = lc.getBinaryAttributes();
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < a.length; i++) {
        sb.append(a[i]);
        if (i < a.length - 1) {
          sb.append(" ");
        }
      }
      env.put(BINARY_ATTRIBUTES, sb.toString());
    }

    if (lc.isTypesOnly()) {
      env.put(TYPES_ONLY, Boolean.valueOf(lc.getTypesOnly()).toString());
    }

    if (lc.isSslEnabled()) {
      env.put(PROTOCOL, "ssl");
      if (lc.getSslSocketFactory() != null) {
        env.put(SOCKET_FACTORY, lc.getSslSocketFactory().getClass().getName());
      }
    }

    env.put(TIMEOUT, Long.toString(lc.getTimeout()));

    if (!lc.getProviderProperties().isEmpty()) {
      for (Map.Entry<String, Object> entry :
           lc.getProviderProperties().entrySet()) {
        env.put(entry.getKey(), entry.getValue());
      }
    }

    return env;
  }
}
