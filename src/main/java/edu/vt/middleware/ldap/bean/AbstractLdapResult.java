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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

/**
 * <code>AbstractLdapResult</code> provides a base implementation of
 * <code>LdapResult</code> where the underlying entries are backed by a
 * <code>Map</code>.
 *
 * @param  <T>  type of backing map
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractLdapResult<T extends Map<String, LdapEntry>>
  extends AbstractLdapBean implements LdapResult
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 44;

  /** Entries contained in this result. */
  protected T entries;


  /**
   * Creates a new <code>AbstractLdapResult</code> with the supplied ldap bean
   * factory.
   *
   * @param  lbf  <code>LdapBeanFactory</code>
   */
  public AbstractLdapResult(final LdapBeanFactory lbf)
  {
    super(lbf);
  }


  /** {@inheritDoc} */
  public Collection<LdapEntry> getEntries()
  {
    return this.entries.values();
  }


  /** {@inheritDoc} */
  public LdapEntry getEntry(final String dn)
  {
    return this.entries.get(dn);
  }


  /** {@inheritDoc} */
  public void addEntry(final LdapEntry e)
  {
    this.entries.put(e.getDn(), e);
  }


  /** {@inheritDoc} */
  public void addEntry(final SearchResult sr)
    throws NamingException
  {
    final LdapEntry le = this.beanFactory.newLdapEntry();
    le.setEntry(sr);
    this.addEntry(le);
  }


  /** {@inheritDoc} */
  public void addEntries(final Collection<LdapEntry> c)
  {
    for (LdapEntry e : c) {
      this.entries.put(e.getDn(), e);
    }
  }


  /** {@inheritDoc} */
  public void addEntries(final NamingEnumeration<SearchResult> ne)
    throws NamingException
  {
    while (ne.hasMore()) {
      final LdapEntry le = this.beanFactory.newLdapEntry();
      le.setEntry(ne.next());
      this.addEntry(le);
    }
  }


  /** {@inheritDoc} */
  public void addEntries(final Iterator<SearchResult> i)
    throws NamingException
  {
    while (i.hasNext()) {
      final LdapEntry le = this.beanFactory.newLdapEntry();
      le.setEntry(i.next());
      this.addEntry(le);
    }
  }


  /** {@inheritDoc} */
  public int size()
  {
    return this.entries.size();
  }


  /** {@inheritDoc} */
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
   * This returns a string representation of this object.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return String.format("%s", this.entries.values());
  }


  /** {@inheritDoc} */
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
