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

import edu.vt.middleware.ldap.LdapConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>CloseLdapPassivator</code> passivates an ldap object by attempting to
 * close it's connection to the ldap.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CloseLdapPassivator implements LdapPassivator<LdapConnection>
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());


  /** {@inheritDoc} */
  public boolean passivate(final LdapConnection lc)
  {
    boolean success = false;
    if (lc != null) {
      lc.close();
      success = true;
    }
    return success;
  }
}
