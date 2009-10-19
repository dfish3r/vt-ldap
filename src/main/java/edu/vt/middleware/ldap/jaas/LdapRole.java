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
 * <code>LdapRole</code> provides a custom implementation for adding LDAP
 * principals to a <code>Subject</code> that represent roles.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapRole implements Principal, Serializable, Comparable<Principal>
{

  /** serial version uid. */
  private static final long serialVersionUID = 1427032827399935399L;

  /** LDAP role name. */
  private String name;


  /**
   * This creates a new <code>LdapRole</code> with the supplied name.
   *
   * @param  name  <code>String</code>
   */
  public LdapRole(final String name)
  {
    this.name = name;
  }


  /**
   * This returns the name for this <code>LdapRole</code>.
   *
   * @return  <code>String</code>
   */
  public String getName()
  {
    return this.name;
  }


  /**
   * This returns the supplied Object is equal to this <code>LdapRole</code>.
   *
   * @param  o  <code>Object</code>
   *
   * @return  <code>boolean</code>
   */
  public boolean equals(final Object o)
  {
    boolean b = false;
    if (o != null) {
      if (this != o) {
        if (o instanceof LdapRole) {
          if (((LdapRole) o).getName().equals(this.name)) {
            b = true;
          }
        }
      } else {
        b = true;
      }
    }
    return b;
  }


  /**
   * This returns the hash code for this <code>LdapRole</code>.
   *
   * @return  <code>int</code>
   */
  public int hashCode()
  {
    return this.name.hashCode();
  }


  /**
   * This returns a String representation of this <code>LdapRole</code>.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return this.name;
  }


  /**
   * This compares the supplied object for order. <code>LdapRole</code> is
   * always greater than any other object. Otherwise principals are compared
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
