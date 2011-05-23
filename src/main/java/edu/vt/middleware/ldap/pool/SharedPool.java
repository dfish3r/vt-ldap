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
 * size. The pool will not grow beyond the maximum size and when the pool is
 * exhausted, requests for new objects will be serviced by objects that are
 * already in use. Since {@link edu.vt.middleware.ldap.Connection} is a thread
 * safe object this implementation leverages that by sharing ldap connections
 * among requests. This implementation should be used when you want some control
 * over the maximum number of ldap connections, but can tolerate some new
 * connections under high load. See {@link AbstractPool}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SharedPool extends AbstractPool<Connection>
{


  /**
   * Creates a new ldap pool.
   *
   * @param  cf  connection factory
   */
  public SharedPool(final ConnectionFactory<Connection> cf)
  {
    super(new PoolConfig(), cf);
  }


  /**
   * Creates a new ldap pool.
   *
   * @param  pc  pool configuration
   * @param  cf  connection factory
   */
  public SharedPool(final PoolConfig pc, final ConnectionFactory<Connection> cf)
  {
    super(pc, cf);
  }


  /** {@inheritDoc} */
  @Override
  public Connection checkOut()
    throws PoolException
  {
    Connection lc = null;
    boolean create = false;
    logger.trace(
      "waiting on pool lock for check out {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      // if an available connection exists, use it
      // if no available connections and the pool can grow, attempt to create
      // otherwise the pool is full, return a shared connection
      if (active.size() < available.size()) {
        logger.trace("retrieve available ldap connection");
        lc = retrieveAvailable();
      } else if (active.size() < config.getMaxPoolSize()) {
        logger.trace("pool can grow, attempt to create ldap connection");
        create = true;
      } else {
        logger.trace(
          "pool is full, attempt to retrieve available ldap connection");
        lc = retrieveAvailable();
      }
    } finally {
      poolLock.unlock();
    }

    if (create) {
      // previous block determined a creation should occur
      // block here until create occurs without locking the whole pool
      // if the pool is already maxed or creates are failing,
      // return a shared connection
      checkOutLock.lock();
      try {
        boolean b = true;
        poolLock.lock();
        try {
          if (available.size() == config.getMaxPoolSize()) {
            b = false;
          }
        } finally {
          poolLock.unlock();
        }
        if (b) {
          lc = createAvailableAndActive();
          logger.trace(
            "created new available and active ldap connection: {}", lc);
        }
      } finally {
        checkOutLock.unlock();
      }
      if (lc == null) {
        logger.debug("create failed, retrieve available ldap connection");
        lc = retrieveAvailable();
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


  /**
   * Attempts to retrieve an ldap connection from the available queue. This
   * pooling implementation guarantees there is always an object available.
   *
   * @return  ldap connection from the pool
   *
   * @throws  IllegalStateException  if a connection cannot be removed from the
   * available queue
   */
  protected Connection retrieveAvailable()
  {
    Connection lc = null;
    logger.trace(
      "waiting on pool lock for retrieve available {}",
      poolLock.getQueueLength());
    poolLock.lock();
    try {
      try {
        final PooledConnection<Connection> pl = available.remove();
        active.add(
          new PooledConnection<Connection>(pl.getConnection()));
        available.add(
          new PooledConnection<Connection>(pl.getConnection()));
        lc = pl.getConnection();
        logger.trace("retrieved available ldap connection: {}", lc);
      } catch (NoSuchElementException e) {
        logger.error("could not remove ldap connection from list", e);
        throw new IllegalStateException("Pool is empty", e);
      }
    } finally {
      poolLock.unlock();
    }
    return lc;
  }


  /** {@inheritDoc} */
  @Override
  public void checkIn(final Connection lc)
  {
    final boolean valid = validateAndPassivate(lc);
    final PooledConnection<Connection> pl =
      new PooledConnection<Connection>(lc);
    logger.trace(
      "waiting on pool lock for check in {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      if (active.remove(pl)) {
        logger.debug("returned active ldap connection: {}", lc);
      } else if (available.contains(pl)) {
        logger.warn("returned available ldap connection: {}", lc);
      } else {
        logger.warn("attempt to return unknown ldap connection: {}", lc);
      }
      if (!valid) {
        available.remove(pl);
      }
    } finally {
      poolLock.unlock();
    }
  }
}
