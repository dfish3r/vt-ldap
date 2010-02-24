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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <code>OrderedLdapBeanFactory</code> provides an ldap bean factory that
 * produces ordered ldap beans.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OrderedLdapBeanFactory implements LdapBeanFactory
{


  /** {@inheritDoc} */
  public LdapResult newLdapResult()
  {
    return new OrderedLdapResult();
  }


  /** {@inheritDoc} */
  public LdapEntry newLdapEntry()
  {
    return new OrderedLdapEntry();
  }


  /** {@inheritDoc} */
  public LdapAttributes newLdapAttributes()
  {
    return new OrderedLdapAttributes();
  }


  /** {@inheritDoc} */
  public LdapAttribute newLdapAttribute()
  {
    return new OrderedLdapAttribute();
  }


  /**
   * <code>OrderedLdapResult</code> represents a collection of ldap entries
   * that are ordered by insertion.
   */
  protected class OrderedLdapResult
    extends AbstractLdapResult<LinkedHashMap<String, LdapEntry>>
  {


    /** Default constructor. */
    public OrderedLdapResult()
    {
      super(OrderedLdapBeanFactory.this);
      this.entries = new LinkedHashMap<String, LdapEntry>();
    }
  }


  /**
   * <code>OrderedLdapEntry</code> represents a single ldap entry.
   */
  protected class OrderedLdapEntry extends AbstractLdapEntry
  {


    /** Default constructor. */
    public OrderedLdapEntry()
    {
      super(OrderedLdapBeanFactory.this);
      this.ldapAttributes = new OrderedLdapAttributes();
    }
  }


  /**
   * <code>OrderedLdapAttributes</code> represents a collection of ldap
   * attribute that are ordered by insertion.
   */
  protected class OrderedLdapAttributes
    extends AbstractLdapAttributes<LinkedHashMap<String, LdapAttribute>>
  {


    /** Default constructor. */
    public OrderedLdapAttributes()
    {
      super(OrderedLdapBeanFactory.this);
      this.attributes = new LinkedHashMap<String, LdapAttribute>();
    }
  }


  /**
   * <code>OrderedLdapAttribute</code> represents a single ldap attribute whose
   * values are ordered by insertion.
   */
  protected class OrderedLdapAttribute
    extends AbstractLdapAttribute<LinkedHashSet<Object>>
  {


    /** Default constructor. */
    public OrderedLdapAttribute()
    {
      super(OrderedLdapBeanFactory.this);
      this.values = new LinkedHashSet<Object>();
    }


    /** {@inheritDoc} */
    public Set<String> getStringValues()
    {
      final Set<String> s = new LinkedHashSet<String>();
      this.convertValuesToString(s);
      return Collections.unmodifiableSet(s);
    }
  }
}
