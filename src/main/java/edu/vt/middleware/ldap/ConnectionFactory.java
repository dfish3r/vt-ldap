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

import edu.vt.middleware.ldap.provider.Provider;
import edu.vt.middleware.ldap.provider.jndi.JndiProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates connections
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ConnectionFactory
{

  /** Ldap provider class name. */
  public static final String PROVIDER = "edu.vt.middleware.ldap.provider";

  /** Static reference to the default ldap provider. */
  protected static final Provider<?> DEFAULT_PROVIDER = getDefaultProvider();

  /** Provider used by this factory. */
  protected Provider<?> provider = DEFAULT_PROVIDER.newInstance();;

  /** Connection configuration used by this factory. */
  protected ConnectionConfig config;


  /**
   * Default constructor.
   */
  public ConnectionFactory() {}


  /**
   * Creates a new connection factory.
   *
   * @param  ldapUrl  to connect to
   */
  public ConnectionFactory(final String ldapUrl)
  {
    config = new ConnectionConfig(ldapUrl);
  }


  /**
   * Creates a new connection factory.
   *
   * @param  cc  connection configuration
   */
  public ConnectionFactory(final ConnectionConfig cc)
  {
    config = cc;
  }


  /**
   * Creates a new connection factory.
   *
   * @param  cc  connection configuration
   * @param  p  provider
   */
  public ConnectionFactory(final ConnectionConfig cc, final Provider<?> p)
  {
    config = cc;
    provider = p;
  }


  /**
   * Returns the connection config.
   *
   * @return  connection config
   */
  public ConnectionConfig getConnectionConfig()
  {
    return config;
  }


  /**
   * Sets the connection config.
   *
   * @param  cc  connection config
   */
  public void setConnectionConfig(final ConnectionConfig cc)
  {
    config = cc;
  }


  /**
   * Returns the ldap provider.
   *
   * @return  ldap provider
   */
  public Provider<?> getProvider()
  {
    return provider;
  }


  /**
   * Sets the ldap provider.
   *
   * @param  p  ldap provider to set
   */
  public void setProvider(final Provider<?> p)
  {
    provider = p;
  }


  /**
   * Creates a new connection. Connections returned from this method must be
   * opened before they can perform ldap operations.
   *
   * @return  connection
   */
  public Connection getConnection()
  {
    return new Connection(config, provider.getConnectionFactory(config));
  }


  /**
   * Creates a new connection. Connections returned from this method must be
   * opened before they can be used.
   *
   * @param  ldapUrl  to connect to
   *
   * @return  connection
   */
  public static Connection getConnection(final String ldapUrl)
  {
    final Provider<?> p = DEFAULT_PROVIDER.newInstance();
    final ConnectionConfig cc = new ConnectionConfig(ldapUrl);
    return new Connection(cc, p.getConnectionFactory(cc));
  }


  /**
   * Creates a new connection. Connections returned from this method must be
   * opened before they can be used.
   *
   * @param  cc  connection configuration
   *
   * @return  connection
   */
  public static Connection getConnection(final ConnectionConfig cc)
  {
    final Provider<?> p = DEFAULT_PROVIDER.newInstance();
    return new Connection(cc, p.getConnectionFactory(cc));
  }


  /**
   * The {@link #LDAP_PROVIDER} property is checked and that class is loaded if
   * provided. Otherwise the JNDI provider is returned.
   *
   * @return  default provider
   */
  public static Provider<?> getDefaultProvider()
  {
    Provider<?> p = null;
    final String providerClass = System.getProperty(PROVIDER);
    if (providerClass != null) {
      final Logger l = LoggerFactory.getLogger(ConnectionConfig.class);
      try {
        if (l.isInfoEnabled()) {
          l.info("Setting ldap provider to {}", providerClass);
        }
        p = (Provider<?>) Class.forName(providerClass).newInstance();
      } catch (Exception e) {
        if (l.isErrorEnabled()) {
          l.error("Error instantiating {}", providerClass, e);
        }
        throw new IllegalStateException(e);
      }
    } else {
      // set the default ldap provider to JNDI
      p = new JndiProvider();
    }
    return p;
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
        "[%s@%d::provider=%s, config=%s]",
        getClass().getName(),
        hashCode(),
        provider,
        config);
  }
}
