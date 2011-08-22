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
package edu.vt.middleware.ldap.auth;

import java.util.Arrays;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.pool.BlockingConnectionPool;
import edu.vt.middleware.ldap.pool.ConnectionPool;
import edu.vt.middleware.ldap.pool.ConnectionPoolType;
import edu.vt.middleware.ldap.pool.PoolConfig;
import edu.vt.middleware.ldap.pool.PoolException;
import edu.vt.middleware.ldap.pool.SoftLimitConnectionPool;

/**
 * Looks up a user's DN using a pool of LDAP connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PooledSearchDnResolver extends SearchDnResolver
  implements ManagedDnResolver
{

  /** serial version uid. */
  private static final long serialVersionUID = -1728263498360652469L;

  /** Connection pool. */
  protected ConnectionPool pool;

  /** Connection pool type. */
  protected ConnectionPoolType poolType = ConnectionPoolType.BLOCKING;

  /** Pool config. */
  protected PoolConfig poolConfig = new PoolConfig();


  /** Default constructor. */
  public PooledSearchDnResolver() {}


  /**
   * Creates a new pooled search dn resolver.
   *
   * @param  pc  pool config
   * @param  cc  connection config
   */
  public PooledSearchDnResolver(final PoolConfig pc, final ConnectionConfig cc)
  {
    setPoolConfig(pc);
    setConnectionConfig(cc);
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
    poolConfig = pc;
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    if (ConnectionPoolType.BLOCKING == poolType) {
      pool = new BlockingConnectionPool(poolConfig, config);
    } else if (ConnectionPoolType.SOFTLIMIT == poolType) {
      pool = new SoftLimitConnectionPool(poolConfig, config);
    } else {
      throw new IllegalArgumentException("Unknown pool type: " + poolType);
    }
    pool.initialize();
  }


  /** {@inheritDoc} */
  @Override
  protected Connection getConnection()
    throws LdapException
  {
    try {
      return pool.getConnection();
    } catch (PoolException e) {
      logger.error("Could not retrieve connection from pool", e);
      throw new LdapException(e);
    }
  }


  /** {@inheritDoc} */
  @Override
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
        "[%s@%d::baseDn=%s, userFilter=%s, userFilterArgs=%s, " +
        "allowMultipleDns=%s, subtreeSearch=%s, pool=%s]",
        getClass().getName(),
        hashCode(),
        baseDn,
        userFilter,
        userFilterArgs != null ? Arrays.asList(userFilterArgs) : null,
        allowMultipleDns,
        subtreeSearch,
        pool);
  }
}
