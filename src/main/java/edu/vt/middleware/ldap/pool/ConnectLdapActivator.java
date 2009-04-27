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

import javax.naming.NamingException;
import edu.vt.middleware.ldap.Ldap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>ConnectLdapActivator</code> activates a ldap object by attempting to
 * connect to the ldap.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectLdapActivator implements LdapActivator<Ldap>
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());


  /** {@inheritDoc}. */
  public boolean activate(final Ldap l)
  {
    boolean success = false;
    if (l != null) {
      try {
        l.connect();
        success = true;
      } catch (NamingException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("unabled to connect to the ldap", e);
        }
      }
    }
    return success;
  }
}
