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
import edu.vt.middleware.ldap.LdapAttributes;

/**
 * Provides a custom implementation for adding LDAP principals to a subject.
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
  private static final long serialVersionUID = -1043578648596801523L;

  /** LDAP user name. */
  private String name;

  /** User attributes. */
  private LdapAttributes attributes = new LdapAttributes();


  /**
   * Creates a new ldap principal with the supplied name.
   *
   * @param  name  of this principal
   */
  public LdapPrincipal(final String name)
  {
    this.name = name;
  }


  /**
   * Returns the name for this ldap principal.
   *
   * @return  ldap principal name
   */
  public String getName()
  {
    return this.name;
  }


  /**
   * Returns the ldap attributes for this ldap principal.
   *
   * @return  ldap attributes
   */
  public LdapAttributes getLdapAttributes()
  {
    return this.attributes;
  }


  /**
   * Returns whether the supplied object is equal to this ldap principal.
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
        (this.getClass() == o.getClass() && o.hashCode() == this.hashCode());
  }


  /**
   * Returns the hash code for this ldap principal.
   *
   * @return  hash code
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
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return String.format(
      "%s@%d::%s%s",
      this.getClass().getName(),
      this.hashCode(),
      this.name,
      this.attributes);
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
