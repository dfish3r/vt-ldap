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

import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.auth.AuthenticatorConfig;
import edu.vt.middleware.ldap.provider.Connection;
import edu.vt.middleware.ldap.provider.ConnectionFactory;

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
   * @param  ac  authenticator config
   */
  public BindAuthenticationHandler(final AuthenticatorConfig ac)
  {
    this.setAuthenticatorConfig(ac);
  }


  /** {@inheritDoc} */
  public Connection authenticate(
    final ConnectionFactory cf,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    return cf.create(ac.getDn(), ac.getCredential());
  }


  /** {@inheritDoc} */
  public BindAuthenticationHandler newInstance()
  {
    return new BindAuthenticationHandler(this.config);
  }
}
