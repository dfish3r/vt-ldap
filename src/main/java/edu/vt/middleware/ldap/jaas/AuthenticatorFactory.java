/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.jaas;

import java.util.Map;
import edu.vt.middleware.ldap.auth.AuthenticationRequest;
import edu.vt.middleware.ldap.auth.Authenticator;

/**
 * Provides an interface for creating authenticators needed by various JAAS
 * modules.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface AuthenticatorFactory
{


  /**
   * Creates a new authenticator with the supplied JAAS options.
   *
   * @param  jaasOptions  JAAS configuration options
   *
   * @return  authenticator
   */
  Authenticator createAuthenticator(Map<String, ?> jaasOptions);


  /**
   * Creates a new authentication request with the supplied JAAS options.
   *
   * @param  jaasOptions  JAAS configuration options
   *
   * @return  authentication request
   */
  AuthenticationRequest createAuthenticationRequest(Map<String, ?> jaasOptions);
}
