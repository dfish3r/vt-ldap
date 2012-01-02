/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.LdapException;

/**
 * Leverages a pool to obtain connections for performing ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PooledConnectionFactory implements ConnectionFactory
{

  /** Connection pool. */
  private ConnectionPool pool;


  /** Default constructor. */
  public PooledConnectionFactory() {}


  /**
   * Creates a new pooled connection factory.
   *
   * @param  cp  connection pool
   */
  public PooledConnectionFactory(final ConnectionPool cp)
  {
    pool = cp;
  }


  /**
   * Returns the connection pool.
   *
   * @return  connection pool
   */
  public ConnectionPool getConnectionPool()
  {
    return pool;
  }


  /**
   * Sets the connection pool.
   *
   * @param  cp  connection pool
   */
  public void setConnectionPool(final ConnectionPool cp)
  {
    pool = cp;
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
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format("[%s@%d::pool=%s]", getClass().getName(), hashCode(), pool);
  }
}
