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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.sasl.SaslConfig;

/**
 * Contains all the configuration data needed to control connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectionConfig extends AbstractConfig
{

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

  /** Configuration for SASL authentication. */
  private SaslConfig saslConfig;

  /** Number of times to retry ldap operations on exception. */
  private int operationRetry = 1;

  /** Amount of time in milliseconds to wait before retrying. */
  private long operationRetryWait;

  /** Factor to multiply operation retry wait by. */
  private int operationRetryBackoff;

  /** Connect to LDAP using SSL protocol. */
  private boolean ssl;

  /** Connect to LDAP using TLS protocol. */
  private boolean tls;


  /** Default constructor. */
  public ConnectionConfig() {}


  /**
   * Creates a new ldap config.
   *
   * @param  url  to connect to
   */
  public ConnectionConfig(final String url)
  {
    setLdapUrl(url);
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
   * @param  factory  SSL socket factory
   */
  public void setSslSocketFactory(final SSLSocketFactory factory)
  {
    checkImmutable();
    logger.trace("setting sslSocketFactory: {}", factory);
    sslSocketFactory = factory;
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
   * @param  verifier  hostname verifier
   */
  public void setHostnameVerifier(final HostnameVerifier verifier)
  {
    checkImmutable();
    logger.trace("setting hostnameVerifier: {}", verifier);
    hostnameVerifier = verifier;
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
   * @param  time  timeout for connect operations
   */
  public void setTimeout(final long time)
  {
    checkImmutable();
    logger.trace("setting timeout: {}", time);
    timeout = time;
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
    logger.trace("setting bindCredential: <suppressed>");
    bindCredential = credential;
  }


  /**
   * Returns the sasl config.
   *
   * @return  sasl config
   */
  public SaslConfig getSaslConfig()
  {
    return saslConfig;
  }


  /**
   * Sets the sasl config.
   *
   * @param  config  sasl config
   */
  public void setSaslConfig(final SaslConfig config)
  {
    checkImmutable();
    logger.trace("setting saslConfig: {}", config);
    saslConfig = config;
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
   * @param  retry  number of retries
   */
  public void setOperationRetry(final int retry)
  {
    checkImmutable();
    logger.trace("setting operationRetry: {}", retry);
    operationRetry = retry;
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
   * @param  wait  time in milliseconds to wait
   */
  public void setOperationRetryWait(final long wait)
  {
    checkImmutable();
    logger.trace("setting operationRetryWait: {}", wait);
    operationRetryWait = wait;
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
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::sslSocketFactory=%s, hostnameVerifier=%s, ldapUrl=%s, " +
        "timeout=%s, bindDn=%s, saslConfig=%s, operationRetry=%s, " +
        "operationRetryWait=%s, operationRetryBackoff=%s, ssl=%s, tls=%s]",
        getClass().getName(),
        hashCode(),
        sslSocketFactory,
        hostnameVerifier,
        ldapUrl,
        timeout,
        bindDn,
        saslConfig,
        operationRetry,
        operationRetryWait,
        operationRetryBackoff,
        ssl,
        tls);
  }
}
