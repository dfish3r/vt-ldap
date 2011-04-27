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
package edu.vt.middleware.ldap;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Simple bean for ldap attributes. Contains a map of attribute name to ldap
 * attribute.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdapAttributes extends AbstractLdapBean
{
  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 42;

  /** Attributes contained in this bean. */
  protected Map<String, LdapAttribute> attributes;


  /** Default constructor. */
  public LdapAttributes()
  {
    this(SortBehavior.getDefaultSortBehavior());
  }


  /**
   * Creates a new ldap attributes.
   *
   * @param  sb  sort behavior of the attributes
   */
  public LdapAttributes(final SortBehavior sb)
  {
    super(sb);
    if (SortBehavior.UNORDERED == sb) {
      this.attributes = new HashMap<String, LdapAttribute>();
    } else if (SortBehavior.ORDERED == sb) {
      this.attributes = new LinkedHashMap<String, LdapAttribute>();
    } else if (SortBehavior.SORTED == sb) {
      this.attributes = new TreeMap<String, LdapAttribute>(
        String.CASE_INSENSITIVE_ORDER);
    }
  }


  /**
   * Creates a new ldap attributes.
   *
   * @param  a  attribute to add
   */
  public LdapAttributes(final LdapAttribute a)
  {
    this();
    this.addAttribute(a);
  }


  /**
   * Creates a new ldap attributes.
   *
   * @param  c  collection of attributes to add
   */
  public LdapAttributes(final Collection<LdapAttribute> c)
  {
    this();
    this.addAttributes(c);
  }


  /**
   * Creates a new ldap attributes.
   *
   * @param  name  of attribute to add
   * @param  value  of attribute to add
   */
  public LdapAttributes(final String name, final Object value)
  {
    this();
    this.addAttribute(name, value);
  }


  /**
   * Creates a new ldap attributes.
   *
   * @param  name  of attribute to add
   * @param  values  of attribute to add
   */
  public LdapAttributes(final String name, final Object[] values)
  {
    this();
    this.addAttribute(name, values);
  }


  /**
   * Creates a new ldap attributes.
   *
   * @param  name  of attribute to add
   * @param  values  of attribute to add
   */
  public LdapAttributes(final String name, final List<?> values)
  {
    this();
    this.addAttribute(name, values);
  }


  /**
   * Returns a collection of ldap attribute.
   *
   * @return  collection of ldap attribute
   */
  public Collection<LdapAttribute> getAttributes()
  {
    return this.attributes.values();
  }


  /**
   * Returns a single attribute of this attributes. If multiple attributes exist
   * the first attribute returned by the underlying iterator is used. If no
   * attributes exist null is returned.
   *
   * @return  single attribute
   */
  public LdapAttribute getAttribute()
  {
    if (this.attributes.size() == 0) {
      return null;
    }
    return this.attributes.values().iterator().next();
  }


  /**
   * Returns the attribute with the supplied name.
   *
   * @param  name  of the attribute to return
   * @return  ldap attribute
   */
  public LdapAttribute getAttribute(final String name)
  {
    return this.attributes.get(name);
  }


  /**
   * Returns the attribute names in this ldap attributes.
   *
   * @return  string array of attribute names
   */
  public String[] getAttributeNames()
  {
    return this.attributes.keySet().toArray(
      new String[this.attributes.keySet().size()]);
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  a  attribute to add
   */
  public void addAttribute(final LdapAttribute a)
  {
    this.attributes.put(a.getName(), a);
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  name  of the attribute to add
   * @param  value  of the attribute to add
   */
  public void addAttribute(final String name, final Object value)
  {
    final LdapAttribute la = new LdapAttribute(this.sortBehavior);
    la.setName(name);
    la.getValues().add(value);
    this.addAttribute(la);
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  name  of the attribute to add
   * @param  values  of the attribute to add
   */
  public void addAttribute(final String name, final Object[] values)
  {
    final LdapAttribute la = new LdapAttribute(this.sortBehavior);
    la.setName(name);
    for (Object o : values) {
      la.getValues().add(o);
    }
    this.addAttribute(la);
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  name  of the attribute to add
   * @param  values  of the attribute to add
   */
  public void addAttribute(final String name, final List<?> values)
  {
    final LdapAttribute la = new LdapAttribute(this.sortBehavior);
    la.setName(name);
    la.getValues().addAll(values);
    this.addAttribute(la);
  }


  /**
   * Adds attribute(s) to this ldap attributes.
   *
   * @param  c  collection of attributes to add
   */
  public void addAttributes(final Collection<LdapAttribute> c)
  {
    for (LdapAttribute la : c) {
      this.addAttribute(la);
    }
  }


  /**
   * Removes an attribute from this ldap attributes.
   *
   * @param  a  attribute to remove
   */
  public void removeAttribute(final LdapAttribute a)
  {
    this.attributes.remove(a.getName());
  }


  /**
   * Removes the attribute of the supplied name from this ldap attributes.
   *
   * @param  name  of attribute to remove
   */
  public void removeAttribute(final String name)
  {
    this.attributes.remove(name);
  }


  /**
   * Removes the attribute(s) from this ldap attributes.
   *
   * @param  c  collection of ldap attributes to remove
   */
  public void removeAttributes(final Collection<LdapAttribute> c)
  {
    for (LdapAttribute la : c) {
      this.removeAttribute(la);
    }
  }


  /**
   * Returns the number of attributes in this ldap attributes.
   *
   * @return  number of attributes in this ldap attributes
   */
  public int size()
  {
    return this.attributes.size();
  }


  /**
   * Removes all the attributes in this ldap attributes.
   */
  public void clear()
  {
    this.attributes.clear();
  }


  /** {@inheritDoc} */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    for (LdapAttribute a : this.attributes.values()) {
      hc += a != null ? a.hashCode() : 0;
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
    return String.format("%s", this.attributes.values());
  }
}
