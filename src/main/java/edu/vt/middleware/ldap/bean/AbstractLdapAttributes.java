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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

/**
 * <code>AbstractLdapAttributes</code> provides a base implementation of
 * <code>LdapAttributes</code> where the underlying attributes are backed by a
 * <code>Map</code>.
 *
 * @param  <T>  type of backing map
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class
AbstractLdapAttributes<T extends Map<String, LdapAttribute>>
  extends AbstractLdapBean implements LdapAttributes
{

  /** Whether to ignore case when creating <code>BasicAttributes</code>. */
  public static final boolean DEFAULT_IGNORE_CASE = true;

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 42;

  /** Attributes contained in this bean. */
  protected T attributes;


  /**
   * Creates a new <code>AbstractLdapAttributes</code> with the supplied ldap
   * bean factory.
   *
   * @param  lbf  <code>LdapBeanFactory</code>
   */
  public AbstractLdapAttributes(final LdapBeanFactory lbf)
  {
    super(lbf);
  }


  /** {@inheritDoc} */
  public Collection<LdapAttribute> getAttributes()
  {
    return this.attributes.values();
  }


  /** {@inheritDoc} */
  public LdapAttribute getAttribute(final String name)
  {
    return this.attributes.get(name);
  }


  /** {@inheritDoc} */
  public String[] getAttributeNames()
  {
    return this.attributes.keySet().toArray(new String[0]);
  }


  /** {@inheritDoc} */
  public void addAttribute(final LdapAttribute a)
  {
    this.attributes.put(a.getName(), a);
  }


  /** {@inheritDoc} */
  public void addAttribute(final String name, final Object value)
  {
    final LdapAttribute la = this.beanFactory.newLdapAttribute();
    la.setName(name);
    la.getValues().add(value);
    this.addAttribute(la);
  }


  /** {@inheritDoc} */
  public void addAttribute(final String name, final List<?> values)
  {
    final LdapAttribute la = this.beanFactory.newLdapAttribute();
    la.setName(name);
    la.getValues().addAll(values);
    this.addAttribute(la);
  }


  /** {@inheritDoc} */
  public void addAttributes(final Collection<LdapAttribute> c)
  {
    for (LdapAttribute la : c) {
      this.addAttribute(la);
    }
  }


  /** {@inheritDoc} */
  public void addAttributes(final Attributes a)
    throws NamingException
  {
    final NamingEnumeration<? extends Attribute> ne = a.getAll();
    while (ne.hasMore()) {
      final LdapAttribute la = this.beanFactory.newLdapAttribute();
      la.setAttribute(ne.next());
      this.addAttribute(la);
    }
  }


  /** {@inheritDoc} */
  public void removeAttribute(final LdapAttribute a)
  {
    this.attributes.remove(a.getName());
  }


  /** {@inheritDoc} */
  public void removeAttribute(final String name)
  {
    this.attributes.remove(name);
  }


  /** {@inheritDoc} */
  public void removeAttributes(final Collection<LdapAttribute> c)
  {
    for (LdapAttribute la : c) {
      this.removeAttribute(la);
    }
  }


  /** {@inheritDoc} */
  public void removeAttributes(final Attributes a)
    throws NamingException
  {
    final NamingEnumeration<? extends Attribute> ne = a.getAll();
    while (ne.hasMore()) {
      final LdapAttribute la = this.beanFactory.newLdapAttribute();
      la.setAttribute(ne.next());
      this.removeAttribute(la);
    }
  }


  /** {@inheritDoc} */
  public int size()
  {
    return this.attributes.size();
  }


  /** {@inheritDoc} */
  public void clear()
  {
    this.attributes.clear();
  }


  /** {@inheritDoc} */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    for (LdapAttribute a : this.attributes.values()) {
      if (a != null) {
        hc += a.hashCode();
      }
    }
    return hc;
  }


  /**
   * This returns a string representation of this object.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return String.format("%s", this.attributes.values());
  }


  /** {@inheritDoc} */
  public Attributes toAttributes()
  {
    final Attributes attributes = new BasicAttributes(DEFAULT_IGNORE_CASE);
    for (LdapAttribute a : this.attributes.values()) {
      attributes.put(a.toAttribute());
    }
    return attributes;
  }
}
