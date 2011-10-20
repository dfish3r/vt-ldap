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

/**
 * Interface for ldap connection implementations.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public interface Connection
{


  /**
   * Returns the connection config for this connection. The config may be
   * read-only.
   *
   * @return  connection config
   */
  ConnectionConfig getConnectionConfig();


  /**
   * This will establish a connection if one does not already exist to the LDAP.
   *
   * @return  response associated with the bind operation
   *
   * @throws  LdapException  if the LDAP cannot be reached
   */
  Response<Void> open() throws LdapException;


  /**
   * This will establish a connection if one does not already exist by binding
   * to the LDAP using the supplied bind request. This connection should be
   * closed using {@link #close()}.
   *
   * @param  request  containing bind information
   *
   * @return  response associated with the bind operation
   *
   * @throws  IllegalStateExcepiton  if the connection is already open
   * @throws  LdapException  if the LDAP cannot be reached
   */
  Response<Void> open(BindRequest request) throws LdapException;


  /**
   * Returns the provider connection to invoke the provider specific
   * implementation. Must be called after a successful call to {@link #open()}.
   *
   * @return  provider connection
   */
  edu.vt.middleware.ldap.provider.Connection getProviderConnection();


  /** This will close the connection to the LDAP. */
  void close();
}
