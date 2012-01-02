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
 * Passivates a connection by attempting to close it.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ClosePassivator implements Passivator<Connection>
{


  /** {@inheritDoc} */
  @Override
  public boolean passivate(final Connection c)
  {
    boolean success = false;
    if (c != null) {
      c.close();
      success = true;
    }
    return success;
  }
}
