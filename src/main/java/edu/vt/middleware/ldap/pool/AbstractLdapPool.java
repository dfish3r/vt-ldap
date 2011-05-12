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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import edu.vt.middleware.ldap.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>AbstractLdapPool</code> contains the basic implementation for pooling
 * ldap objects. The main design objective for the supplied pooling
 * implementations is to provide a pool that does not block on object creation
 * or destruction. This is what accounts for the multiple locks available on
 * this class. The pool is backed by two queues, one for available objects and
 * one for active objects. Objects that are available for {@link #checkOut()}
 * exist in the available queue. Objects that are actively in use exist in the
 * active queue. Note that depending on the implementation an object can exist
 * in both queues at the same time.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractLdapPool<T extends LdapConnection>
  implements LdapPool<T>
{

  /** Lock for the entire pool. */
  protected final ReentrantLock poolLock = new ReentrantLock();

  /** Condition for notifying threads that an object was returned. */
  protected final Condition poolNotEmpty = poolLock.newCondition();

  /** Lock for check ins. */
  protected final ReentrantLock checkInLock = new ReentrantLock();

  /** Lock for check outs. */
  protected final ReentrantLock checkOutLock = new ReentrantLock();

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** List of available ldap objects in the pool. */
  protected Queue<PooledLdapConnection<T>> available =
    new LinkedList<PooledLdapConnection<T>>();

  /** List of ldap objects in use. */
  protected Queue<PooledLdapConnection<T>> active =
    new LinkedList<PooledLdapConnection<T>>();

  /** Ldap pool config. */
  protected LdapPoolConfig poolConfig;

  /** Factory to create ldap objects. */
  protected LdapFactory<T> ldapFactory;

  /** Timer for scheduling pool tasks. */
  private Timer poolTimer = new Timer(true);


  /**
   * Creates a new pool with the supplied pool configuration and ldap factory.
   * The pool configuration will be marked as immutable by this pool.
   *
   * @param  lpc  <code>LdapPoolConfig</code>
   * @param  lf  <code>LdapFactory</code>
   */
  public AbstractLdapPool(final LdapPoolConfig lpc, final LdapFactory<T> lf)
  {
    poolConfig = lpc;
    poolConfig.makeImmutable();
    ldapFactory = lf;
  }


  /** {@inheritDoc} */
  @Override
  public LdapPoolConfig getLdapPoolConfig()
  {
    return poolConfig;
  }


  /** {@inheritDoc} */
  @Override
  public void setPoolTimer(final Timer t)
  {
    poolTimer = t;
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
  {
    logger.debug("beginning pool initialization");

    poolTimer.scheduleAtFixedRate(
      new PrunePoolTask<T>(this),
      poolConfig.getPruneTimerPeriod(),
      poolConfig.getPruneTimerPeriod());
    logger.debug("prune pool task scheduled");

    poolTimer.scheduleAtFixedRate(
      new ValidatePoolTask<T>(this),
      poolConfig.getValidateTimerPeriod(),
      poolConfig.getValidateTimerPeriod());
    logger.debug("validate pool task scheduled");

    initializePool();

    logger.debug("pool initialized to size " + available.size());
  }


  /** Attempts to fill the pool to its minimum size. */
  private void initializePool()
  {
    logger.debug(
      "checking ldap pool size >= {}", poolConfig.getMinPoolSize());

    int count = 0;
    poolLock.lock();
    try {
      while (
        available.size() < poolConfig.getMinPoolSize() &&
          count < poolConfig.getMinPoolSize() * 2) {
        final T t = createAvailable();
        if (poolConfig.isValidateOnCheckIn()) {
          if (ldapFactory.validate(t)) {
            logger.trace(
              "ldap object passed initialize validation: {}", t);
          } else {
            logger.warn("ldap object failed initialize validation: {}", t);
            removeAvailable(t);
          }
        }
        count++;
      }
    } finally {
      poolLock.unlock();
    }
  }


  /** {@inheritDoc} */
  @Override
  public void close()
  {
    poolLock.lock();
    try {
      while (available.size() > 0) {
        final PooledLdapConnection<T> pl = available.remove();
        ldapFactory.destroy(pl.getLdapConnection());
      }
      while (active.size() > 0) {
        final PooledLdapConnection<T> pl = active.remove();
        ldapFactory.destroy(pl.getLdapConnection());
      }
      logger.debug("pool closed");
    } finally {
      poolLock.unlock();
    }

    poolTimer.cancel();
  }


  /**
   * Create a new ldap object and place it in the available pool.
   *
   * @return  ldap object that was placed in the available pool
   */
  protected T createAvailable()
  {
    final T t = ldapFactory.create();
    if (t != null) {
      final PooledLdapConnection<T> pl = new PooledLdapConnection<T>(t);
      poolLock.lock();
      try {
        available.add(pl);
      } finally {
        poolLock.unlock();
      }
    } else {
      logger.warn("unable to create available ldap object");
    }
    return t;
  }


  /**
   * Create a new ldap object and place it in the active pool.
   *
   * @return  ldap object that was placed in the active pool
   */
  protected T createActive()
  {
    final T t = ldapFactory.create();
    if (t != null) {
      final PooledLdapConnection<T> pl = new PooledLdapConnection<T>(t);
      poolLock.lock();
      try {
        active.add(pl);
      } finally {
        poolLock.unlock();
      }
    } else {
      logger.warn("unable to create active ldap object");
    }
    return t;
  }


  /**
   * Create a new ldap object and place it in both the available and active
   * pools.
   *
   * @return  ldap object that was placed in the available and active pools
   */
  protected T createAvailableAndActive()
  {
    final T t = ldapFactory.create();
    if (t != null) {
      final PooledLdapConnection<T> pl = new PooledLdapConnection<T>(t);
      poolLock.lock();
      try {
        available.add(pl);
        active.add(pl);
      } finally {
        poolLock.unlock();
      }
    } else {
      logger.warn("unable to create available and active ldap object");
    }
    return t;
  }


  /**
   * Remove an ldap object from the available pool.
   *
   * @param  t  ldap object that exists in the available pool
   */
  protected void removeAvailable(final T t)
  {
    boolean destroy = false;
    final PooledLdapConnection<T> pl = new PooledLdapConnection<T>(t);
    poolLock.lock();
    try {
      if (available.remove(pl)) {
        destroy = true;
      } else {
        logger.warn(
          "attempt to remove unknown available ldap object: {}", t);
      }
    } finally {
      poolLock.unlock();
    }
    if (destroy) {
      logger.trace("removing available ldap object: {}", t);
      ldapFactory.destroy(t);
    }
  }


  /**
   * Remove an ldap object from the active pool.
   *
   * @param  t  ldap object that exists in the active pool
   */
  protected void removeActive(final T t)
  {
    boolean destroy = false;
    final PooledLdapConnection<T> pl = new PooledLdapConnection<T>(t);
    poolLock.lock();
    try {
      if (active.remove(pl)) {
        destroy = true;
      } else {
        logger.warn("attempt to remove unknown active ldap object: {}", t);
      }
    } finally {
      poolLock.unlock();
    }
    if (destroy) {
      logger.trace("removing active ldap object: {}", t);
      ldapFactory.destroy(t);
    }
  }


  /**
   * Remove an ldap object from both the available and active pools.
   *
   * @param  t  ldap object that exists in the both the available and active
   * pools
   */
  protected void removeAvailableAndActive(final T t)
  {
    boolean destroy = false;
    final PooledLdapConnection<T> pl = new PooledLdapConnection<T>(t);
    poolLock.lock();
    try {
      if (available.remove(pl)) {
        destroy = true;
      } else {
        logger.debug(
          "attempt to remove unknown available ldap object: {}", t);
      }
      if (active.remove(pl)) {
        destroy = true;
      } else {
        logger.debug(
          "attempt to remove unknown active ldap object: {}", t);
      }
    } finally {
      poolLock.unlock();
    }
    if (destroy) {
      logger.trace("removing active ldap object: {}", t);
      ldapFactory.destroy(t);
    }
  }


  /**
   * Attempts to activate and validate an ldap object. Performed before an
   * object is returned from {@link LdapPool#checkOut()}.
   *
   * @param  t  ldap object
   *
   * @throws  LdapPoolException  if this method fais
   * @throws  LdapActivationException  if the ldap object cannot be activated
   * @throws  LdapValidateException  if the ldap object cannot be validated
   */
  protected void activateAndValidate(final T t)
    throws LdapPoolException
  {
    if (!ldapFactory.activate(t)) {
      logger.warn("ldap object failed activation: {}", t);
      removeAvailableAndActive(t);
      throw new LdapActivationException("Activation of ldap object failed");
    }
    if (
      poolConfig.isValidateOnCheckOut() &&
        !ldapFactory.validate(t)) {
      logger.warn("ldap object failed check out validation: {}", t);
      removeAvailableAndActive(t);
      throw new LdapValidationException("Validation of ldap object failed");
    }
  }


  /**
   * Attempts to validate and passivate an ldap object. Performed when an object
   * is given to {@link LdapPool#checkIn}.
   *
   * @param  t  ldap object
   *
   * @return  whether both validate and passivation succeeded
   */
  protected boolean validateAndPassivate(final T t)
  {
    boolean valid = false;
    if (poolConfig.isValidateOnCheckIn()) {
      if (!ldapFactory.validate(t)) {
        logger.warn("ldap object failed check in validation: {}", t);
      } else {
        valid = true;
      }
    } else {
      valid = true;
    }
    if (valid && !ldapFactory.passivate(t)) {
      valid = false;
      logger.warn("ldap object failed activation: {}", t);
    }
    return valid;
  }


  /** {@inheritDoc} */
  @Override
  public void prune()
  {
    logger.trace(
      "waiting for pool lock to prune {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      if (active.size() == 0) {
        logger.debug("pruning pool of size {}", available.size());
        while (available.size() > poolConfig.getMinPoolSize()) {
          PooledLdapConnection<T> pl = available.peek();
          final long time = System.currentTimeMillis() - pl.getCreatedTime();
          if (time > poolConfig.getExpirationTime()) {
            pl = available.remove();
            logger.trace(
              "removing {} in the pool for {}ms", pl.getLdapConnection(), time);
            ldapFactory.destroy(pl.getLdapConnection());
          } else {
            break;
          }
        }
        logger.debug("pool size pruned to {}", available.size());
      } else {
        logger.debug("pool is currently active, no objects pruned");
      }
    } finally {
      poolLock.unlock();
    }
  }


  /** {@inheritDoc} */
  @Override
  public void validate()
  {
    poolLock.lock();
    try {
      if (active.size() == 0) {
        if (poolConfig.isValidatePeriodically()) {
          logger.debug(
            "validate for pool of size {}", available.size());

          final Queue<PooledLdapConnection<T>> remove =
            new LinkedList<PooledLdapConnection<T>>();
          for (PooledLdapConnection<T> pl : available) {
            logger.trace("validating {}", pl.getLdapConnection());
            if (ldapFactory.validate(pl.getLdapConnection())) {
              logger.trace(
                "ldap object passed validation: {}", pl.getLdapConnection());
            } else {
              logger.warn(
                "ldap object failed validation: {}", pl.getLdapConnection());
              remove.add(pl);
            }
          }
          for (PooledLdapConnection<T> pl : remove) {
            logger.trace(
              "removing {} from the pool", pl.getLdapConnection());
            available.remove(pl);
            ldapFactory.destroy(pl.getLdapConnection());
          }
        }
        initializePool();
        logger.debug(
          "pool size after validation is {}", available.size());
      } else {
        logger.debug("pool is currently active, no validation performed");
      }
    } finally {
      poolLock.unlock();
    }
  }


  /** {@inheritDoc} */
  @Override
  public int availableCount()
  {
    return available.size();
  }


  /** {@inheritDoc} */
  @Override
  public int activeCount()
  {
    return active.size();
  }


  /**
   * Called by the garbage collector on an object when garbage collection
   * determines that there are no more references to the object.
   *
   * @throws  Throwable  if an exception is thrown by this method
   */
  protected void finalize()
    throws Throwable
  {
    try {
      close();
    } finally {
      super.finalize();
    }
  }


  /**
   * <code>PooledLdap</code> contains an ldap object that is participating in a
   * pool. Used to track how long an ldap object has been in either the
   * available or active queues.
   *
   * @param  <T>  type of ldap object
   */
  static protected class PooledLdapConnection<T extends LdapConnection>
  {

    /** hash code seed. */
    protected static final int HASH_CODE_SEED = 89;

    /** Underlying search operation object. */
    private T ldapConn;

    /** Time this object was created. */
    private long createdTime;


    /**
     * Creates a new <code>PooledLdap</code> with the supplied ldap object.
     *
     * @param  t  ldap object
     */
    public PooledLdapConnection(final T t)
    {
      ldapConn = t;
      createdTime = System.currentTimeMillis();
    }


    /**
     * Returns the ldap connection.
     *
     * @return  underlying ldap connection
     */
    public T getLdapConnection()
    {
      return ldapConn;
    }


    /**
     * Returns the time this object was created.
     *
     * @return  creation time
     */
    public long getCreatedTime()
    {
      return createdTime;
    }


    /**
     * Returns whether the supplied <code>Object</code> contains the same data
     * as this bean.
     *
     * @param  o  <code>Object</code>
     *
     * @return  <code>boolean</code>
     */
    public boolean equals(final Object o)
    {
      if (o == null) {
        return false;
      }
      return
        o == this ||
          (getClass() == o.getClass() &&
            o.hashCode() == hashCode());
    }


    /**
     * This returns the hash code for this object.
     *
     * @return  <code>int</code>
     */
    public int hashCode()
    {
      int hc = HASH_CODE_SEED;
      if (ldapConn != null) {
        hc += ldapConn.hashCode();
      }
      return hc;
    }
  }
}
