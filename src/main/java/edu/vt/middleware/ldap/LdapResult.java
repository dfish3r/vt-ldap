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
 * Simple bean for ldap result. Contains a map of entry DN to ldap entry.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdapResult extends AbstractLdapBean
{
  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 44;

  /** Entries contained in this result. */
  protected Map<String, LdapEntry> entries;


  /** Default constructor. */
  public LdapResult()
  {
    this(SortBehavior.getDefaultSortBehavior());
  }


  /**
   * Creates a new ldap result.
   *
   * @param  sb  sort behavior of the results
   */
  public LdapResult(final SortBehavior sb)
  {
    super(sb);
    if (SortBehavior.UNORDERED == sb) {
      this.entries = new HashMap<String, LdapEntry>();
    } else if (SortBehavior.ORDERED == sb) {
      this.entries = new LinkedHashMap<String, LdapEntry>();
    } else if (SortBehavior.SORTED == sb) {
      this.entries = new TreeMap<String, LdapEntry>(
        String.CASE_INSENSITIVE_ORDER);
    }
  }


  /**
   * Creates a new ldap result.
   *
   * @param  le  ldap entry
   */
  public LdapResult(final LdapEntry le)
  {
    this();
    this.addEntry(le);
  }


  /**
   * Creates a new ldap result.
   *
   * @param  c  collection of ldap entries
   */
  public LdapResult(final Collection<LdapEntry> c)
  {
    this();
    this.addEntries(c);
  }


  /**
   * Returns a collection of ldap entry.
   *
   * @return  collection of ldap entry
   */
  public Collection<LdapEntry> getEntries()
  {
    return this.entries.values();
  }


  /**
   * Returns a single entry of this result. If multiple entries exist the first
   * entry returned by the underlying iterator is used. If no entries exist null
   * is returned.
   *
   * @return  single entry
   */
  public LdapEntry getEntry()
  {
    if (this.entries.size() == 0) {
      return null;
    }
    return this.entries.values().iterator().next();
  }


  /**
   * Returns the ldap in this result with the supplied DN.
   *
   * @param  dn  of the entry to return
   * @return  ldap entry
   */
  public LdapEntry getEntry(final String dn)
  {
    return this.entries.get(dn);
  }


  /**
   * Returns the entry DNs in this result.
   *
   * @return  string array of entry DNs
   */
  public String[] getEntryDns()
  {
    return this.entries.keySet().toArray(
      new String[this.entries.keySet().size()]);
  }


  /**
   * Adds an entry to this ldap result.
   *
   * @param  le  entry to add
   */
  public void addEntry(final LdapEntry le)
  {
    this.entries.put(le.getDn(), le);
  }


  /**
   * Adds entry(s) to this ldap result.
   *
   * @param  c  collection of entries to add
   */
  public void addEntries(final Collection<LdapEntry> c)
  {
    for (LdapEntry e : c) {
      this.entries.put(e.getDn(), e);
    }
  }


  /**
   * Returns the number of entries in this ldap result.
   *
   * @return  number of entries in this ldap result
   */
  public int size()
  {
    return this.entries.size();
  }


  /**
   * Removes all the entries in this ldap result.
   */
  public void clear()
  {
    this.entries.clear();
  }


  /** {@inheritDoc} */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    for (LdapEntry e : this.entries.values()) {
      if (e != null) {
        hc += e.hashCode();
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
    return String.format("%s", this.entries.values());
  }
}
