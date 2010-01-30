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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * <code>UnorderedLdapBeanFactory</code> provides an ldap bean factory that
 * produces unordered ldap beans.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class UnorderedLdapBeanFactory implements LdapBeanFactory
{


  /** {@inheritDoc} */
  public LdapResult newLdapResult()
  {
    return new UnorderedLdapResult();
  }


  /** {@inheritDoc} */
  public LdapEntry newLdapEntry()
  {
    return new UnorderedLdapEntry();
  }


  /** {@inheritDoc} */
  public LdapAttributes newLdapAttributes()
  {
    return new UnorderedLdapAttributes();
  }


  /** {@inheritDoc} */
  public LdapAttribute newLdapAttribute()
  {
    return new UnorderedLdapAttribute();
  }


  /**
   * <code>UnorderedLdapResult</code> represents a collection of ldap entries
   * that are unordered.
   */
  public class UnorderedLdapResult
    extends AbstractLdapResult<HashMap<String, LdapEntry>>
  {


    /** Default constructor. */
    public UnorderedLdapResult()
    {
      super(UnorderedLdapBeanFactory.this);
      this.entries = new HashMap<String, LdapEntry>();
    }
  }


  /**
   * <code>UnorderedLdapEntry</code> represents a single ldap entry.
   */
  public class UnorderedLdapEntry extends AbstractLdapEntry
  {


    /** Default constructor. */
    public UnorderedLdapEntry()
    {
      super(UnorderedLdapBeanFactory.this);
      this.ldapAttributes = new UnorderedLdapAttributes();
    }
  }


  /**
   * <code>UnorderedLdapAttributes</code> represents a collection of ldap
   * attribute that are unordered.
   */
  public class UnorderedLdapAttributes
    extends AbstractLdapAttributes<HashMap<String, LdapAttribute>>
  {


    /** Default constructor. */
    public UnorderedLdapAttributes()
    {
      super(UnorderedLdapBeanFactory.this);
      this.attributes = new HashMap<String, LdapAttribute>();
    }
  }


  /**
   * <code>UnorderedLdapAttribute</code> represents a single ldap attribute
   * whose values are unordered.
   */
  public class UnorderedLdapAttribute
    extends AbstractLdapAttribute<HashSet<Object>>
  {


    /** Default constructor. */
    public UnorderedLdapAttribute()
    {
      super(UnorderedLdapBeanFactory.this);
      this.values = new HashSet<Object>();
    }


    /** {@inheritDoc} */
    public Set<String> getStringValues()
    {
      final Set<String> s = new HashSet<String>();
      this.convertValuesToString(s);
      return Collections.unmodifiableSet(s);
    }
  }
}
