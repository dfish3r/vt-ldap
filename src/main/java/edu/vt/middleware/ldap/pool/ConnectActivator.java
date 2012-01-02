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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activates a connection by attempting to open it.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectActivator implements Activator<Connection>
{

  /** Logger for this class. */
  private final Logger logger = LoggerFactory.getLogger(getClass());


  /** {@inheritDoc} */
  @Override
  public boolean activate(final Connection c)
  {
    boolean success = false;
    if (c != null) {
      try {
        c.open();
        success = true;
      } catch (Exception e) {
        logger.error("unabled to connect to the ldap", e);
      }
    }
    return success;
  }
}
