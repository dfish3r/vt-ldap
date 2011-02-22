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
import java.security.Principal;

/**
 * Provides a custom implementation for adding LDAP principals to a subject
 * that represent roles.
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
   * Creates a new ldap role with the supplied name.
   *
   * @param  name  of this role
   */
  public LdapRole(final String name)
  {
    this.name = name;
  }


  /**
   * Returns the name for this ldap role.
   *
   * @return  role name
   */
  public String getName()
  {
    return this.name;
  }


  /**
   * Returns whether the supplied object is equal to this ldap role.
   *
   * @param  o  to compare
   *
   * @return  whether the supplied object is equal
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
   * Returns the hash code for this ldap role.
   *
   * @return  hash code
   */
  public int hashCode()
  {
    return this.name.hashCode();
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
      this.getClass().getName(),
      this.hashCode(),
      this.name);
  }


  /**
   * Principals are compared lexicographically by name. See
   * {@link Comparable#compareTo(Object)}.
   *
   * @param  p  principal to compare
   *
   * @return  a negative integer, zero, or a positive integer as this object is
   * less than, equal to, or greater than the specified object.
   */
  public int compareTo(final Principal p)
  {
    return this.name.compareTo(p.getName());
  }
}
