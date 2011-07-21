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

import java.util.Map;
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
   * Returns provider specific properties.
   *
   * @return  map of additional provider properties
   */
  Map<String, Object> getProperties();


  /**
   * Sets provider specific properties.
   *
   * @param  props  map of additional provider properties
   */
  void setProperties(final Map<String, Object> props);


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
