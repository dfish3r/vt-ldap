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

import java.io.Serializable;
import javax.naming.NamingException;

/**
 * <code>NoopDnResolver</code> returns the user as the LDAP DN.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NoopDnResolver implements DnResolver, Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -7832850056696716639L;


  /** Default constructor. */
  public NoopDnResolver() {}


  /**
   * This method is not implemented.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public void setAuthenticatorConfig(final AuthenticatorConfig authConfig) {}


  /**
   * This method is not implemented.
   *
   * @return  null
   */
  public AuthenticatorConfig getAuthenticatorConfig()
  {
    return null;
  }


  /**
   * Returns the user as the LDAP DN.
   *
   * @param  user  <code>String</code> to find dn for
   *
   * @return  <code>String</code> - user's dn
   *
   * @throws  NamingException  if the LDAP search fails
   */
  public String resolve(final String user)
    throws NamingException
  {
    return user;
  }


  /** {@inheritDoc} */
  public void close() {}
}
