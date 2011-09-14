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
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.provider.Provider;

/**
 * Factory for creating connections.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ConnectionFactory
{


  /**
   * Returns the connection config.
   *
   * @return  connection config
   */
  ConnectionConfig getConnectionConfig();


  /**
   * Sets the connection config.
   *
   * @param  cc  connection config
   */
  void setConnectionConfig(ConnectionConfig cc);


  /**
   * Returns the ldap provider.
   *
   * @return  ldap provider
   */
  Provider<?> getProvider();


  /**
   * Sets the ldap provider.
   *
   * @param  p  ldap provider to set
   */
  void setProvider(final Provider<?> p);


  /**
   * Creates a new connection.
   *
   * @return  connection
   *
   * @throws  LdapException  if a connection cannot be returned
   */
  Connection getConnection() throws LdapException;
}
