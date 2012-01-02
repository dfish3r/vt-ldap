/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
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
 * Provides an interface for connection pooling.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ConnectionPool
{


  /**
   * Returns the activator for this pool.
   *
   * @return  activator
   */
  Activator<Connection> getActivator();


  /**
   * Sets the activator for this pool.
   *
   * @param  a  activator
   */
  void setActivator(final Activator<Connection> a);


  /**
   * Returns the passivator for this pool.
   *
   * @return  passivator
   */
  Passivator<Connection> getPassivator();


  /**
   * Sets the passivator for this pool.
   *
   * @param  p  passivator
   */
  void setPassivator(final Passivator<Connection> p);


  /**
   * Returns the validator for this pool.
   *
   * @return  validator
   */
  Validator<Connection> getValidator();


  /**
   * Sets the validator for this pool.
   *
   * @param  v  validator
   */
  void setValidator(final Validator<Connection> v);


  /** Initialize this pool for use. */
  void initialize();


  /**
   * Returns an object from the pool.
   *
   * @return  pooled object
   *
   * @throws  PoolException  if this operation fails
   * @throws  BlockingTimeoutException  if this pool is configured with a block
   * time and it occurs
   * @throws  PoolInterruptedException  if this pool is configured with a block
   * time and the current thread is interrupted
   */
  Connection getConnection()
    throws PoolException;


  /** Empty this pool, freeing any resources. */
  void close();
}
