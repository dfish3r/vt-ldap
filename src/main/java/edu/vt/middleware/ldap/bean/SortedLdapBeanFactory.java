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
package edu.vt.middleware.ldap.bean;

import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * <code>SortedLdapBeanFactory</code> provides an ldap bean factory that
 * produces sorted ldap beans.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SortedLdapBeanFactory implements LdapBeanFactory
{


  /** {@inheritDoc} */
  public LdapResult newLdapResult()
  {
    return new SortedLdapResult();
  }


  /** {@inheritDoc} */
  public LdapEntry newLdapEntry()
  {
    return new SortedLdapEntry();
  }


  /** {@inheritDoc} */
  public LdapAttributes newLdapAttributes()
  {
    return new SortedLdapAttributes();
  }


  /** {@inheritDoc} */
  public LdapAttribute newLdapAttribute()
  {
    return new SortedLdapAttribute();
  }


  /**
   * <code>SortedLdapResult</code> represents a collection of ldap entries
   * that are sorted by their DN.
   */
  protected class SortedLdapResult
    extends AbstractLdapResult<TreeMap<String, LdapEntry>>
  {


    /** Default constructor. */
    public SortedLdapResult()
    {
      super(SortedLdapBeanFactory.this);
      this.entries = new TreeMap<String, LdapEntry>(
        String.CASE_INSENSITIVE_ORDER);
    }
  }


  /**
   * <code>SortedLdapEntry</code> represents a single ldap entry.
   */
  protected class SortedLdapEntry extends AbstractLdapEntry
  {


    /** Default constructor. */
    public SortedLdapEntry()
    {
      super(SortedLdapBeanFactory.this);
      this.ldapAttributes = new SortedLdapAttributes();
    }
  }


  /**
   * <code>SortedLdapAttributes</code> represents a collection of ldap attribute
   * that are sorted by their name.
   */
  protected class SortedLdapAttributes
    extends AbstractLdapAttributes<TreeMap<String, LdapAttribute>>
  {


    /** Default constructor. */
    public SortedLdapAttributes()
    {
      super(SortedLdapBeanFactory.this);
      this.attributes = new TreeMap<String, LdapAttribute>(
        String.CASE_INSENSITIVE_ORDER);
    }
  }


  /**
   * <code>SortedLdapAttribute</code> represents a single ldap attribute whose
   * values are sorted.
   */
  protected class SortedLdapAttribute
    extends AbstractLdapAttribute<TreeSet<Object>>
  {


    /** Default constructor. */
    public SortedLdapAttribute()
    {
      super(SortedLdapBeanFactory.this);
      this.values = new TreeSet<Object>();
    }


    /** {@inheritDoc} */
    public Set<String> getStringValues()
    {
      final Set<String> s = new TreeSet<String>();
      this.convertValuesToString(s);
      return Collections.unmodifiableSet(s);
    }
  }
}
