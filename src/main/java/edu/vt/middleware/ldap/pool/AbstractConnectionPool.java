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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapException;

/**
 * Contains the base implementation for pooling connections. The main design
 * objective for the supplied pooling implementations is to provide a
 * pool that does not block on connection creation or destruction. This is what
 * accounts for the multiple locks available on this class. The pool is backed
 * by two queues, one for available connections and one for active connections.
 * Connections that are available via {@link #getConnection()} exist in the
 * available queue. Connections that are actively in use exist in the active
 * queue.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractConnectionPool extends AbstractPool<Connection>
{

  /** Lock for the entire pool. */
  protected final ReentrantLock poolLock = new ReentrantLock();

  /** Condition for notifying threads that a connection was returned. */
  protected final Condition poolNotEmpty = poolLock.newCondition();

  /** Lock for check ins. */
  protected final ReentrantLock checkInLock = new ReentrantLock();

  /** Lock for check outs. */
  protected final ReentrantLock checkOutLock = new ReentrantLock();

  /** List of available connections in the pool. */
  protected Queue<PooledConnection> available =
    new LinkedList<PooledConnection>();

  /** List of connections in use. */
  protected Queue<PooledConnection> active = new LinkedList<PooledConnection>();

  /** Connection configuration to create connections with. */
  protected ConnectionConfig connectionConfig;

  /** Whether to connect to the ldap on connection creation. */
  protected boolean connectOnCreate = true;

  /** Executor for scheduling pool tasks. */
  protected ScheduledExecutorService poolExecutor =
    Executors.newSingleThreadScheduledExecutor(
      new ThreadFactory() {
        public Thread newThread(final Runnable r)
        {
          final Thread t = new Thread(r);
          t.setDaemon(true);
          return t;
        }
      });


  /**
   * Creates a new pool with the supplied pool configuration and connection
   * configuration. The configurations will be marked as immutable by this pool.
   *
   * @param  pc  pool config
   * @param  cc  connection config
   */
  public AbstractConnectionPool(final PoolConfig pc, final ConnectionConfig cc)
  {
    super(pc);
    connectionConfig = cc;
    connectionConfig.makeImmutable();
  }


  /**
   * Returns the connection configuration for this pool.
   *
   * @return  connection config
   */
  public ConnectionConfig getConnectionConfig()
  {
    return connectionConfig;
  }


  /**
   * Returns whether connections will attempt to connect after creation.
   * Default is true.
   *
   * @return   whether connections will attempt to connect after creation
   */
  public boolean getConnectOnCreate()
  {
    return connectOnCreate;
  }


  /**
   * Sets whether newly created  connections will attempt to connect. Default is
   * true.
   *
   * @param  b  connect on create
   */
  public void setConnectOnCreate(final boolean b)
  {
    connectOnCreate = b;
  }


  /** Initialize this pool for use. */
  public void initialize()
  {
    logger.debug("beginning pool initialization");

    final Runnable prune = new Runnable() {
      public void run()
      {
        logger.debug("Begin prune task for {}", this);
        prune();
        logger.debug("End prune task for {}", this);
      }
    };
    poolExecutor.scheduleAtFixedRate(
      prune,
      poolConfig.getPrunePeriod(),
      poolConfig.getPrunePeriod(),
      TimeUnit.SECONDS);
    logger.debug("prune pool task scheduled");

    final Runnable validate = new Runnable() {
      public void run()
      {
        logger.debug("Begin validate task for {}", this);
        validate();
        logger.debug("End validate task for {}", this);
      }
    };
    poolExecutor.scheduleAtFixedRate(
      validate,
      poolConfig.getValidatePeriod(),
      poolConfig.getValidatePeriod(),
      TimeUnit.SECONDS);
    logger.debug("validate pool task scheduled");

    initializePool();

    logger.debug("pool initialized to size {}",  available.size());
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
        final PooledConnection pc = createAvailable();
        if (poolConfig.isValidateOnCheckIn()) {
          if (validate(pc)) {
            logger.trace("connection passed initialize validation: {}", pc);
          } else {
            logger.warn("connection failed initialize validation: {}", pc);
            removeAvailable(pc);
          }
        }
        count++;
      }
    } finally {
      poolLock.unlock();
    }
  }


  /** Empty this pool, freeing any resources. */
  public void close()
  {
    poolLock.lock();
    try {
      while (available.size() > 0) {
        final PooledConnection pc = available.remove();
        pc.destroy();
        logger.trace("destroyed connection: {}", pc);
      }
      while (active.size() > 0) {
        final PooledConnection pc = active.remove();
        pc.destroy();
        logger.trace("destroyed connection: {}", pc);
      }
      logger.debug("pool closed");
    } finally {
      poolLock.unlock();
    }

    logger.debug("shutting down executor");
    poolExecutor.shutdown();
    logger.debug("executor shutdown");
  }


  /**
   * Returns a connection from the pool.
   *
   * @return  connection
   *
   * @throws  PoolException  if this operation fails
   * @throws  BlockingTimeoutException  if this pool is configured with a block
   * time and it occurs
   * @throws  PoolInterruptedException  if this pool is configured with a block
   * time and the current thread is interrupted
   */
  public abstract Connection getConnection() throws PoolException;


  /**
   * Returns a connection to the pool.
   *
   * @param c  pooled connection
   */
  protected abstract void putConnection(final PooledConnection c);


  /**
   * Create a new connection. If {@link #connectOnCreate} is true, the
   * connection will be opened.
   *
   * @return  pooled connection
   */
  protected PooledConnection createConnection()
  {
    PooledConnection conn = new PooledConnection(connectionConfig);
    if (connectOnCreate) {
      try {
        conn.open();
      } catch (LdapException e) {
        logger.error("unabled to connect to the ldap", e);
        conn = null;
      }
    }
    return conn;
  }


  /**
   * Create a new connection and place it in the available pool.
   *
   * @return  connection that was placed in the available pool
   */
  protected PooledConnection createAvailable()
  {
    final PooledConnection pc = createConnection();
    if (pc != null) {
      poolLock.lock();
      try {
        available.add(pc);
      } finally {
        poolLock.unlock();
      }
    } else {
      logger.warn("unable to create available connection");
    }
    return pc;
  }


  /**
   * Create a new connection and place it in the active pool.
   *
   * @return  connection that was placed in the active pool
   */
  protected PooledConnection createActive()
  {
    final PooledConnection pc = createConnection();
    if (pc != null) {
      poolLock.lock();
      try {
        active.add(pc);
      } finally {
        poolLock.unlock();
      }
    } else {
      logger.warn("unable to create active connection");
    }
    return pc;
  }


  /**
   * Remove a connection from the available pool.
   *
   * @param  pc  connection that is in the available pool
   */
  protected void removeAvailable(final PooledConnection pc)
  {
    boolean destroy = false;
    poolLock.lock();
    try {
      if (available.remove(pc)) {
        destroy = true;
      } else {
        logger.warn("attempt to remove unknown available connection: {}", pc);
      }
    } finally {
      poolLock.unlock();
    }
    if (destroy) {
      logger.trace("removing available connection: {}", pc);
      pc.destroy();
      logger.trace("destroyed connection: {}", pc);
    }
  }


  /**
   * Remove a connection from the active pool.
   *
   * @param  pc  connection that is in the active pool
   */
  protected void removeActive(final PooledConnection pc)
  {
    boolean destroy = false;
    poolLock.lock();
    try {
      if (active.remove(pc)) {
        destroy = true;
      } else {
        logger.warn("attempt to remove unknown active connection: {}", pc);
      }
    } finally {
      poolLock.unlock();
    }
    if (destroy) {
      logger.trace("removing active connection: {}", pc);
      pc.destroy();
      logger.trace("destroyed connection: {}", pc);
    }
  }


  /**
   * Remove a connection from both the available and active pools.
   *
   * @param  pc  connection that is in both the available and active pools
   */
  protected void removeAvailableAndActive(final PooledConnection pc)
  {
    boolean destroy = false;
    poolLock.lock();
    try {
      if (available.remove(pc)) {
        destroy = true;
      } else {
        logger.debug("attempt to remove unknown available connection: {}", pc);
      }
      if (active.remove(pc)) {
        destroy = true;
      } else {
        logger.debug("attempt to remove unknown active connection: {}", pc);
      }
    } finally {
      poolLock.unlock();
    }
    if (destroy) {
      logger.trace("removing active connection: {}", pc);
      pc.destroy();
      logger.trace("destroyed connection: {}", pc);
    }
  }


  /**
   * Attempts to activate and validate a connection. Performed before a
   * connection is returned from {@link #getConnection()}.
   *
   * @param  pc  connection
   *
   * @throws  PoolException  if this method fails
   * @throws  ActivationException  if the connection cannot be activated
   * @throws  ValidationException  if the connection cannot be validated
   */
  protected void activateAndValidate(final PooledConnection pc)
    throws PoolException
  {
    if (!activate(pc)) {
      logger.warn("connection failed activation: {}", pc);
      removeAvailableAndActive(pc);
      throw new ActivationException("Activation of connection failed");
    }
    if (poolConfig.isValidateOnCheckOut() && !validate(pc)) {
      logger.warn("connection failed check out validation: {}", pc);
      removeAvailableAndActive(pc);
      throw new ValidationException("Validation of connection failed");
    }
  }


  /**
   * Attempts to validate and passivate a connection. Performed when a
   * connection is given to {@link #putConnection(PooledConnection)}.
   *
   * @param  pc  connection
   *
   * @return  whether both validate and passivation succeeded
   */
  protected boolean validateAndPassivate(final PooledConnection pc)
  {
    boolean valid = false;
    if (poolConfig.isValidateOnCheckIn()) {
      if (!validate(pc)) {
        logger.warn("connection failed check in validation: {}", pc);
      } else {
        valid = true;
      }
    } else {
      valid = true;
    }
    if (valid && !passivate(pc)) {
      valid = false;
      logger.warn("connection failed passivation: {}", pc);
    }
    return valid;
  }


  /**
   * Attempts to reduce the size of the pool back to it's configured minimum.
   * {@link PoolConfig#setMinPoolSize(int)}.
   */
  public void prune()
  {
    logger.trace(
      "waiting for pool lock to prune {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      if (active.size() == 0) {
        logger.debug("pruning pool of size {}", available.size());
        while (available.size() > poolConfig.getMinPoolSize()) {
          PooledConnection pc = available.peek();
          final long time = System.currentTimeMillis() - pc.getCreatedTime();
          if (time >
              TimeUnit.SECONDS.toMillis(poolConfig.getExpirationTime())) {
            pc = available.remove();
            logger.trace("removing {} in the pool for {}ms", pc, time);
            pc.destroy();
            logger.trace("destroyed connection: {}", pc);
          } else {
            break;
          }
        }
        logger.debug("pool size pruned to {}", available.size());
      } else {
        logger.debug("pool is currently active, no connections pruned");
      }
    } finally {
      poolLock.unlock();
    }
  }


  /**
   * Attempts to validate all objects in the pool. {@link
   * PoolConfig#setValidatePeriodically(boolean)}.
   */
  public void validate()
  {
    poolLock.lock();
    try {
      if (active.size() == 0) {
        if (poolConfig.isValidatePeriodically()) {
          logger.debug(
            "validate for pool of size {}", available.size());

          final Queue<PooledConnection> remove =
            new LinkedList<PooledConnection>();
          for (PooledConnection pc : available) {
            logger.trace("validating {}", pc);
            if (validate(pc)) {
              logger.trace("connection passed validation: {}", pc);
            } else {
              logger.warn("connection failed validation: {}", pc);
              remove.add(pc);
            }
          }
          for (PooledConnection pc : remove) {
            logger.trace("removing {} from the pool", pc);
            available.remove(pc);
            pc.destroy();
            logger.trace("destroyed connection: {}", pc);
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


  /**
   * Returns the number of connections available for use.
   *
   * @return  count
   */
  public int availableCount()
  {
    return available.size();
  }


  /**
   * Returns the number of connections in use.
   *
   * @return  count
   */
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
   * A connection that is participating in this pool. Used to track how long a
   * connection has been in use and override close invocations.
   *
   * @author  Middleware Services
   * @version  $Revision$ $Date$
   */
  protected class PooledConnection extends Connection
  {

    /** hash code seed. */
    protected static final int HASH_CODE_SEED = 89;

    /** Time this connection was created. */
    private long createdTime = System.currentTimeMillis();


    /**
     * Creates a new pooled connection.
     *
     * @param  cc  connection configuration
     */
    public PooledConnection(final ConnectionConfig cc)
    {
      super(cc);
    }


    /**
     * Returns the time this connection was created.
     *
     * @return  creation time
     */
    public long getCreatedTime()
    {
      return createdTime;
    }


    /** This will return this connection to it's pool. */
    public synchronized void close()
    {
      putConnection(this);
    }


    /** This will close the connection to the LDAP. */
    protected synchronized void destroy()
    {
      super.close();
    }


    /**
     * Returns whether the supplied object is the same as this one.
     *
     * @param  o  to compare against
     *
     * @return  whether the supplied object is the same as this one
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
     * Returns the hash code for this object.
     *
     * @return  hash code
     */
    public int hashCode()
    {
      int hc = HASH_CODE_SEED;
      hc += super.hashCode();
      return hc;
    }
  }
}
