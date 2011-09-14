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
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.DefaultConnectionFactory;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.provider.Provider;

/**
 * Provides an interface for creating provider connections.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PooledConnectionFactory implements ConnectionFactory
{

  /** Static reference to the default ldap provider. */
  protected static final Provider<?> DEFAULT_PROVIDER =
    DefaultConnectionFactory.getDefaultProvider();

  /** Provider used by this factory. */
  protected Provider<?> provider = DEFAULT_PROVIDER.newInstance();;

  /** Connection configuration used by this factory. */
  protected ConnectionConfig config;

  /** Connection pool. */
  protected ConnectionPool pool;

  /** Connection pool type. */
  protected ConnectionPoolType poolType = ConnectionPoolType.BLOCKING;

  /** Pool config. */
  protected PoolConfig poolConfig = new PoolConfig();


  /**
   * Default constructor.
   */
  public PooledConnectionFactory() {}


  /**
   * Creates a new pooled connection factory.
   *
   * @param  ldapUrl  to connect to
   */
  public PooledConnectionFactory(final String ldapUrl)
  {
    config = new ConnectionConfig(ldapUrl);
  }


  /**
   * Creates a new pooled connection factory.
   *
   * @param  cc  connection configuration
   */
  public PooledConnectionFactory(final ConnectionConfig cc)
  {
    config = cc;
  }


  /**
   * Creates a new pooled connection factory.
   *
   * @param  cc  connection configuration
   * @param  p  provider
   */
  public PooledConnectionFactory(final ConnectionConfig cc, final Provider<?> p)
  {
    config = cc;
    provider = p;
  }


  /** {@inheritDoc} */
  @Override
  public ConnectionConfig getConnectionConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setConnectionConfig(final ConnectionConfig cc)
  {
    if (pool != null) {
      throw new IllegalStateException(
        "Cannot set configuration after factory has been initialized");
    }
    config = cc;
  }


  /** {@inheritDoc} */
  @Override
  public Provider<?> getProvider()
  {
    return provider;
  }


  /** {@inheritDoc} */
  @Override
  public void setProvider(final Provider<?> p)
  {
    if (pool != null) {
      throw new IllegalStateException(
        "Cannot set provider after factory has been initialized");
    }
    provider = p;
  }


  /**
   * Returns the pool type.
   *
   * @return  pool type
   */
  public ConnectionPoolType getPoolType()
  {
    return poolType;
  }


  /**
   * Sets the pool type.
   *
   * @param  pt  pool type
   */
  public void setPoolType(final ConnectionPoolType pt)
  {
    if (pool != null) {
      throw new IllegalStateException(
        "Cannot set pool type after factory has been initialized");
    }
    poolType = pt;
  }


  /**
   * Returns the pool config.
   *
   * @return  pool config
   */
  public PoolConfig getPoolConfig()
  {
    return poolConfig;
  }


  /**
   * Sets the pool config.
   *
   * @param  pc  pool config
   */
  public void setPoolConfig(final PoolConfig pc)
  {
    if (pool != null) {
      throw new IllegalStateException(
        "Cannot set pool config after factory has been initialized");
    }
    poolConfig = pc;
  }


  /**
   * Prepares this connection factory for use. Must be called before
   * {@link #getConnection()}.
   */
  public void initialize()
  {
    if (ConnectionPoolType.BLOCKING == poolType) {
      pool = new BlockingConnectionPool(
        poolConfig, new DefaultConnectionFactory(config, provider));
    } else if (ConnectionPoolType.SOFTLIMIT == poolType) {
      pool = new SoftLimitConnectionPool(
        poolConfig, new DefaultConnectionFactory(config, provider));
    } else {
      throw new IllegalArgumentException("Unknown pool type: " + poolType);
    }
    pool.initialize();
  }


  /**
   * Returns a connection from the pool. Connections returned from this method
   * are ready to perform ldap operations.
   *
   * @return  connection
   *
   * @throws  LdapException  if a connection cannot be retrieved from the pool
   */
  public Connection getConnection()
    throws LdapException
  {
    return pool.getConnection();
  }


  /**
   * Closes the underlying connection pool.
   */
  public void close()
  {
    pool.close();
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
        "[%s@%d::provider=%s, config=%s, pool=%s]",
        getClass().getName(),
        hashCode(),
        provider,
        config,
        pool);
  }
}
