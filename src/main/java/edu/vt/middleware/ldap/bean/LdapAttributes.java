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
package edu.vt.middleware.ldap.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

/**
 * <code>LdapAttributes</code> represents a collection of ldap attribute.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class LdapAttributes extends AbstractLdapBean
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 42;

  /** Attributes contained in this bean. */
  private Map<String, LdapAttribute> attributes =
    new HashMap<String, LdapAttribute>();


  /** Default constructor. */
  public LdapAttributes() {}


  /**
   * This creates a new <code>LdapAttributes</code> with the supplied <code>
   * LdapAttributes</code>.
   *
   * @param  la  <code>LdapAttributes</code>
   */
  public LdapAttributes(final LdapAttributes la)
  {
    this.addAttributes(la.getAttributes());
  }


  /**
   * This creates a new <code>LdapAttributes</code> with the supplied <code>
   * LdapAttribute</code>.
   *
   * @param  la  <code>LdapAttribute</code>
   */
  public LdapAttributes(final LdapAttribute la)
  {
    this.addAttribute(la);
  }


  /**
   * This creates a new <code>LdapAttributes</code> with the supplied
   * attributes.
   *
   * @param  a  <code>Attributes</code>
   *
   * @throws  NamingException  if the attributes cannot be read
   */
  public LdapAttributes(final Attributes a)
    throws NamingException
  {
    this.addAttributes(a);
  }


  /**
   * This returns a <code>Collection</code> of <code>LdapAttribute</code> for
   * this <code>LdapAttributes</code>.
   *
   * @return  <code>List</code>
   */
  public Collection<LdapAttribute> getAttributes()
  {
    return this.attributes.values();
  }


  /**
   * This returns the <code>LdapAttribute</code> for this <code>
   * LdapAttributes</code> with the supplied name.
   *
   * @param  name  <code>String</code>
   *
   * @return  <code>LdapAttribute</code>
   */
  public LdapAttribute getAttribute(final String name)
  {
    return this.attributes.get(name);
  }


  /**
   * This returns an array of all the attribute names for this <code>
   * LdapAttributes</code>.
   *
   * @return  <code>String[]</code>
   */
  public String[] getAttributeNames()
  {
    return (String[]) this.attributes.keySet().toArray(new String[0]);
  }


  /**
   * This adds a new attribute to this <code>LdapAttributes</code>.
   *
   * @param  a  <code>LdapAttribute</code>
   */
  public void addAttribute(final LdapAttribute a)
  {
    this.attributes.put(a.getName(), a);
  }


  /**
   * This adds a new attribute to this <code>LdapAttributes</code> with the
   * supplied name and value.
   *
   * @param  name  <code>String</code>
   * @param  value  <code>Object</code>
   */
  public void addAttribute(final String name, final Object value)
  {
    this.addAttribute(new LdapAttribute(name, value));
  }


  /**
   * This adds a new attribute to this <code>LdapAttributes</code> with the
   * supplied name and values.
   *
   * @param  name  <code>String</code>
   * @param  values  <code>List</code>
   */
  public void addAttribute(final String name, final List<?> values)
  {
    this.addAttribute(new LdapAttribute(name, values));
  }


  /**
   * This adds a <code>Collection</code> of attributes to this <code>
   * LdapAttributes</code>. The collection should contain <code>
   * LdapAttribute</code> objects.
   *
   * @param  c  <code>Collection</code>
   */
  public void addAttributes(final Collection<LdapAttribute> c)
  {
    for (LdapAttribute la : c) {
      this.addAttribute(la);
    }
  }


  /**
   * This adds the attributes in the supplied <code>Attributes</code> to this
   * <code>LdapAttributes</code>.
   *
   * @param  a  <code>Attributes</code>
   *
   * @throws  NamingException  if the attributes cannot be read
   */
  public void addAttributes(final Attributes a)
    throws NamingException
  {
    final NamingEnumeration<? extends Attribute> ne = a.getAll();
    while (ne.hasMore()) {
      this.addAttribute(new LdapAttribute(ne.next()));
    }
  }


  /**
   * This removes an attribute from this <code>LdapAttributes</code>.
   *
   * @param  a  <code>LdapAttribute</code>
   */
  public void removeAttribute(final LdapAttribute a)
  {
    this.attributes.remove(a.getName());
  }


  /**
   * This removes the attribute with the supplied name.
   *
   * @param  name  <code>String</code>
   */
  public void removeAttribute(final String name)
  {
    this.attributes.remove(name);
  }


  /**
   * This removes a <code>Collection</code> of attributes from this <code>
   * LdapAttributes</code>. The collection should contain <code>
   * LdapAttribute</code> objects.
   *
   * @param  c  <code>Collection</code>
   */
  public void removeAttributes(final Collection<LdapAttribute> c)
  {
    for (LdapAttribute la : c) {
      this.removeAttribute(la);
    }
  }


  /**
   * This removes the attributes in the supplied <code>Attributes</code> from
   * this <code>LdapAttributes</code>.
   *
   * @param  a  <code>Attributes</code>
   *
   * @throws  NamingException  if the attributes cannot be read
   */
  public void removeAttributes(final Attributes a)
    throws NamingException
  {
    final NamingEnumeration<? extends Attribute> ne = a.getAll();
    while (ne.hasMore()) {
      this.removeAttribute(new LdapAttribute(ne.next()));
    }
  }


  /**
   * This returns the number of attributes in this attributes.
   *
   * @return  <code>int</code>
   */
  public int size()
  {
    return this.attributes.size();
  }


  /** This removes all attributes from this <code>LdapAttributes</code>. */
  public void clear()
  {
    this.attributes.clear();
  }


  /** {@inheritDoc}. */
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
  public String toString()
  {
    return this.attributes.values().toString();
  }


  /**
   * This returns an <code>Attributes</code> that represents this entry.
   * Attributes will be case-insensitive.
   *
   * @return  <code>Attributes</code>
   */
  public Attributes toAttributes()
  {
    return this.toAttributes(true);
  }


  /**
   * This returns an <code>Attributes</code> that represents this entry.
   * Attributes will be case-insensitive.
   *
   * @param  ignoreCase whether to ignore attribute case
   * @return  <code>Attributes</code>
   */
  public Attributes toAttributes(final boolean ignoreCase)
  {
    final Attributes attributes = new BasicAttributes(ignoreCase);
    for (LdapAttribute a : this.attributes.values()) {
      attributes.put(a.toAttribute());
    }
    return attributes;
  }
}
