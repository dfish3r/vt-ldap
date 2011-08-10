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
import edu.vt.middleware.ldap.ConnectionConfig;

/**
 * Implements a pool of connections that has a set minimum and maximum size. The
 * pool will not grow beyond the maximum size and when the pool is exhausted,
 * requests for new connections will block. The length of time the pool will
 * block is determined by {@link #getBlockWaitTime()}. By default the pool will
 * block indefinitely and there is no guarantee that waiting threads will be
 * serviced in the order in which they made their request. This implementation
 * should be used when you need to control the <em>exact</em> number of
 * connections that can be created. See {@link AbstractConnectionPool}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BlockingConnectionPool extends AbstractConnectionPool
  implements ConnectionPool
{

  /** Time in milliseconds to wait for an available connection. */
  private long blockWaitTime;


  /**
   * Creates a new blocking pool.
   *
   * @param  cc  connection configuration
   */
  public BlockingConnectionPool(final ConnectionConfig cc)
  {
    super(new PoolConfig(), cc);
  }


  /**
   * Creates a new blocking pool.
   *
   * @param  pc  pool configuration
   * @param  cc  connection configuration
   */
  public BlockingConnectionPool(final PoolConfig pc, final ConnectionConfig cc)
  {
    super(pc, cc);
  }


  /**
   * Returns the block wait time. Default time is 0, which will wait
   * indefinitely.
   *
   * @return  time in milliseconds to wait for available connections
   */
  public long getBlockWaitTime()
  {
    return blockWaitTime;
  }


  /**
   * Sets the block wait time. Default time is 0, which will wait indefinitely.
   *
   * @param  time  in milliseconds to wait for available connections
   */
  public void setBlockWaitTime(final long time)
  {
    if (time >= 0) {
      blockWaitTime = time;
    }
  }


  /** {@inheritDoc} */
  @Override
  public Connection getConnection()
    throws PoolException
  {
    PooledConnection pc = null;
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
          logger.trace("retrieve available connection");
          pc = retrieveAvailable();
        } catch (NoSuchElementException e) {
          logger.error("could not remove connection from list", e);
          throw new IllegalStateException("Pool is empty", e);
        }
      } else if (active.size() < poolConfig.getMaxPoolSize()) {
        logger.trace("pool can grow, attempt to create connection");
        create = true;
      } else {
        logger.trace("pool is full, block until connection is available");
        pc = blockAvailable();
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
              poolConfig.getMaxPoolSize()) {
            b = false;
          }
        } finally {
          poolLock.unlock();
        }
        if (b) {
          pc = createActive();
          logger.trace("created new active connection: {}", pc);
        }
      } finally {
        checkOutLock.unlock();
      }
      if (pc == null) {
        logger.debug("create failed, block until connection is available");
        pc = blockAvailable();
      }
    }

    if (pc != null) {
      activateAndValidate(pc);
    } else {
      logger.error("Could not service check out request");
      throw new PoolExhaustedException(
        "Pool is empty and connection creation failed");
    }

    return pc;
  }


  /**
   * Attempts to retrieve a connection from the available queue.
   *
   * @return  connection from the pool
   *
   * @throws  NoSuchElementException  if the available queue is empty
   */
  protected PooledConnection retrieveAvailable()
  {
    PooledConnection pc = null;
    logger.trace(
      "waiting on pool lock for retrieve available {}",
      poolLock.getQueueLength());
    poolLock.lock();
    try {
      pc = available.remove();
      active.add(pc);
      logger.trace("retrieved available connection: {}", pc);
    } finally {
      poolLock.unlock();
    }
    return pc;
  }


  /**
   * This blocks until a connection can be acquired.
   *
   * @return  connection from the pool
   *
   * @throws  PoolException  if this method fails
   * @throws  BlockingTimeoutException  if this pool is configured with a block
   * time and it occurs
   * @throws  PoolInterruptedException  if the current thread is interrupted
   */
  protected PooledConnection blockAvailable()
    throws PoolException
  {
    PooledConnection pc = null;
    logger.trace(
      "waiting on pool lock for block available {}",
      poolLock.getQueueLength());
    poolLock.lock();
    try {
      while (pc == null) {
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
          pc = retrieveAvailable();
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
    return pc;
  }


  /** {@inheritDoc} */
  @Override
  protected void putConnection(final PooledConnection pc)
  {
    final boolean valid = validateAndPassivate(pc);
    logger.trace(
      "waiting on pool lock for check in {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      if (active.remove(pc)) {
        if (valid) {
          available.add(pc);
          logger.trace("returned active connection: {}", pc);
          poolNotEmpty.signal();
        }
      } else if (available.contains(pc)) {
        logger.warn("returned available connection: {}", pc);
      } else {
        logger.warn("attempt to return unknown connection: {}", pc);
      }
    } finally {
      poolLock.unlock();
    }
  }
}
