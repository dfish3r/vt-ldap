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

/**
 * Provides an interface for connection pooling.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ConnectionPool
{


  /** Initialize this pool for use. */
  void initialize();


  /**
   * Returns an object from the pool.
   *
   * @return  pooled object
   *
   * @throws  PoolException  if this operation fails
   * @throws  BlockingTimeoutException  if this pool is configured with a block
   * time and it occurs
   * @throws  PoolInterruptedException  if this pool is configured with a block
   * time and the current thread is interrupted
   */
  Connection getConnection()
    throws PoolException;


  /** Empty this pool, freeing any resources. */
  void close();
}
