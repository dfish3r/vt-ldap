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
import edu.vt.middleware.ldap.LdapException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>ConnectLdapActivator</code> activates an ldap object by attempting to
 * connect to the ldap.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectLdapActivator implements LdapActivator<LdapConnection>
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());


  /** {@inheritDoc} */
  public boolean activate(final LdapConnection lc)
  {
    boolean success = false;
    if (lc != null) {
      try {
        lc.open();
        success = true;
      } catch (LdapException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("unabled to connect to the ldap", e);
        }
      }
    }
    return success;
  }
}
