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
import edu.vt.middleware.ldap.provider.ConnectionFactory;
import edu.vt.middleware.ldap.provider.ConnectionStrategy;
import edu.vt.middleware.ldap.provider.LdapProvider;
import edu.vt.middleware.ldap.provider.jndi.JndiProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains all the configuration data needed to control LDAP connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapConnectionConfig extends AbstractConfig
{
  /** ldap provider class name. */
  public static final String LDAP_PROVIDER = "edu.vt.middleware.ldap.provider";

  /** Ldap provider implementation. */
  private LdapProvider ldapProvider = getDefaultLdapProvider();

  /** Default connection handler. */
  private ConnectionFactory connectionFactory;

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

  /** Authentication mechanism to use when binding to the LDAP. */
  private String authtype = "simple";

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


  /** Default constructor. */
  public LdapConnectionConfig() {}


  /**
   * Creates a new ldap config.
   *
   * @param  ldapUrl  to connect to
   */
  public LdapConnectionConfig(final String ldapUrl)
  {
    this();
    this.setLdapUrl(ldapUrl);
  }


  /**
   * Returns the default ldap provider. The {@link #LDAP_PROVIDER} property
   * is checked and that class is loaded if provided. Otherwise the JNDI
   * provider is returned.
   *
   * @return  default ldap provider
   */
  protected static LdapProvider getDefaultLdapProvider()
  {
    final String ldapProviderClass = System.getProperty(LDAP_PROVIDER);
    if (ldapProviderClass != null) {
      final Log log = LogFactory.getLog(LdapConnectionConfig.class);
      try {
        if (log.isInfoEnabled()) {
          log.info("Setting ldap provider to " + ldapProviderClass);
        }
        return (LdapProvider) Class.forName(ldapProviderClass).newInstance();
      } catch (ClassNotFoundException e) {
        if (log.isErrorEnabled()) {
          log.error("Error instantiating " + ldapProviderClass, e);
        }
      } catch (InstantiationException e) {
        if (log.isErrorEnabled()) {
          log.error("Error instantiating " + ldapProviderClass, e);
        }
      } catch (IllegalAccessException e) {
        if (log.isErrorEnabled()) {
          log.error("Error instantiating " + ldapProviderClass, e);
        }
      }
    }
    // set the default ldap provider to JNDI
    return new JndiProvider();
  }


  /**
   * Returns the ldap provider.
   *
   * @return  ldap provider
   */
  public LdapProvider getLdapProvider()
  {
    return this.ldapProvider;
  }


  /**
   * Sets the ldap provider.
   *
   * @param  lp  ldap provider to set
   */
  public void setLdapProvider(final LdapProvider lp)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting ldapProvider: " + lp);
    }
    this.ldapProvider = lp;
  }


  /**
   * Returns the connection factory.
   *
   * @return  connection factory
   */
  public ConnectionFactory getConnectionFactory()
  {
    if (this.connectionFactory == null) {
      this.connectionFactory = this.ldapProvider.getConnectionFactory(this);
    }
    return this.connectionFactory;
  }


  /**
   * Sets the connection factory.
   *
   * @param  cf  connection factory
   */
  public void setConnectionFactory(final ConnectionFactory cf)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting connectionFactory: " + cf);
    }
    this.connectionFactory = cf;
  }


  /**
   * Returns the SSL socket factory used when making SSL or TLS connections.
   *
   * @return  SSL socket factory
   */
  public SSLSocketFactory getSslSocketFactory()
  {
    return this.sslSocketFactory;
  }


  /**
   * Sets the SSL socket factory.
   *
   * @param  sf  SSL socket factory
   */
  public void setSslSocketFactory(final SSLSocketFactory sf)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting sslSocketFactory: " + sf);
    }
    this.sslSocketFactory = sf;
  }


  /**
   * Returns the hostname verifier used when making SSL or TLS connections.
   *
   * @return  hostname verifier
   */
  public HostnameVerifier getHostnameVerifier()
  {
    return this.hostnameVerifier;
  }


  /**
   * Sets the hostname verifier.
   *
   * @param  hv  hostname verifier
   */
  public void setHostnameVerifier(final HostnameVerifier hv)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting hostnameVerifier: " + hv);
    }
    this.hostnameVerifier = hv;
  }


  /**
   * Returns the ldap url.
   *
   * @return  ldap url
   */
  public String getLdapUrl()
  {
    return this.ldapUrl;
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
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting ldapUrl: " + url);
    }
    this.ldapUrl = url;
  }


  /**
   * Returns the connect timeout. If this value is 0, then connect operations
   * will wait indefinitely.
   *
   * @return  timeout
   */
  public long getTimeout()
  {
    return this.timeout;
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
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting timeout: " + l);
    }
    this.timeout = l;
  }


  /**
   * Returns the bind DN.
   *
   * @return  DN to bind as
   */
  public String getBindDn()
  {
    return this.bindDn;
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
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting bindDn: " + dn);
    }
    this.bindDn = dn;
  }


  /**
   * Returns the credential used with the bind DN.
   *
   * @return  bind DN credential
   */
  public Credential getBindCredential()
  {
    return this.bindCredential;
  }


  /**
   * Sets the credential of the bind DN.
   *
   * @param  credential  to use with bind DN
   */
  public void setBindCredential(final Credential credential)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      if (this.getLogCredentials()) {
        this.logger.trace("setting bindCredential: " + credential);
      } else {
        this.logger.trace("setting bindCredential: <suppressed>");
      }
    }
    this.bindCredential = credential;
  }


  /**
   * Returns the authentication type.
   *
   * @return  authentication type
   */
  public String getAuthtype()
  {
    return this.authtype;
  }


  /**
   * This sets the authentication type.
   *
   * @param  type  of authentication to use
   */
  public void setAuthtype(final String type)
  {
    checkImmutable();
    checkStringInput(type, false);
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting authtype: " + type);
    }
    this.authtype = type;
  }


  /**
   * Returns the number of times ldap operations will be retried if an operation
   * exception occurs. If this value is 0, no retries will occur.
   *
   * @return  number of retries
   */
  public int getOperationRetry()
  {
    return this.operationRetry;
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
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting operationRetry: " + i);
    }
    this.operationRetry = i;
  }


  /**
   * Returns the operation retry wait time.
   *
   * @return  retry wait
   */
  public long getOperationRetryWait()
  {
    return this.operationRetryWait;
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
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting operationRetryWait: " + l);
    }
    this.operationRetryWait = l;
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
    return this.operationRetryBackoff;
  }


  /**
   * Sets the factor by which to multiply the operation retry wait time.
   *
   * @param  backoff  factor to multiply wait time by
   */
  public void setOperationRetryBackoff(final int backoff)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting operationRetryBackoff: " + backoff);
    }
    this.operationRetryBackoff = backoff;
  }


  /**
   * Returns provider specific properties.
   *
   * @return  map of additional provider properties
   */
  public Map<String, Object> getProviderProperties()
  {
    return this.providerProperties;
  }


  /**
   * Sets provider specific properties.
   *
   * @param  props  map of additional provider properties
   */
  public void setProviderProperties(final Map<String, Object> props)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting provider properties " + props);
    }
    this.providerProperties = props;
  }


  /**
   * Returns whether authentication credentials will be logged.
   *
   * @return  whether authentication credentials will be logged.
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
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting logCredentials: " + b);
    }
    this.logCredentials = b;
  }


  /**
   * See {@link #isSslEnabled()}.
   *
   * @return  whether the SSL protocol will be used
   */
  public boolean getSsl()
  {
    return this.isSslEnabled();
  }


  /**
   * Returns whether the SSL protocol will be used for connections.
   *
   * @return  whether the SSL protocol will be used
   */
  public boolean isSslEnabled()
  {
    return this.ssl;
  }


  /**
   * Sets whether the SSL protocol will be used for connections.
   *
   * @param  b  whether the SSL protocol will be used
   */
  public void setSsl(final boolean b)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting ssl: " + b);
    }
    this.ssl = b;
  }


  /**
   * See {@link #isTlsEnabled()}.
   *
   * @return  whether the TLS protocol will be used
   */
  public boolean getTls()
  {
    return this.isTlsEnabled();
  }


  /**
   * Returns whether the TLS protocol will be used for connections.
   *
   * @return  whether the TLS protocol will be used
   */
  public boolean isTlsEnabled()
  {
    return this.tls;
  }


  /**
   * Sets whether the TLS protocol will be used for connections.
   *
   * @param  b  whether the TLS protocol will be used
   */
  public void setTls(final boolean b)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting tls: " + b);
    }
    this.tls = b;
  }


  /**
   * Returns the connection strategy.
   *
   * @return  connection strategy
   */
  public ConnectionStrategy getConnectionStrategy()
  {
    return this.connectionStrategy;
  }


  /**
   * Sets the connection strategy.
   *
   * @param  strategy  for making new connections
   */
  public void setConnectionStrategy(final ConnectionStrategy strategy)
  {
    checkImmutable();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting connectionStrategy: " + strategy);
    }
    this.connectionStrategy = strategy;
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
        "%s@%d::ldapProvider=%s, ldapUrl=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.getLdapProvider(),
        this.getLdapUrl());
  }
}
