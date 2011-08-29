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
package edu.vt.middleware.ldap.provider;

import edu.vt.middleware.ldap.BindRequest;
import edu.vt.middleware.ldap.LdapException;

/**
 * Provides an interface for creating provider connections.
 *
 * @param  <T>  type of provider config for this connection factory
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ProviderConnectionFactory<T extends ProviderConfig>
{


  /**
   * Returns the provider configuration.
   *
   * @return  provider configuration
   */
  T getProviderConfig();


  /**
   * Sets the provider configuration.
   *
   * @param  pc  provider configuration
   */
  void setProviderConfig(T pc);


  /**
   * Create a connection to an LDAP.
   *
   * @param  request  bind request
   *
   * @throws  AuthenticationException  if the supplied credentials are invalid
   * @throws  LdapException  if an LDAP error occurs
   */
  ProviderConnection create(BindRequest request)
    throws LdapException;
}
