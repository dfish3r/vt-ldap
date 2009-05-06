/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.pool;

import java.util.TimerTask;
import edu.vt.middleware.ldap.BaseLdap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>ValidatePoolTask</code> is a periodic task that checks that every ldap
 * object in the pool is valid. Objects that don't pass validation are removed.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ValidatePoolTask<T extends BaseLdap> extends TimerTask
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Pool to clean. */
  private LdapPool<T> pool;


  /**
   * Creates a new task to periodically validate the supplied pool.
   *
   * @param  lp  ldap pool to periodically validate
   */
  public ValidatePoolTask(final LdapPool<T> lp)
  {
    this.pool = lp;
  }


  /**
   * This attempts to validate idle objects in a pool. See {@link
   * LdapPool#validate()}.
   */
  public void run()
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Begin validate task for " + this.pool);
    }
    this.pool.validate();
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("End validate task for " + this.pool);
    }
  }
}
