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
import java.util.concurrent.TimeUnit;
import edu.vt.middleware.ldap.Connection;

/**
 * Implements a pool of ldap connections that has a set minimum and maximum
 * size. The pool will not grow beyond the maximum size and when the pool is
 * exhausted, requests for new connections will block. The length of time the
 * pool will block is determined by {@link #getBlockWaitTime()}. By default the
 * pool will block indefinitely and there is no guarantee that waiting threads
 * will be serviced in the order in which they made their request. This
 * implementation should be used when you need to control the <em>exact</em>
 * number of ldap connections that can be created. See
 * {@link AbstractPool}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BlockingPool extends AbstractPool<Connection>
{

  /** Time in milliseconds to wait for an available ldap connection. */
  private long blockWaitTime;


  /**
   * Creates a new ldap pool.
   *
   * @param  cf  connection factory
   */
  public BlockingPool(final ConnectionFactory<Connection> cf)
  {
    super(new PoolConfig(), cf);
  }


  /**
   * Creates a new ldap pool.
   *
   * @param  cpc  connection pool configuration
   * @param  cf  connection factory
   */
  public BlockingPool(
    final PoolConfig cpc, final ConnectionFactory<Connection> cf)
  {
    super(cpc, cf);
  }


  /**
   * Returns the block wait time. Default time is 0, which will wait
   * indefinitely.
   *
   * @return  time in milliseconds to wait for available ldap connections
   */
  public long getBlockWaitTime()
  {
    return blockWaitTime;
  }


  /**
   * Sets the block wait time. Default time is 0, which will wait indefinitely.
   *
   * @param  time  in milliseconds to wait for available ldap connections
   */
  public void setBlockWaitTime(final long time)
  {
    if (time >= 0) {
      blockWaitTime = time;
    }
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
      // otherwise the pool is full, block until a connection is returned
      if (available.size() > 0) {
        try {
          logger.trace("retrieve available ldap connection");
          lc = retrieveAvailable();
        } catch (NoSuchElementException e) {
          logger.error("could not remove ldap connection from list", e);
          throw new IllegalStateException("Pool is empty", e);
        }
      } else if (active.size() < config.getMaxPoolSize()) {
        logger.trace("pool can grow, attempt to create ldap connection");
        create = true;
      } else {
        logger.trace("pool is full, block until ldap connection is available");
        lc = blockAvailable();
      }
    } finally {
      poolLock.unlock();
    }

    if (create) {
      // previous block determined a creation should occur
      // block here until create occurs without locking the whole pool
      // if the pool is already maxed or creates are failing,
      // block until a connection is available
      checkOutLock.lock();
      try {
        boolean b = true;
        poolLock.lock();
        try {
          if (
            available.size() + active.size() ==
              config.getMaxPoolSize()) {
            b = false;
          }
        } finally {
          poolLock.unlock();
        }
        if (b) {
          lc = createActive();
          logger.trace("created new active ldap connection: {}", lc);
        }
      } finally {
        checkOutLock.unlock();
      }
      if (lc == null) {
        logger.debug(
          "create failed, block until ldap connection is available");
        lc = blockAvailable();
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
   * Attempts to retrieve an ldap connection from the available queue.
   *
   * @return  ldap connection from the pool
   *
   * @throws  NoSuchElementException  if the available queue is empty
   */
  protected Connection retrieveAvailable()
  {
    Connection lc = null;
    logger.trace(
      "waiting on pool lock for retrieve available {}",
      poolLock.getQueueLength());
    poolLock.lock();
    try {
      final PooledConnection<Connection> pl = available.remove();
      active.add(
        new PooledConnection<Connection>(pl.getConnection()));
      lc = pl.getConnection();
      logger.trace("retrieved available ldap connection: {}", lc);
    } finally {
      poolLock.unlock();
    }
    return lc;
  }


  /**
   * This blocks until an ldap connection can be acquired.
   *
   * @return  ldap connection from the pool
   *
   * @throws  PoolException  if this method fails
   * @throws  BlockingTimeoutException  if this pool is configured with a block
   * time and it occurs
   * @throws  PoolInterruptedException  if the current thread is interrupted
   */
  protected Connection blockAvailable()
    throws PoolException
  {
    Connection lc = null;
    logger.trace(
      "waiting on pool lock for block available {}",
      poolLock.getQueueLength());
    poolLock.lock();
    try {
      while (lc == null) {
        logger.trace("available pool is empty, waiting...");
        if (blockWaitTime > 0) {
          if (
            !poolNotEmpty.await(
                blockWaitTime,
                TimeUnit.MILLISECONDS)) {
            logger.debug("block time exceeded, throwing exception");
            throw new BlockingTimeoutException("Block time exceeded");
          }
        } else {
          poolNotEmpty.await();
        }
        logger.trace("notified to continue...");
        try {
          lc = retrieveAvailable();
        } catch (NoSuchElementException e) {
          logger.trace("notified to continue but pool was empty");
        }
      }
    } catch (InterruptedException e) {
      logger.error("waiting for available connection interrupted", e);
      throw new PoolInterruptedException(
        "Interrupted while waiting for an available connection",
        e);
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
        if (valid) {
          available.add(pl);
          logger.trace("returned active ldap connection: {}", lc);
          poolNotEmpty.signal();
        }
      } else if (available.contains(pl)) {
        logger.warn("returned available ldap connection: {}", lc);
      } else {
        logger.warn("attempt to return unknown ldap connection: {}", lc);
      }
    } finally {
      poolLock.unlock();
    }
  }
}
