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
 * <code>BlockingLdapPool</code> implements a pool of ldap objects that has a
 * set minimum and maximum size. The pool will not grow beyond the maximum size
 * and when the pool is exhausted, requests for new objects will block. The
 * length of time the pool will block is determined by {@link
 * #getBlockWaitTime()}. By default the pool will block indefinitely and there
 * is no guarantee that waiting threads will be serviced in the order in which
 * they made their request. This implementation should be used when you need to
 * control the <em>exact</em> number of ldap connections that can be created.
 * See {@link AbstractLdapPool}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BlockingLdapPool extends AbstractLdapPool<Connection>
{

  /** Time in milliseconds to wait for an available ldap object. */
  private long blockWaitTime;


  /**
   * Creates a new ldap pool with the supplied ldap factory.
   *
   * @param  lf  ldap factory
   */
  public BlockingLdapPool(final LdapFactory<Connection> lf)
  {
    super(new LdapPoolConfig(), lf);
  }


  /**
   * Creates a new ldap pool with the supplied ldap config and factory.
   *
   * @param  lpc  ldap pool configuration
   * @param  lf  ldap factory
   */
  public BlockingLdapPool(
    final LdapPoolConfig lpc, final LdapFactory<Connection> lf)
  {
    super(lpc, lf);
  }


  /**
   * Returns the block wait time. Default time is 0, which will wait
   * indefinitely.
   *
   * @return  time in milliseconds to wait for available ldap objects
   */
  public long getBlockWaitTime()
  {
    return blockWaitTime;
  }


  /**
   * Sets the block wait time. Default time is 0, which will wait indefinitely.
   *
   * @param  time  in milliseconds to wait for available ldap objects
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
    throws LdapPoolException
  {
    Connection lc = null;
    boolean create = false;
    logger.trace(
      "waiting on pool lock for check out {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      // if an available object exists, use it
      // if no available objects and the pool can grow, attempt to create
      // otherwise the pool is full, block until an object is returned
      if (available.size() > 0) {
        try {
          logger.trace("retrieve available ldap object");
          lc = retrieveAvailable();
        } catch (NoSuchElementException e) {
          logger.error("could not remove ldap object from list", e);
          throw new IllegalStateException("Pool is empty", e);
        }
      } else if (active.size() < poolConfig.getMaxPoolSize()) {
        logger.trace("pool can grow, attempt to create ldap object");
        create = true;
      } else {
        logger.trace("pool is full, block until ldap object is available");
        lc = blockAvailable();
      }
    } finally {
      poolLock.unlock();
    }

    if (create) {
      // previous block determined a creation should occur
      // block here until create occurs without locking the whole pool
      // if the pool is already maxed or creates are failing,
      // block until an object is available
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
          lc = createActive();
          logger.trace("created new active ldap object: {}", lc);
        }
      } finally {
        checkOutLock.unlock();
      }
      if (lc == null) {
        logger.debug(
          "create failed, block until ldap object is available");
        lc = blockAvailable();
      }
    }

    if (lc != null) {
      activateAndValidate(lc);
    } else {
      logger.error("Could not service check out request");
      throw new LdapPoolExhaustedException(
        "Pool is empty and object creation failed");
    }

    return lc;
  }


  /**
   * This attempts to retrieve an ldap object from the available queue.
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
   * This blocks until an ldap object can be aquired.
   *
   * @return  ldap object from the pool
   *
   * @throws  LdapPoolException  if this method fails
   * @throws  BlockingTimeoutException  if this pool is configured with a block
   * time and it occurs
   * @throws  PoolInterruptedException  if the current thread is interrupted
   */
  protected Connection blockAvailable()
    throws LdapPoolException
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
      logger.error("waiting for available object interrupted", e);
      throw new PoolInterruptedException(
        "Interrupted while waiting for an available object",
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
