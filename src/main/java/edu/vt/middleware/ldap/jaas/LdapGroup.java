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
package edu.vt.middleware.ldap.jaas;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * <code>LdapGroup</code> provides a custom implementation for grouping
 * principals.
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
   * This creates a new <code>LdapGroup</code> with the supplied name.
   *
   * @param  name  <code>String</code>
   */
  public LdapGroup(final String name)
  {
    this.name = name;
  }


  /**
   * This returns the name for this <code>LdapGroup</code>.
   *
   * @return  <code>String</code>
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
   * @return  <code>Set</code> of member principals
   */
  public Set<Principal> getMembers()
  {
    return Collections.unmodifiableSet(this.members);
  }


  /**
   * This returns a String representation of this <code>LdapGroup</code>.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return String.format("%s%s", this.name, this.members);
  }
}
