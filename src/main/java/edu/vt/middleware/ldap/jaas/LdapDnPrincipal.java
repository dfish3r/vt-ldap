/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
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
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapUtil;

/**
 * Provides a custom implementation for adding LDAP principals to a subject.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapDnPrincipal
  implements Principal, Serializable, Comparable<Principal>
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 409;

  /** serial version uid. */
  private static final long serialVersionUID = -4530972236127507368L;

  /** LDAP user name. */
  private String ldapDn;

  /** User ldap entry. */
  private LdapEntry ldapEntry = new LdapEntry();


  /**
   * Creates a new ldap principal with the supplied name.
   *
   * @param  name  of an ldap DN
   * @param  entry  ldap entry associated with this principal
   */
  public LdapDnPrincipal(final String name, final LdapEntry entry)
  {
    ldapDn = name;
    ldapEntry = entry;
  }


  /**
   * Returns the name for this ldap principal.
   *
   * @return  name
   */
  public String getName()
  {
    return ldapDn;
  }


  /**
   * Returns the ldap entry for this ldap principal.
   *
   * @return  ldap entry
   */
  public LdapEntry getLdapEntry()
  {
    return ldapEntry;
  }


  /**
   * Returns whether the supplied object is equal to this ldap principal.
   *
   * @param  o  to compare
   *
   * @return  whether the supplied object is equal
   */
  @Override
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this || (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /**
   * Returns the hash code for this ldap principal.
   *
   * @return  hash code
   */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(HASH_CODE_SEED, ldapDn);
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::%s%s]",
        getClass().getName(),
        hashCode(),
        ldapDn,
        ldapEntry != null ? ldapEntry : "");
  }


  /**
   * Principals are compared lexicographically by name. See {@link
   * Comparable#compareTo(Object)}.
   *
   * @param  p  principal to compare
   *
   * @return  a negative integer, zero, or a positive integer as this object is
   * less than, equal to, or greater than the specified object.
   */
  @Override
  public int compareTo(final Principal p)
  {
    return ldapDn.compareTo(p.getName());
  }
}
