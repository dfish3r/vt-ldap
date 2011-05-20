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
import edu.vt.middleware.ldap.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>ConnectLdapActivator</code> activates an ldap object by attempting to
 * connect to the ldap.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectLdapActivator implements LdapActivator<Connection>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());


  /** {@inheritDoc} */
  @Override
  public boolean activate(final Connection lc)
  {
    boolean success = false;
    if (lc != null) {
      try {
        lc.open();
        success = true;
      } catch (LdapException e) {
        logger.error("unabled to connect to the ldap", e);
      }
    }
    return success;
  }
}
