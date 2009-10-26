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
package edu.vt.middleware.ldap;

import javax.naming.NamingException;

/**
 * <code>BaseLdap</code> provides a base interface for all ldap implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface BaseLdap
{


  /**
   * This will establish a connection to the ldap.
   *
   * @return  <code>boolean</code> - whether the connection was successfull
   *
   * @throws  NamingException  if the LDAP cannot be reached
   */
  boolean connect()
    throws NamingException;


  /**
   * This will close the connection to the LDAP and establish a new connection.
   *
   * @return  <code>boolean</code> - whether the connection was successfull
   *
   * @throws  NamingException  if the LDAP cannot be reached
   */
  boolean reconnect()
    throws NamingException;


  /** This will close the connection to the LDAP. */
  void close();
}
