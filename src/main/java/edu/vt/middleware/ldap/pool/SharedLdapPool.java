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
import edu.vt.middleware.ldap.LdapConnection;

/**
 * <code>SharedLdapPool</code> implements a pool of ldap objects that has a set
 * minimum and maximum size. The pool will not grow beyond the maximum size and
 * when the pool is exhausted, requests for new objects will be serviced by
 * objects that are already in use. Since {@link edu.vt.middleware.ldap.Ldap} is
 * a thread safe object this implementation leverages that by sharing ldap
 * objects among requests. This implementation should be used when you want some
 * control over the maximum number of ldap connections, but can tolerate some
 * new connections under high load. See {@link AbstractLdapPool}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SharedLdapPool extends AbstractLdapPool<LdapConnection>
{


  /**
   * Creates a new ldap pool with the supplied ldap factory.
   *
   * @param  lf  ldap factory
   */
  public SharedLdapPool(final LdapFactory<LdapConnection> lf)
  {
    super(new LdapPoolConfig(), lf);
  }


  /**
   * Creates a new ldap pool with the supplied ldap config and factory.
   *
   * @param  lpc  ldap pool configuration
   * @param  lf  ldap factory
   */
  public SharedLdapPool(
    final LdapPoolConfig lpc, final LdapFactory<LdapConnection> lf)
  {
    super(lpc, lf);
  }


  /** {@inheritDoc} */
  @Override
  public LdapConnection checkOut()
    throws LdapPoolException
  {
    LdapConnection lc = null;
    boolean create = false;
    logger.trace(
      "waiting on pool lock for check out {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      // if an available object exists, use it
      // if no available objects and the pool can grow, attempt to create
      // otherwise the pool is full, return a shared object
      if (active.size() < available.size()) {
        logger.trace("retrieve available ldap object");
        lc = retrieveAvailable();
      } else if (active.size() < poolConfig.getMaxPoolSize()) {
        logger.trace("pool can grow, attempt to create ldap object");
        create = true;
      } else {
        logger.trace(
          "pool is full, attempt to retrieve available ldap object");
        lc = retrieveAvailable();
      }
    } finally {
      poolLock.unlock();
    }

    if (create) {
      // previous block determined a creation should occur
      // block here until create occurs without locking the whole pool
      // if the pool is already maxed or creates are failing,
      // return a shared object
      checkOutLock.lock();
      try {
        boolean b = true;
        poolLock.lock();
        try {
          if (available.size() == poolConfig.getMaxPoolSize()) {
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
        logger.debug("create failed, retrieve available ldap object");
        lc = retrieveAvailable();
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
   * This attempts to retrieve an ldap object from the available queue. This
   * pooling implementation guarantees there is always an object available.
   *
   * @return  ldap connection from the pool
   *
   * @throws  IllegalStateException  if an object cannot be removed from the
   * available queue
   */
  protected LdapConnection retrieveAvailable()
  {
    LdapConnection lc = null;
    logger.trace(
      "waiting on pool lock for retrieve available {}",
      poolLock.getQueueLength());
    poolLock.lock();
    try {
      try {
        final PooledLdapConnection<LdapConnection> pl = available.remove();
        active.add(
          new PooledLdapConnection<LdapConnection>(pl.getLdapConnection()));
        available.add(
          new PooledLdapConnection<LdapConnection>(pl.getLdapConnection()));
        lc = pl.getLdapConnection();
        logger.trace("retrieved available ldap connection: {}", lc);
      } catch (NoSuchElementException e) {
        logger.error("could not remove ldap object from list", e);
        throw new IllegalStateException("Pool is empty", e);
      }
    } finally {
      poolLock.unlock();
    }
    return lc;
  }


  /** {@inheritDoc} */
  @Override
  public void checkIn(final LdapConnection lc)
  {
    final boolean valid = validateAndPassivate(lc);
    final PooledLdapConnection<LdapConnection> pl =
      new PooledLdapConnection<LdapConnection>(lc);
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
