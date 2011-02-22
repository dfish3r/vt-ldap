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
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides a custom implementation for grouping principals.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapGroup implements Group, Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -342760961669842632L;

  /** LDAP role name. */
  private String name;

  /** Principal members. */
  private Set<Principal> members = new HashSet<Principal>();


  /**
   * Creates a new ldap group with the supplied name.
   *
   * @param  name  of the group
   */
  public LdapGroup(final String name)
  {
    this.name = name;
  }


  /**
   * Returns the name for this ldap group.
   *
   * @return  name
   */
  public String getName()
  {
    return this.name;
  }


  /** {@inheritDoc} */
  public boolean addMember(final Principal user)
  {
    return this.members.add(user);
  }


  /** {@inheritDoc} */
  public boolean removeMember(final Principal user)
  {
    return this.members.remove(user);
  }


  /** {@inheritDoc} */
  public boolean isMember(final Principal member)
  {
    for (Principal p : this.members) {
      if (p.getName() != null && p.getName().equals(member.getName())) {
        return true;
      }
    }
    return false;
  }


  /** {@inheritDoc} */
  public Enumeration<? extends Principal> members()
  {
    return Collections.enumeration(this.members);
  }


  /**
   * Returns an unmodifiable set of the members in this group.
   *
   * @return  set of member principals
   */
  public Set<Principal> getMembers()
  {
    return Collections.unmodifiableSet(this.members);
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
      this.members);
  }
}
