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
 * <code>SoftLimitLdapPool</code> implements a pool of ldap objects that has a
 * set minimum and maximum size. The pool will grow beyond it's maximum size as
 * necessary based on it's current load. Pool size will return to it's minimum
 * based on the configuration of the prune timer. See {@link
 * LdapPoolConfig#setPruneTimerPeriod} and {@link
 * LdapPoolConfig#setExpirationTime}. This implementation should be used when
 * you have some flexibility in the number of ldap connections that can be
 * created to handle spikes in load. See {@link AbstractLdapPool}. Note that
 * this pool will begin blocking if it cannot create new ldap connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SoftLimitLdapPool extends BlockingLdapPool
{


  /**
   * Creates a new ldap pool with the supplied ldap factory.
   *
   * @param  lf  ldap factory
   */
  public SoftLimitLdapPool(final LdapFactory<LdapConnection> lf)
  {
    super(new LdapPoolConfig(), lf);
  }


  /**
   * Creates a new ldap pool with the supplied ldap config and factory.
   *
   * @param  lpc  ldap pool configuration
   * @param  lf  ldap factory
   */
  public SoftLimitLdapPool(
    final LdapPoolConfig lpc, final LdapFactory<LdapConnection> lf)
  {
    super(lpc, lf);
  }


  /** {@inheritDoc} */
  public LdapConnection checkOut()
    throws LdapPoolException
  {
    LdapConnection lc = null;
    this.logger.trace(
      "waiting on pool lock for check out {}", this.poolLock.getQueueLength());
    this.poolLock.lock();
    try {
      // if an available object exists, use it
      // if no available objects, attempt to create
      if (this.available.size() > 0) {
        try {
          this.logger.trace("retrieve available ldap object");
          lc = this.retrieveAvailable();
        } catch (NoSuchElementException e) {
          this.logger.error("could not remove ldap object from list", e);
          throw new IllegalStateException("Pool is empty", e);
        }
      }
    } finally {
      this.poolLock.unlock();
    }

    if (lc == null) {
      // no object was available, create a new one
      lc = this.createActive();
      this.logger.trace("created new active ldap connection: {}", lc);
      if (lc == null) {
        // create failed, block until an object is available
        this.logger.debug("created failed, block until an object is available");
        lc = this.blockAvailable();
      } else {
        this.logger.trace("created new active ldap connection: {}", lc);
      }
    }

    if (lc != null) {
      this.activateAndValidate(lc);
    } else {
      this.logger.error("Could not service check out request");
      throw new LdapPoolExhaustedException(
        "Pool is empty and object creation failed");
    }

    return lc;
  }
}
