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

import edu.vt.middleware.ldap.AuthenticationType;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ResultCode;

/**
 * Provides an interface for creating provider connections.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ProviderConnectionFactory
{


  /**
   * Returns the connection strategy.
   *
   * @return  strategy for making connections
   */
  ConnectionStrategy getConnectionStrategy();


  /**
   * Sets the connection strategy.
   *
   * @param  strategy  for making connections
   */
  void setConnectionStrategy(ConnectionStrategy strategy);


  /**
   * Returns the result codes that trigger an operation retry.
   *
   * @return  ldap result codes
   */
  ResultCode[] getOperationRetryResultCodes();


  /**
   * Sets the result codes that trigger an operation retry.
   *
   * @param  codes  ldap result codes
   */
  void setOperationRetryResultCodes(ResultCode[] codes);


  /**
   * Returns the authentication type.
   *
   * @return  authentication type
   */
  AuthenticationType getAuthenticationType();


  /**
   * Sets the authentication type.
   *
   * @param  type  authentication type
   */
  void setAuthenticationType(AuthenticationType type);


  /**
   * Returns whether authentication credentials will be logged.
   *
   * @return  whether authentication credentials will be logged
   */
  boolean getLogCredentials();


  /**
   * Sets whether authentication credentials will be logged.
   *
   * @param  b  whether authentication credentials will be logged
   */
  void setLogCredentials(boolean b);


  /**
   * Create a connection to an LDAP.
   *
   * @param  dn  to attempt bind with
   * @param  credential  to attempt bind with
   *
   * @throws  AuthenticationException  if the supplied credentials are invalid
   * @throws  LdapException  if an LDAP error occurs
   */
  ProviderConnection create(String dn, Credential credential)
    throws LdapException;
}
