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

/**
 * Provides an LDAP authentication implementation that leverages the LDAP bind
 * operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class BindAuthenticationHandler extends AbstractAuthenticationHandler
{


  /** Default constructor. */
  public BindAuthenticationHandler() {}


  /**
   * Creates a new bind authentication handler.
   *
   * @param  lcc  ldap connection config
   */
  public BindAuthenticationHandler(final LdapConnectionConfig lcc)
  {
    this.setLdapConnectionConfig(lcc);
  }


  /** {@inheritDoc} */
  public Connection authenticate(final AuthenticationCriteria ac)
    throws LdapException
  {
    return this.config.getConnectionFactory().create(
      ac.getDn(), ac.getCredential());
  }


  /** {@inheritDoc} */
  public BindAuthenticationHandler newInstance()
  {
    return new BindAuthenticationHandler(this.config);
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "%s@%d: config=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.config);
  }
}
