/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
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
 * <code>ConnectLdapValidator</code> validates an ldap connection is healthy by
 * testing it is connected.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectLdapValidator implements LdapValidator<Ldap>
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());


  /** {@inheritDoc} */
  public boolean validate(final Ldap l)
  {
    boolean success = false;
    if (l != null) {
      try {
        success = l.connect();
      } catch (NamingException e) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("validation failed for " + l, e);
        }
      }
    }
    return success;
  }
}
