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

import java.util.NoSuchElementException;
import edu.vt.middleware.ldap.Connection;

/**
 * Implements a pool of ldap connections that has a set minimum and maximum
 * size. The pool will grow beyond it's maximum size as necessary based on it's
 * current load. Pool size will return to it's minimum based on the
 * configuration of the prune period. See {@link
 * PoolConfig#setPrunePeriod} and {@link
 * PoolConfig#setExpirationTime}. This implementation should be used
 * when you have some flexibility in the number of ldap connections that can be
 * created to handle spikes in load. See {@link AbstractPool}. Note
 * that this pool will begin blocking if it cannot create new ldap connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SoftLimitPool extends BlockingPool
{


  /**
   * Creates a new ldap pool.
   *
   * @param  cf  connection factory
   */
  public SoftLimitPool(final ConnectionFactory<Connection> cf)
  {
    super(new PoolConfig(), cf);
  }


  /**
   * Creates a new ldap pool.
   *
   * @param  pc  pool configuration
   * @param  cf  connection factory
   */
  public SoftLimitPool(
    final PoolConfig pc, final ConnectionFactory<Connection> cf)
  {
    super(pc, cf);
  }


  /** {@inheritDoc} */
  @Override
  public Connection checkOut()
    throws PoolException
  {
    Connection lc = null;
    logger.trace(
      "waiting on pool lock for check out {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      // if an available connection exists, use it
      // if no available connections, attempt to create
      if (available.size() > 0) {
        try {
          logger.trace("retrieve available ldap connection");
          lc = retrieveAvailable();
        } catch (NoSuchElementException e) {
          logger.error("could not remove ldap connection from list", e);
          throw new IllegalStateException("Pool is empty", e);
        }
      }
    } finally {
      poolLock.unlock();
    }

    if (lc == null) {
      // no connection was available, create a new one
      lc = createActive();
      logger.trace("created new active ldap connection: {}", lc);
      if (lc == null) {
        // create failed, block until a connection is available
        logger.debug("created failed, block until a connection is available");
        lc = blockAvailable();
      } else {
        logger.trace("created new active ldap connection: {}", lc);
      }
    }

    if (lc != null) {
      activateAndValidate(lc);
    } else {
      logger.error("Could not service check out request");
      throw new PoolExhaustedException(
        "Pool is empty and connection creation failed");
    }

    return lc;
  }
}
