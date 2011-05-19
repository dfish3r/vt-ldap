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

import edu.vt.middleware.ldap.AbstractConfig;

/**
 * <code>LdapPoolConfig</code> contains all the configuration data that the
 * pooling implementations need to control the pool.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapPoolConfig extends AbstractConfig
{
  /** Default min pool size, value is {@value}. */
  public static final int DEFAULT_MIN_POOL_SIZE = 3;

  /** Default max pool size, value is {@value}. */
  public static final int DEFAULT_MAX_POOL_SIZE = 10;

  /** Default validate on check in, value is {@value}. */
  public static final boolean DEFAULT_VALIDATE_ON_CHECKIN = false;

  /** Default validate on check out, value is {@value}. */
  public static final boolean DEFAULT_VALIDATE_ON_CHECKOUT = false;

  /** Default validate periodically, value is {@value}. */
  public static final boolean DEFAULT_VALIDATE_PERIODICALLY = false;

  /** Default validate period, value is {@value}. */
  public static final long DEFAULT_VALIDATE_PERIOD = 1800;

  /** Default prune period, value is {@value}. */
  public static final long DEFAULT_PRUNE_PERIOD = 300;

  /** Default expiration time, value is {@value}. */
  public static final long DEFAULT_EXPIRATION_TIME = 600;

  /** Min pool size. */
  private int minPoolSize = DEFAULT_MIN_POOL_SIZE;

  /** Max pool size. */
  private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

  /** Whether the ldap object should be validated when returned to the pool. */
  private boolean validateOnCheckIn = DEFAULT_VALIDATE_ON_CHECKIN;

  /** Whether the ldap object should be validated when given from the pool. */
  private boolean validateOnCheckOut = DEFAULT_VALIDATE_ON_CHECKOUT;

  /** Whether the pool should be validated periodically. */
  private boolean validatePeriodically = DEFAULT_VALIDATE_PERIODICALLY;

  /** Time in seconds that the validate pool should repeat. */
  private long validatePeriod = DEFAULT_VALIDATE_PERIOD;

  /** Time in seconds that the prune pool should repeat. */
  private long prunePeriod = DEFAULT_PRUNE_PERIOD;

  /** Time in seconds that ldap objects should be considered expired. */
  private long expirationTime = DEFAULT_EXPIRATION_TIME;


  /** Default constructor. */
  public LdapPoolConfig() {}


  /**
   * This returns the min pool size for the <code>LdapPoolConfig</code>. Default
   * value is {@link #DEFAULT_MIN_POOL_SIZE}. This value represents the size of
   * the pool after a prune has occurred.
   *
   * @return  <code>int</code> - min pool size
   */
  public int getMinPoolSize()
  {
    return minPoolSize;
  }


  /**
   * This returns the max pool size for the <code>LdapPoolConfig</code>. Default
   * value is {@link #DEFAULT_MAX_POOL_SIZE}. This value may or may not be
   * strictly enforced depending on the pooling implementation.
   *
   * @return  <code>int</code> - max pool size
   */
  public int getMaxPoolSize()
  {
    return maxPoolSize;
  }


  /**
   * This returns the validate on check in flag for the <code>
   * LdapPoolConfig</code>. Default value is {@link
   * #DEFAULT_VALIDATE_ON_CHECKIN}.
   *
   * @return  <code>boolean</code> - validate on check in
   */
  public boolean isValidateOnCheckIn()
  {
    return validateOnCheckIn;
  }


  /**
   * This returns the validate on check out flag for the <code>
   * LdapPoolConfig</code>. Default value is {@link
   * #DEFAULT_VALIDATE_ON_CHECKOUT}.
   *
   * @return  <code>boolean</code> - validate on check in
   */
  public boolean isValidateOnCheckOut()
  {
    return validateOnCheckOut;
  }


  /**
   * This returns the validate periodically flag for the <code>
   * LdapPoolConfig</code>. Default value is {@link
   * #DEFAULT_VALIDATE_PERIODICALLY}.
   *
   * @return  <code>boolean</code> - validate periodically
   */
  public boolean isValidatePeriodically()
  {
    return validatePeriodically;
  }


  /**
   * This returns the prune period for the <code>LdapPoolConfig</code>.
   * Default value is {@link #DEFAULT_PRUNE_PERIOD}..
   *
   * @return  <code>long</code> - prune period in seconds
   */
  public long getPrunePeriod()
  {
    return prunePeriod;
  }


  /**
   * This returns the validate period for the <code>LdapPoolConfig</code>.
   * Default value is {@link #DEFAULT_VALIDATE_PERIOD}.
   *
   * @return  <code>long</code> - validate period in seconds
   */
  public long getValidatePeriod()
  {
    return validatePeriod;
  }


  /**
   * This returns the expiration time for the <code>LdapPoolConfig</code>.
   * Default value is {@link #DEFAULT_EXPIRATION_TIME}. The expiration time
   * represents the max time an ldap object should be available before it is
   * considered stale. This value does not apply to objects in the pool if the
   * pool has only a minimum number of objects available.
   *
   * @return  <code>long</code> - expiration time in seconds
   */
  public long getExpirationTime()
  {
    return expirationTime;
  }


  /**
   * This sets the min pool size for the <code>LdapPoolConfig</code>.
   *
   * @param  size  <code>int</code>
   */
  public void setMinPoolSize(final int size)
  {
    checkImmutable();
    if (size >= 0) {
      logger.trace("setting minPoolSize: {}", size);
      minPoolSize = size;
    }
  }


  /**
   * This sets the max pool size for the <code>LdapPoolConfig</code>.
   *
   * @param  size  <code>int</code>
   */
  public void setMaxPoolSize(final int size)
  {
    checkImmutable();
    if (size >= 0) {
      logger.trace("setting maxPoolSize: {}", size);
      maxPoolSize = size;
    }
  }


  /**
   * This sets the validate on check in flag for the <code>
   * LdapPoolConfig</code>.
   *
   * @param  b  <code>boolean</code>
   */
  public void setValidateOnCheckIn(final boolean b)
  {
    checkImmutable();
    logger.trace("setting validateOnCheckIn: {}", b);
    validateOnCheckIn = b;
  }


  /**
   * This sets the validate on check out flag for the <code>
   * LdapPoolConfig</code>.
   *
   * @param  b  <code>boolean</code>
   */
  public void setValidateOnCheckOut(final boolean b)
  {
    checkImmutable();
    logger.trace("setting validateOnCheckOut: {}", b);
    validateOnCheckOut = b;
  }


  /**
   * This sets the validate periodically flag for the <code>
   * LdapPoolConfig</code>.
   *
   * @param  b  <code>boolean</code>
   */
  public void setValidatePeriodically(final boolean b)
  {
    checkImmutable();
    logger.trace("setting validatePeriodically: {}", b);
    validatePeriodically = b;
  }


  /**
   * Sets the period for which the pool will be pruned.
   *
   * @param  time  in seconds
   */
  public void setPrunePeriod(final long time)
  {
    checkImmutable();
    if (time >= 0) {
      logger.trace("setting prunePeriod: {}", time);
      prunePeriod = time;
    }
  }


  /**
   * Sets the period for which the pool will be validated.
   *
   * @param  time  in seconds
   */
  public void setValidatePeriod(final long time)
  {
    checkImmutable();
    if (time >= 0) {
      logger.trace("setting validatePeriod: {}", time);
      validatePeriod = time;
    }
  }


  /**
   * Sets the time that an ldap object should be considered stale and ready for
   * removal from the pool.
   *
   * @param  time  in seconds
   */
  public void setExpirationTime(final long time)
  {
    checkImmutable();
    if (time >= 0) {
      logger.trace("setting expirationTime: {}", time);
      expirationTime = time;
    }
  }
}
