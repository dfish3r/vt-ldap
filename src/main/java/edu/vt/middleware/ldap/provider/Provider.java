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
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.ResultCode;

/**
 * Provides access to a provider specific connection factory.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public interface Provider
{


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
   * Returns the connection factory for this provider.
   *
   * @param  lcc  ldap connection configuration
   * @return  connection factory
   */
  ProviderConnectionFactory getConnectionFactory(ConnectionConfig lcc);
}
