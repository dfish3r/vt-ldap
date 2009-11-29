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
 * <code>DnResolver</code> provides an interface for finding LDAP DNs.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface DnResolver
{

  /**
   * Attempts to find the LDAP DN for the supplied user.
   *
   * @param  user  <code>String</code> to find dn for
   *
   * @return  <code>String</code> - user's dn
   *
   * @throws  NamingException  if an LDAP error occurs
   */
  String resolve(String user) throws NamingException;


  /**
   * Returns the authenticator config.
   *
   * @return  authenticator configuration
   */
  AuthenticatorConfig getAuthenticatorConfig();


  /**
   * Sets the authenticator config.
   *
   * @param  config  authenticator configuration
   */
  void setAuthenticatorConfig(AuthenticatorConfig config);


  /** This will close any resources associated with this resolver. */
  void close();
}
