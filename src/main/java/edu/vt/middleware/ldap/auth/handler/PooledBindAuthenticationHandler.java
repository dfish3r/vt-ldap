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
package edu.vt.middleware.ldap.auth.handler;

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
 * Provides an LDAP authentication implementation that leverages a pool of LDAP
 * connections to perform the LDAP bind operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PooledBindAuthenticationHandler extends BindAuthenticationHandler
  implements ManagedAuthenticationHandler
{

  /** Connection pool. */
  protected ConnectionPool pool;

  /** Connection pool type. */
  protected ConnectionPoolType poolType = ConnectionPoolType.BLOCKING;

  /** Pool config. */
  protected PoolConfig poolConfig = new PoolConfig();


  /** Default constructor. */
  public PooledBindAuthenticationHandler() {}


  /**
   * Creates a new pooled bind authentication handler.
   *
   * @param  cc  connection config
   */
  public PooledBindAuthenticationHandler(final ConnectionConfig cc)
  {
    setConnectionConfig(cc);
  }


  /**
   * Creates a new pooled bind authentication handler.
   *
   * @param  pc  pool config
   * @param  cc  connection config
   */
  public PooledBindAuthenticationHandler(
    final PoolConfig pc, final ConnectionConfig cc)
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
  public Connection authenticate(final AuthenticationCriteria ac)
    throws LdapException
  {
    Connection conn = null;
    try {
      conn = pool.getConnection();
    } catch (PoolException e) {
      logger.error("Could not retrieve connection from pool", e);
      throw new LdapException(e);
    }
    boolean closeConn = false;
    try {
      conn.bind(ac.getDn(), ac.getCredential());
    } catch (LdapException e) {
      closeConn = true;
      throw e;
    } finally {
      if (closeConn) {
        conn.close();
      }
    }
    return conn;
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
        "[%s@%d::pool=%s]",
        getClass().getName(),
        hashCode(),
        pool);
  }
}
