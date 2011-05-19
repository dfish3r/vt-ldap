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
 * based on the configuration of the prune period. See {@link
 * LdapPoolConfig#setPrunePeriod} and {@link LdapPoolConfig#setExpirationTime}.
 * This implementation should be used when you have some flexibility in the
 * number of ldap connections that can be created to handle spikes in load. See
 * {@link AbstractLdapPool}. Note that this pool will begin blocking if it
 * cannot create new ldap connections.
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
  @Override
  public LdapConnection checkOut()
    throws LdapPoolException
  {
    LdapConnection lc = null;
    logger.trace(
      "waiting on pool lock for check out {}", poolLock.getQueueLength());
    poolLock.lock();
    try {
      // if an available object exists, use it
      // if no available objects, attempt to create
      if (available.size() > 0) {
        try {
          logger.trace("retrieve available ldap object");
          lc = retrieveAvailable();
        } catch (NoSuchElementException e) {
          logger.error("could not remove ldap object from list", e);
          throw new IllegalStateException("Pool is empty", e);
        }
      }
    } finally {
      poolLock.unlock();
    }

    if (lc == null) {
      // no object was available, create a new one
      lc = createActive();
      logger.trace("created new active ldap connection: {}", lc);
      if (lc == null) {
        // create failed, block until an object is available
        logger.debug("created failed, block until an object is available");
        lc = blockAvailable();
      } else {
        logger.trace("created new active ldap connection: {}", lc);
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
}
