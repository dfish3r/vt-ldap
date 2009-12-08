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
package edu.vt.middleware.ldap.auth.handler;

import javax.naming.NamingException;
import edu.vt.middleware.ldap.auth.AuthenticatorConfig;
import edu.vt.middleware.ldap.handler.ConnectionHandler;

/**
 * <code>BindAuthenticationHandler</code> provides an LDAP authentication
 * implementation that leverages the LDAP bind operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class BindAuthenticationHandler extends AbstractAuthenticationHandler
{


  /**
   * Default constructor.
   */
  public BindAuthenticationHandler() {}


  /**
   * Creates a new <code>BindAuthenticationHandler</code> with the supplied
   * authenticator config.
   *
   * @param  ac  authenticator config
   */
  public BindAuthenticationHandler(final AuthenticatorConfig ac)
  {
    this.setAuthenticatorConfig(ac);
  }


  /** {@inheritDoc} */
  public void authenticate(
    final ConnectionHandler ch, final AuthenticationCriteria ac)
    throws NamingException
  {
    ch.connect(ac.getDn(), ac.getCredential());
  }


  /** {@inheritDoc} */
  public BindAuthenticationHandler newInstance()
  {
    return new BindAuthenticationHandler(this.config);
  }
}
