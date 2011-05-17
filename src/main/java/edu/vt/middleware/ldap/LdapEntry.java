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
import java.util.Map;
import java.util.TreeMap;

/**
 * Simple bean for an ldap entry. Contains a DN and ldap attributes.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdapEntry extends AbstractLdapBean
{
  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 43;

  /** Distinguished name for this entry. */
  protected String dn;

  /** Attributes contained in this bean. */
  protected Map<String, LdapAttribute> ldapAttributes;


  /** Default constructor. */
  public LdapEntry()
  {
    this(SortBehavior.getDefaultSortBehavior());
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  sb  sort behavior
   */
  public LdapEntry(final SortBehavior sb)
  {
    super(sb);
    if (SortBehavior.UNORDERED == sb) {
      ldapAttributes = new HashMap<String, LdapAttribute>();
    } else if (SortBehavior.ORDERED == sb) {
      ldapAttributes = new LinkedHashMap<String, LdapAttribute>();
    } else if (SortBehavior.SORTED == sb) {
      ldapAttributes = new TreeMap<String, LdapAttribute>(
        String.CASE_INSENSITIVE_ORDER);
    }
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  s  dn for this entry
   */
  public LdapEntry(final String s)
  {
    this();
    setDn(s);
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  s  dn for this entry
   * @param  la  ldap attribute for this entry
   */
  public LdapEntry(final String s, final LdapAttribute ... la)
  {
    this();
    setDn(s);
    for (LdapAttribute a : la) {
      addAttribute(a);
    }
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  s  dn for this entry
   * @param  c  collection of attributes to add
   */
  public LdapEntry(final String s, final Collection<LdapAttribute> c)
  {
    this();
    setDn(s);
    addAttributes(c);
  }


  /**
   * Returns the DN.
   *
   * @return  entry DN
   */
  public String getDn()
  {
    return dn;
  }


  /**
   * Sets the DN.
   *
   * @param  s  dn to set
   */
  public void setDn(final String s)
  {
    dn = s;
  }


  /**
   * Returns a collection of ldap attribute.
   *
   * @return  collection of ldap attribute
   */
  public Collection<LdapAttribute> getAttributes()
  {
    return ldapAttributes.values();
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
    if (ldapAttributes.size() == 0) {
      return null;
    }
    return ldapAttributes.values().iterator().next();
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
      return ldapAttributes.get(name.toLowerCase());
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
    final String[] names = new String[ldapAttributes.size()];
    int i = 0;
    for (LdapAttribute la : ldapAttributes.values()) {
      names[i++] = la.getName();
    }
    return names;
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  la  attribute to add
   */
  public void addAttribute(final LdapAttribute ... la)
  {
    for (LdapAttribute a : la) {
      ldapAttributes.put(a.getName().toLowerCase(), a);
    }
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
   * @param  la  attribute to remove
   */
  public void removeAttribute(final LdapAttribute ... la)
  {
    for (LdapAttribute a : la) {
      ldapAttributes.remove(a.getName().toLowerCase());
    }
  }


  /**
   * Removes the attribute of the supplied name from this ldap attributes.
   *
   * @param  name  of attribute to remove
   */
  public void removeAttribute(final String name)
  {
    ldapAttributes.remove(name.toLowerCase());
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
    return ldapAttributes.size();
  }


  /**
   * Removes all the attributes in this ldap attributes.
   */
  public void clear()
  {
    ldapAttributes.clear();
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    hc += dn != null ? dn.toLowerCase().hashCode() : 0;
    for (LdapAttribute la : ldapAttributes.values()) {
      if (la != null) {
        hc += la.hashCode();
      }
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
    return String.format("dn=>%s%s", dn, ldapAttributes);
  }
}
