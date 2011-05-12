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
 * Provides a custom implementation for adding LDAP credentials to a subject.
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
   * Creates a new ldap credential with the supplied credential.
   *
   * @param  o  credential to store
   */
  public LdapCredential(final Object o)
  {
    credential = o;
  }


  /**
   * Returns the credential for this ldap credential.
   *
   * @return  credential
   */
  public Object getCredential()
  {
    return credential;
  }


  /**
   * Returns whether the supplied object is equal to this ldap credential.
   *
   * @param  o  to compare
   *
   * @return  whether the supplied object is equal
   */
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this ||
        (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /**
   * Returns the hash code for this ldap credential.
   *
   * @return  hash code
   */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    if (credential != null) {
      hc += credential.hashCode();
    }
    return hc;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return String.format(
        "%s@%d::%s",
        getClass().getName(),
        hashCode(),
        credential);
  }
}
