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

import java.util.TimerTask;
import edu.vt.middleware.ldap.LdapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>PrunePoolTask</code> is a periodic task that removes available ldap
 * objects from the pool if the objects have been in the pool longer than a
 * configured expiration time and the pool size is above it's configured
 * minimum. Task will skip execution if the pool has any active objects.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PrunePoolTask<T extends LdapConnection> extends TimerTask
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Pool to clean. */
  private LdapPool<T> pool;


  /**
   * Creates a new task to periodically prune the supplied pool.
   *
   * @param  lp  ldap pool to periodically inspect
   */
  public PrunePoolTask(final LdapPool<T> lp)
  {
    pool = lp;
  }


  /**
   * This attempts to remove idle objects from a pool. See {@link
   * LdapPool#prune()}.
   */
  public void run()
  {
    logger.debug("Begin prune task for {}", pool);
    pool.prune();
    logger.debug("End prune task for {}", pool);
  }
}
