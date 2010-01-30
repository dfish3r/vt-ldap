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
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

/**
 * <code>LdapResult</code> represents a collection of ldap entries.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapResult
{


  /**
   * This returns a <code>Collection</code> of <code>LdapEntry</code> for this
   * <code>LdapResult</code>.
   *
   * @return  <code>Collection</code>
   */
  Collection<LdapEntry> getEntries();


  /**
   * This returns the <code>LdapEntry</code> for this <code>LdapResult</code>
   * with the supplied DN.
   *
   * @param  dn  <code>String</code>
   *
   * @return  <code>LdapEntry</code>
   */
  LdapEntry getEntry(final String dn);


  /**
   * This adds a new entry to this <code>LdapResult</code>.
   *
   * @param  e  <code>LdapEntry</code>
   */
  void addEntry(final LdapEntry e);


  /**
   * This adds a new entry to this <code>LdapResult</code>.
   *
   * @param  sr  <code>SearchResult</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  void addEntry(final SearchResult sr)
    throws NamingException;


  /**
   * This adds a <code>Collection</code> of entries to this <code>
   * LdapResult</code>. The list should contain <code>LdapEntry</code> objects.
   *
   * @param  c  <code>Collection</code>
   */
  void addEntries(final Collection<LdapEntry> c);


  /**
   * This adds a <code>NamingEnumeration</code> of <code>SearchResult</code> to
   * this <code>LdapResult</code>.
   *
   * @param  ne  <code>NamingEnumeration</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  void addEntries(final NamingEnumeration<SearchResult> ne)
    throws NamingException;


  /**
   * This adds an <code>Iterator</code> of <code>SearchResult</code> to this
   * <code>LdapResult</code>.
   *
   * @param  i  <code>Iterator</code>
   *
   * @throws  NamingException  if the search results cannot be read
   */
  void addEntries(final Iterator<SearchResult> i)
    throws NamingException;


  /**
   * This returns the number of entries in this result.
   *
   * @return  <code>int</code>
   */
  int size();


  /** This removes all entries from this <code>LdapResult</code>. */
  void clear();


  /**
   * This returns a <code>List</code> of <code>SearchResult</code> that
   * represent the entries in this <code>LdapResult</code>.
   *
   * @return  <code>List</code>
   */
  List<SearchResult> toSearchResults();
}
