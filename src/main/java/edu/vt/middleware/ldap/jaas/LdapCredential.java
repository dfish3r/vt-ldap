/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.jaas;

/**
 * <code>LdapCredential</code> provides a custom implementation for adding LDAP
 * credentials to a <code>Subject</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapCredential
{

  /** LDAP credential */
  private Object credential;


  /**
   * This creates a new <code>LdapCredential</code> with the supplied
   * credential.
   *
   * @param  credential  <code>Object</code>
   */
  public LdapCredential(final Object credential)
  {
    this.credential = credential;
  }


  /**
   * This returns the credential for this <code>LdapCredential</code>.
   *
   * @return  <code>Object</code>
   */
  public Object getCredential()
  {
    return this.credential;
  }
}
