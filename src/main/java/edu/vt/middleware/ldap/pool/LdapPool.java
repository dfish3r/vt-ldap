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
 * <code>LdapPool</code> provides an interface for pooling ldap objects.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapPool<T extends Connection>
{


  /**
   * Returns the configuration for this pool.
   *
   * @return  ldap pool config
   */
  LdapPoolConfig getLdapPoolConfig();


  /** Initialize this pool for use. */
  void initialize();


  /** Empty this pool, closing all connections, and freeing any resources. */
  void close();


  /**
   * Returns an ldap object from the pool.
   *
   * @return  ldap object
   *
   * @throws  LdapPoolException  if this operation fails
   * @throws  BlockingTimeoutException  if this pool is configured with a block
   * time and it occurs
   * @throws  PoolInterruptedException  if this pool is configured with a block
   * time and the current thread is interrupted
   */
  T checkOut()
    throws LdapPoolException;


  /**
   * Returns an ldap object to the pool.
   *
   * @param  t  ldap object
   */
  void checkIn(final T t);


  /**
   * Attempts to reduce the size of the pool back to it's configured minimum.
   * {@link LdapPoolConfig#setMinPoolSize(int)}.
   */
  void prune();


  /**
   * Attempts to validate all objects in the pool. {@link
   * LdapPoolConfig#setValidatePeriodically(boolean)}.
   */
  void validate();


  /**
   * Returns the number of ldap objects available for use.
   *
   * @return  count
   */
  int availableCount();


  /**
   * Returns the number of ldap objects in use.
   *
   * @return  count
   */
  int activeCount();
}
