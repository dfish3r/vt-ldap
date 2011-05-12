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

  /** LDAP connection configuration. */
  protected LdapConnectionConfig config;


  /** {@inheritDoc} */
  @Override
  public LdapConnectionConfig getLdapConnectionConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setLdapConnectionConfig(final LdapConnectionConfig lcc)
  {
    config = lcc;
  }
}
