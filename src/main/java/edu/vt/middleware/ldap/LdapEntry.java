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
 * Simple bean representing an ldap entry. Contains a DN and ldap attributes.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdapEntry extends AbstractLdapBean
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 331;

  /** Distinguished name for this entry. */
  private String entryDn;

  /** Attributes contained in this bean. */
  private final Map<String, LdapAttribute> entryAttributes;


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
      entryAttributes = new HashMap<String, LdapAttribute>();
    } else if (SortBehavior.ORDERED == sb) {
      entryAttributes = new LinkedHashMap<String, LdapAttribute>();
    } else if (SortBehavior.SORTED == sb) {
      entryAttributes = new TreeMap<String, LdapAttribute>(
        String.CASE_INSENSITIVE_ORDER);
    } else {
      throw new IllegalArgumentException("Unknown sort behavior: " + sb);
    }
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  dn  dn for this entry
   */
  public LdapEntry(final String dn)
  {
    this();
    setDn(dn);
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  dn  dn for this entry
   * @param  attr  ldap attribute for this entry
   */
  public LdapEntry(final String dn, final LdapAttribute ... attr)
  {
    this();
    setDn(dn);
    for (LdapAttribute a : attr) {
      addAttribute(a);
    }
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  dn  dn for this entry
   * @param  attrs  collection of attributes to add
   */
  public LdapEntry(final String dn, final Collection<LdapAttribute> attrs)
  {
    this();
    setDn(dn);
    addAttributes(attrs);
  }


  /**
   * Returns the DN.
   *
   * @return  entry DN
   */
  public String getDn()
  {
    return entryDn;
  }


  /**
   * Sets the DN.
   *
   * @param  dn  dn to set
   */
  public void setDn(final String dn)
  {
    entryDn = dn;
  }


  /**
   * Returns a collection of ldap attribute.
   *
   * @return  collection of ldap attribute
   */
  public Collection<LdapAttribute> getAttributes()
  {
    return entryAttributes.values();
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
    if (entryAttributes.size() == 0) {
      return null;
    }
    return entryAttributes.values().iterator().next();
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
      return entryAttributes.get(name.toLowerCase());
    }
    return null;
  }


  /**
   * Returns the attribute names in this entry.
   *
   * @return  string array of attribute names
   */
  public String[] getAttributeNames()
  {
    final String[] names = new String[entryAttributes.size()];
    int i = 0;
    for (LdapAttribute la : entryAttributes.values()) {
      names[i++] = la.getName();
    }
    return names;
  }


  /**
   * Adds an attribute to this ldap attributes.
   *
   * @param  attr  attribute to add
   */
  public void addAttribute(final LdapAttribute ... attr)
  {
    for (LdapAttribute a : attr) {
      entryAttributes.put(a.getName().toLowerCase(), a);
    }
  }


  /**
   * Adds attribute(s) to this ldap attributes.
   *
   * @param  attrs  collection of attributes to add
   */
  public void addAttributes(final Collection<LdapAttribute> attrs)
  {
    for (LdapAttribute la : attrs) {
      addAttribute(la);
    }
  }


  /**
   * Removes an attribute from this ldap attributes.
   *
   * @param  attr  attribute to remove
   */
  public void removeAttribute(final LdapAttribute ... attr)
  {
    for (LdapAttribute a : attr) {
      entryAttributes.remove(a.getName().toLowerCase());
    }
  }


  /**
   * Removes the attribute of the supplied name from this ldap attributes.
   *
   * @param  name  of attribute to remove
   */
  public void removeAttribute(final String name)
  {
    entryAttributes.remove(name.toLowerCase());
  }


  /**
   * Removes the attribute(s) from this ldap attributes.
   *
   * @param  attrs  collection of ldap attributes to remove
   */
  public void removeAttributes(final Collection<LdapAttribute> attrs)
  {
    for (LdapAttribute la : attrs) {
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
    return entryAttributes.size();
  }


  /**
   * Removes all the attributes in this ldap attributes.
   */
  public void clear()
  {
    entryAttributes.clear();
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(
      HASH_CODE_SEED,
      entryDn != null ? entryDn.toLowerCase() : null,
      entryAttributes.values());
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return String.format("[dn=%s%s]", entryDn, entryAttributes);
  }
}
