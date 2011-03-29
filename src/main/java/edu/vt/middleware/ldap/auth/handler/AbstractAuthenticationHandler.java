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
package edu.vt.middleware.ldap.auth.handler;

import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.provider.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AbstractAuthenticationHandler provides a base implementation for
 * authentication handlers.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractAuthenticationHandler
  implements AuthenticationHandler
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Ldap connection configuration. */
  protected LdapConnectionConfig config;


  /** {@inheritDoc} */
  public LdapConnectionConfig getLdapConnectionConfig()
  {
    return this.config;
  }


  /** {@inheritDoc} */
  public void setLdapConnectionConfig(final LdapConnectionConfig lcc)
  {
    this.config = lcc;
  }


  /** {@inheritDoc} */
  public void destroy(final Connection conn)
    throws LdapException
  {
    this.config.getConnectionFactory().destroy(conn);
  }
}
