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
package edu.vt.middleware.ldap.jaas;

import java.io.Serializable;

/**
 * <code>LdapCredential</code> provides a custom implementation for adding LDAP
 * credentials to a <code>Subject</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapCredential implements Serializable
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 89;

  /** serial version uid. */
  private static final long serialVersionUID = 6571981350905290712L;

  /** LDAP credential. */
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


  /**
   * This returns the supplied Object is equal to this <code>
   * LdapCredential</code>.
   *
   * @param  o  <code>Object</code>
   *
   * @return  <code>boolean</code>
   */
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this ||
        (this.getClass() == o.getClass() && o.hashCode() == this.hashCode());
  }


  /**
   * This returns the hash code for this <code>LdapPrincipal</code>.
   *
   * @return  <code>int</code>
   */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    if (this.credential != null) {
      hc += this.credential.hashCode();
    }
    return hc;
  }
}
