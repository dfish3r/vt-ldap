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
 * <code>CloseLdapPassivator</code> passivates an ldap object by attempting to
 * close it's connection to the ldap.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CloseLdapPassivator implements LdapPassivator<Connection>
{


  /** {@inheritDoc} */
  @Override
  public boolean passivate(final Connection lc)
  {
    boolean success = false;
    if (lc != null) {
      lc.close();
      success = true;
    }
    return success;
  }
}
