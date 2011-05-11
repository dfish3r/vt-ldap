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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>ConnectLdapValidator</code> validates an ldap connection is healthy by
 * testing it is connected.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectLdapValidator implements LdapValidator<LdapConnection>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(this.getClass());


  /** {@inheritDoc} */
  public boolean validate(final LdapConnection lc)
  {
    boolean success = false;
    if (lc != null) {
      try {
        lc.open();
        success = true;
      } catch (LdapException e) {
        this.logger.debug("validation failed for {}", lc, e);
      }
    }
    return success;
  }
}
