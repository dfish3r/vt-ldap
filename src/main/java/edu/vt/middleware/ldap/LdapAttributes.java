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
      attributes = new HashMap<String, LdapAttribute>();
    } else if (SortBehavior.ORDERED == sb) {
      attributes = new LinkedHashMap<String, LdapAttribute>();
    } else if (SortBehavior.SORTED == sb) {
      attributes = new TreeMap<String, LdapAttribute>(
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
    addAttribute(a);
  }


  /**
   * Creates a new ldap attributes.
   *
   * @param  c  collection of attributes to add
   */
  public LdapAttributes(final Collection<LdapAttribute> c)
  {
    this();
    addAttributes(c);
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
    addAttribute(name, value);
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
    addAttribute(name, values);
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
    addAttribute(name, values);
  }


  /**
   * Returns a collection of ldap attribute.
   *
   * @return  collection of ldap attribute
   */
  public Collection<LdapAttribute> getAttributes()
  {
    return attributes.values();
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
    if (attributes.size() == 0) {
      return null;
    }
    return attributes.values().iterator().next();
  }


  /**
   * Returns the attribute with the supplied name.
   *
   * @param  name  of the attribute to return
   * @return  ldap attribute
   */
  public LdapAttribute getAttribute(final String name)
  {
    if (name != null) {
      return attributes.get(name.toLowerCase());
    }
    return null;
  }


  /**
   * Returns the attribute names in this ldap attributes.
   *
   * @return  string array of attribute names
   */
  public String[] getAttributeNames()
  {
    final String[] names = new String[attributes.size()];
    int i = 0;
    for (LdapAttribute la : attributes.values()) {
      names[i++] = la.getName();
    }
    return names;
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  a  attribute to add
   */
  public void addAttribute(final LdapAttribute a)
  {
    attributes.put(a.getName().toLowerCase(), a);
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  name  of the attribute to add
   * @param  value  of the attribute to add
   */
  public void addAttribute(final String name, final Object value)
  {
    final LdapAttribute la = new LdapAttribute(sortBehavior);
    la.setName(name);
    la.getValues().add(value);
    addAttribute(la);
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  name  of the attribute to add
   * @param  values  of the attribute to add
   */
  public void addAttribute(final String name, final Object[] values)
  {
    final LdapAttribute la = new LdapAttribute(sortBehavior);
    la.setName(name);
    for (Object o : values) {
      la.getValues().add(o);
    }
    addAttribute(la);
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  name  of the attribute to add
   * @param  values  of the attribute to add
   */
  public void addAttribute(final String name, final List<?> values)
  {
    final LdapAttribute la = new LdapAttribute(sortBehavior);
    la.setName(name);
    la.getValues().addAll(values);
    addAttribute(la);
  }


  /**
   * Adds attribute(s) to this ldap attributes.
   *
   * @param  c  collection of attributes to add
   */
  public void addAttributes(final Collection<LdapAttribute> c)
  {
    for (LdapAttribute la : c) {
      addAttribute(la);
    }
  }


  /**
   * Removes an attribute from this ldap attributes.
   *
   * @param  a  attribute to remove
   */
  public void removeAttribute(final LdapAttribute a)
  {
    attributes.remove(a.getName().toLowerCase());
  }


  /**
   * Removes the attribute of the supplied name from this ldap attributes.
   *
   * @param  name  of attribute to remove
   */
  public void removeAttribute(final String name)
  {
    attributes.remove(name.toLowerCase());
  }


  /**
   * Removes the attribute(s) from this ldap attributes.
   *
   * @param  c  collection of ldap attributes to remove
   */
  public void removeAttributes(final Collection<LdapAttribute> c)
  {
    for (LdapAttribute la : c) {
      removeAttribute(la);
    }
  }


  /**
   * Returns the number of attributes in this ldap attributes.
   *
   * @return  number of attributes in this ldap attributes
   */
  public int size()
  {
    return attributes.size();
  }


  /**
   * Removes all the attributes in this ldap attributes.
   */
  public void clear()
  {
    attributes.clear();
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    for (LdapAttribute a : attributes.values()) {
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
    return String.format("%s", attributes.values());
  }
}
