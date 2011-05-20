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
package edu.vt.middleware.ldap;

import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.provider.ConnectionStrategy;
import edu.vt.middleware.ldap.provider.Provider;
import edu.vt.middleware.ldap.provider.jndi.JndiProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all the configuration data needed to control LDAP connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectionConfig extends AbstractConfig
{
  /** Ldap provider class name. */
  public static final String PROVIDER = "edu.vt.middleware.ldap.provider";

  /** Static reference to the default ldap provider. */
  private static final Provider DEFAULT_PROVIDER;

  /** Ldap provider implementation. */
  private Provider provider = DEFAULT_PROVIDER;

  /** Default ldap socket factory used for SSL and TLS. */
  private SSLSocketFactory sslSocketFactory;

  /** Default hostname verifier for TLS connections. */
  private HostnameVerifier hostnameVerifier;

  /** URL to the LDAP(s). */
  private String ldapUrl;

  /** Amount of time in milliseconds that connect operations will block. */
  private long timeout = -1;

  /** DN to bind as before performing operations. */
  private String bindDn;

  /** Credential for the bind DN. */
  private Credential bindCredential;

  /** Authentication type to use when binding to the LDAP. */
  private AuthenticationType authenticationType = AuthenticationType.SIMPLE;

  /** Number of times to retry ldap operations on exception. */
  private int operationRetry = 1;

  /** Amount of time in milliseconds to wait before retrying. */
  private long operationRetryWait;

  /** Factor to multiply operation retry wait by. */
  private int operationRetryBackoff;

  /** Additional provider properties. */
  private Map<String, Object> providerProperties =
    new HashMap<String, Object>();

  /** Whether to log authentication credentials. */
  private boolean logCredentials;

  /** Connect to LDAP using SSL protocol. */
  private boolean ssl;

  /** Connect to LDAP using TLS protocol. */
  private boolean tls;

  /** Ldap connection strategy. */
  private ConnectionStrategy connectionStrategy = ConnectionStrategy.DEFAULT;

  /** Initialize the default ldap provider. The {@link #LDAP_PROVIDER} property
   * is checked and that class is loaded if provided. Otherwise the JNDI
   * provider is returned.
   */
  static {
    final String providerClass = System.getProperty(PROVIDER);
    if (providerClass != null) {
      final Logger l = LoggerFactory.getLogger(ConnectionConfig.class);
      try {
        if (l.isInfoEnabled()) {
          l.info("Setting ldap provider to " + providerClass);
        }
        DEFAULT_PROVIDER =
          (Provider) Class.forName(providerClass).newInstance();
      } catch (Exception e) {
        if (l.isErrorEnabled()) {
          l.error("Error instantiating " + providerClass, e);
        }
        throw new IllegalStateException(e);
      }
    } else {
      // set the default ldap provider to JNDI
      DEFAULT_PROVIDER = new JndiProvider();
    }
  }


  /** Default constructor. */
  public ConnectionConfig() {}


  /**
   * Creates a new ldap config.
   *
   * @param  url  to connect to
   */
  public ConnectionConfig(final String url)
  {
    this();
    setLdapUrl(url);
  }


  /**
   * Returns the ldap provider.
   *
   * @return  ldap provider
   */
  public Provider getProvider()
  {
    return provider;
  }


  /**
   * Sets the ldap provider.
   *
   * @param  lp  ldap provider to set
   */
  public void setProvider(final Provider lp)
  {
    checkImmutable();
    logger.trace("setting provider: {}", lp);
    provider = lp;
  }


  /**
   * Returns the SSL socket factory used when making SSL or TLS connections.
   *
   * @return  SSL socket factory
   */
  public SSLSocketFactory getSslSocketFactory()
  {
    return sslSocketFactory;
  }


  /**
   * Sets the SSL socket factory.
   *
   * @param  sf  SSL socket factory
   */
  public void setSslSocketFactory(final SSLSocketFactory sf)
  {
    checkImmutable();
    logger.trace("setting sslSocketFactory: {}", sf);
    sslSocketFactory = sf;
  }


  /**
   * Returns the hostname verifier used when making SSL or TLS connections.
   *
   * @return  hostname verifier
   */
  public HostnameVerifier getHostnameVerifier()
  {
    return hostnameVerifier;
  }


  /**
   * Sets the hostname verifier.
   *
   * @param  hv  hostname verifier
   */
  public void setHostnameVerifier(final HostnameVerifier hv)
  {
    checkImmutable();
    logger.trace("setting hostnameVerifier: {}", hv);
    hostnameVerifier = hv;
  }


  /**
   * Returns the ldap url.
   *
   * @return  ldap url
   */
  public String getLdapUrl()
  {
    return ldapUrl;
  }


  /**
   * Sets the ldap url.
   *
   * @param  url  of the ldap
   */
  public void setLdapUrl(final String url)
  {
    checkImmutable();
    checkStringInput(url, true);
    logger.trace("setting ldapUrl: {}", url);
    ldapUrl = url;
  }


  /**
   * Returns the connect timeout. If this value is 0, then connect operations
   * will wait indefinitely.
   *
   * @return  timeout
   */
  public long getTimeout()
  {
    return timeout;
  }


  /**
   * Sets the maximum amount of time in milliseconds that connect operations
   * will block.
   *
   * @param  l  timeout for connect operations
   */
  public void setTimeout(final long l)
  {
    checkImmutable();
    logger.trace("setting timeout: {}", l);
    timeout = l;
  }


  /**
   * Returns the bind DN.
   *
   * @return  DN to bind as
   */
  public String getBindDn()
  {
    return bindDn;
  }


  /**
   * Sets the bind DN to authenticate as before performing operations.
   *
   * @param  dn  to bind as
   */
  public void setBindDn(final String dn)
  {
    checkImmutable();
    checkStringInput(dn, true);
    logger.trace("setting bindDn: {}", dn);
    bindDn = dn;
  }


  /**
   * Returns the credential used with the bind DN.
   *
   * @return  bind DN credential
   */
  public Credential getBindCredential()
  {
    return bindCredential;
  }


  /**
   * Sets the credential of the bind DN.
   *
   * @param  credential  to use with bind DN
   */
  public void setBindCredential(final Credential credential)
  {
    checkImmutable();
    if (getLogCredentials() || credential == null) {
      logger.trace("setting bindCredential: {}", credential);
    } else {
      logger.trace("setting bindCredential: <suppressed>");
    }
    bindCredential = credential;
  }


  /**
   * Returns the authentication type.
   *
   * @return  authentication type
   */
  public AuthenticationType getAuthenticationType()
  {
    return authenticationType;
  }


  /**
   * Sets the authentication type.
   *
   * @param  type  of authentication to use
   */
  public void setAuthenticationType(final AuthenticationType type)
  {
    checkImmutable();
    logger.trace("setting authenticationType: {}", type);
    authenticationType = type;
  }


  /**
   * Returns the number of times ldap operations will be retried if an operation
   * exception occurs. If this value is 0, no retries will occur.
   *
   * @return  number of retries
   */
  public int getOperationRetry()
  {
    return operationRetry;
  }


  /**
   * Sets the number of times that ldap operations will be retried if an
   * operation exception occurs.
   *
   * @param  i  number of retries
   */
  public void setOperationRetry(final int i)
  {
    checkImmutable();
    logger.trace("setting operationRetry: {}", i);
    operationRetry = i;
  }


  /**
   * Returns the operation retry wait time.
   *
   * @return  retry wait
   */
  public long getOperationRetryWait()
  {
    return operationRetryWait;
  }


  /**
   * Sets the amount of time in milliseconds that operations should wait
   * before retrying.
   *
   * @param  l  time in milliseconds to wait
   */
  public void setOperationRetryWait(final long l)
  {
    checkImmutable();
    logger.trace("setting operationRetryWait: {}", l);
    operationRetryWait = l;
  }


  /**
   * Returns the factor by which to multiply the operation retry wait time.
   * This allows clients to progressively delay each retry. The formula for
   * backoff is (wait * backoff * attempt). So a wait time of 2s with a backoff
   * of 3 will delay by 6s, then 12s, then 18s, and so forth.
   *
   * @return  backoff factor
   */
  public int getOperationRetryBackoff()
  {
    return operationRetryBackoff;
  }


  /**
   * Sets the factor by which to multiply the operation retry wait time.
   *
   * @param  backoff  factor to multiply wait time by
   */
  public void setOperationRetryBackoff(final int backoff)
  {
    checkImmutable();
    logger.trace("setting operationRetryBackoff: {}", backoff);
    operationRetryBackoff = backoff;
  }


  /**
   * Returns provider specific properties.
   *
   * @return  map of additional provider properties
   */
  public Map<String, Object> getProviderProperties()
  {
    return providerProperties;
  }


  /**
   * Sets provider specific properties.
   *
   * @param  props  map of additional provider properties
   */
  public void setProviderProperties(final Map<String, Object> props)
  {
    checkImmutable();
    logger.trace("setting provider properties {}", props);
    providerProperties = props;
  }


  /**
   * Returns whether authentication credentials will be logged.
   *
   * @return  whether authentication credentials will be logged.
   */
  public boolean getLogCredentials()
  {
    return logCredentials;
  }


  /**
   * Sets whether authentication credentials will be logged.
   *
   * @param  b  whether authentication credentials will be logged
   */
  public void setLogCredentials(final boolean b)
  {
    checkImmutable();
    logger.trace("setting logCredentials: {}", b);
    logCredentials = b;
  }


  /**
   * See {@link #isSslEnabled()}.
   *
   * @return  whether the SSL protocol will be used
   */
  public boolean getSsl()
  {
    return isSslEnabled();
  }


  /**
   * Returns whether the SSL protocol will be used for connections.
   *
   * @return  whether the SSL protocol will be used
   */
  public boolean isSslEnabled()
  {
    return ssl;
  }


  /**
   * Sets whether the SSL protocol will be used for connections.
   *
   * @param  b  whether the SSL protocol will be used
   */
  public void setSsl(final boolean b)
  {
    checkImmutable();
    logger.trace("setting ssl: {}", b);
    ssl = b;
  }


  /**
   * See {@link #isTlsEnabled()}.
   *
   * @return  whether the TLS protocol will be used
   */
  public boolean getTls()
  {
    return isTlsEnabled();
  }


  /**
   * Returns whether the TLS protocol will be used for connections.
   *
   * @return  whether the TLS protocol will be used
   */
  public boolean isTlsEnabled()
  {
    return tls;
  }


  /**
   * Sets whether the TLS protocol will be used for connections.
   *
   * @param  b  whether the TLS protocol will be used
   */
  public void setTls(final boolean b)
  {
    checkImmutable();
    logger.trace("setting tls: {}", b);
    tls = b;
  }


  /**
   * Returns the connection strategy.
   *
   * @return  connection strategy
   */
  public ConnectionStrategy getConnectionStrategy()
  {
    return connectionStrategy;
  }


  /**
   * Sets the connection strategy.
   *
   * @param  strategy  for making new connections
   */
  public void setConnectionStrategy(final ConnectionStrategy strategy)
  {
    checkImmutable();
    logger.trace("setting connectionStrategy: {}", strategy);
    connectionStrategy = strategy;
  }


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
        "[%s@%d::provider=%s, sslSocketFactory=%s, " +
        "hostnameVerifier=%s, ldapUrl=%s, timeout=%s, bindDn=%s, " +
        "bindCredential=%s, authenticationType=%s, operationRetry=%s, " +
        "operationRetryWait=%s, operationRetryBackoff=%s, " +
        "providerProperties=%s, logCredentials=%s, ssl=%s, tls=%s, " +
        "connectionStrategy=%s]",
        getClass().getName(),
        hashCode(),
        provider,
        sslSocketFactory,
        hostnameVerifier,
        ldapUrl,
        timeout,
        bindDn,
        logCredentials || bindCredential == null ?
          bindCredential : "<suppressed>",
        authenticationType,
        operationRetry,
        operationRetryWait,
        operationRetryBackoff,
        providerProperties,
        logCredentials,
        ssl,
        tls,
        connectionStrategy);
  }
}
