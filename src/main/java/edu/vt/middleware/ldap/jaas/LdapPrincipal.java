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

import java.io.Serializable;
import java.security.Principal;

/**
 * <code>LdapPrincipal</code> provides a custom implementation for adding LDAP
 * principals to a <code>Subject</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapPrincipal
  implements Principal, Serializable, Comparable<Principal>
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 79;

  /** serial version uid. */
  private static final long serialVersionUID = 1575574310250098272L;

  /** LDAP user name. */
  private String name;


  /**
   * This creates a new <code>LdapPrincipal</code> with the supplied name.
   *
   * @param  name  <code>String</code>
   */
  public LdapPrincipal(final String name)
  {
    this.name = name;
  }


  /**
   * This returns the name for this <code>LdapPrincipal</code>.
   *
   * @return  <code>String</code>
   */
  public String getName()
  {
    return this.name;
  }


  /**
   * This returns the supplied Object is equal to this <code>
   * LdapPrincipal</code>.
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
    if (this.name != null) {
      hc += this.name.hashCode();
    }
    return hc;
  }


  /**
   * This returns a String representation of this <code>LdapPrincipal</code>.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return this.name;
  }


  /**
   * This compares the supplied object for order. <code>LdapPrincipal</code> is
   * always less than any other object. Otherwise principals are compared
   * lexicographically on name.
   *
   * @param  p  <code>Principal</code>
   *
   * @return  <code>int</code>
   */
  public int compareTo(final Principal p)
  {
    return this.name.compareTo(p.getName());
  }
}
