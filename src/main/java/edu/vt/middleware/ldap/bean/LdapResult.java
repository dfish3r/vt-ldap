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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

/**
 * <code>LdapResult</code> represents a collection of ldap entries.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class LdapResult extends AbstractLdapBean
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 44;

  /** Entries contained in this result. */
  private Map<String, LdapEntry> entries = new HashMap<String, LdapEntry>();


  /** Default constructor. */
  public LdapResult() {}


  /**
   * This will create a new <code>LdapResult</code> with the supplied <code>
   * LdapResult</code>.
   *
   * @param  lr  <code>LdapResult</code>
   */
  public LdapResult(final LdapResult lr)
  {
    this.addEntries(lr.getEntries());
  }


  /**
   * This will create a new <code>LdapResult</code> with the supplied <code>
   * LdapEntry</code>.
   *
   * @param  e  <code>LdapEntry</code>
   */
  public LdapResult(final LdapEntry e)
  {
    this.addEntry(e);
  }


  /**
   * This will create a new <code>LdapResult</code> with the supplied
   * enumeration of <code>SearchResult</code>.
   *
   * @param  ne  <code>NamingEnumeration</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  public LdapResult(final NamingEnumeration<SearchResult> ne)
    throws NamingException
  {
    this.addEntries(ne);
  }


  /**
   * This will create a new <code>LdapResult</code> with the supplied iterator
   * of <code>SearchResult</code>.
   *
   * @param  i  <code>Iterator</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  public LdapResult(final Iterator<SearchResult> i)
    throws NamingException
  {
    this.addEntries(i);
  }


  /**
   * This will create a new <code>LdapResult</code> with the supplied <code>
   * SearchResult</code>.
   *
   * @param  sr  <code>SearchResult</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  public LdapResult(final SearchResult sr)
    throws NamingException
  {
    this.addEntry(sr);
  }


  /**
   * This returns a <code>Collection</code> of <code>LdapEntry</code> for this
   * <code>LdapResult</code>.
   *
   * @return  <code>Collection</code>
   */
  public Collection<LdapEntry> getEntries()
  {
    return this.entries.values();
  }


  /**
   * This returns the <code>LdapEntry</code> for this <code>LdapResult</code>
   * with the supplied DN.
   *
   * @param  dn  <code>String</code>
   *
   * @return  <code>LdapEntry</code>
   */
  public LdapEntry getEntry(final String dn)
  {
    return this.entries.get(dn);
  }


  /**
   * This adds a new entry to this <code>LdapResult</code>.
   *
   * @param  e  <code>LdapEntry</code>
   */
  public void addEntry(final LdapEntry e)
  {
    this.entries.put(e.getDn(), e);
  }


  /**
   * This adds a new entry to this <code>LdapResult</code>.
   *
   * @param  sr  <code>SearchResult</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  public void addEntry(final SearchResult sr)
    throws NamingException
  {
    this.addEntry(new LdapEntry(sr));
  }


  /**
   * This adds a <code>Collection</code> of entries to this <code>
   * LdapResult</code>. The list should contain <code>LdapEntry</code> objects.
   *
   * @param  c  <code>Collection</code>
   */
  public void addEntries(final Collection<LdapEntry> c)
  {
    for (LdapEntry e : c) {
      this.entries.put(e.getDn(), e);
    }
  }


  /**
   * This adds a <code>NamingEnumeration</code> of <code>SearchResult</code> to
   * this <code>LdapResult</code>.
   *
   * @param  ne  <code>NamingEnumeration</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  public void addEntries(final NamingEnumeration<SearchResult> ne)
    throws NamingException
  {
    while (ne.hasMore()) {
      this.addEntry(new LdapEntry(ne.next()));
    }
  }


  /**
   * This adds an <code>Iterator</code> of <code>SearchResult</code> to this
   * <code>LdapResult</code>.
   *
   * @param  i  <code>Iterator</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  public void addEntries(final Iterator<SearchResult> i)
    throws NamingException
  {
    while (i.hasNext()) {
      this.addEntry(new LdapEntry(i.next()));
    }
  }


  /** This removes all entries from this <code>LdapResult</code>. */
  public void clear()
  {
    this.entries.clear();
  }


  /** {@inheritDoc}. */
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
   * This returns a string representation of this object.
   *
   * @return  <code>String</code>
   */
  public String toString()
  {
    return this.entries.values().toString();
  }


  /**
   * This returns a <code>List</code> of <code>SearchResult</code> that
   * represent the entries in this <code>LdapResult</code>.
   *
   * @return  <code>List</code>
   */
  public List<SearchResult> toSearchResults()
  {
    final List<SearchResult> results = new ArrayList<SearchResult>(
      this.entries.size());
    for (LdapEntry e : this.entries.values()) {
      results.add(e.toSearchResult());
    }
    return results;
  }
}
