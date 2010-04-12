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
package edu.vt.middleware.ldap.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.Ldap;

/**
 * <code>RecursiveSearchResultHandler</code> recursively searches based on a
 * supplied attribute and merges those results into the original result set.
 * For the following LDIF:
 * <pre>
 * dn: uugid=group1,ou=groups,dc=vt,dc=edu
 * uugid: group1
 * member: uugid=group2,ou=groups,dc=vt,dc=edu

 * dn: uugid=group2,ou=groups,dc=vt,dc=edu
 * uugid: group2
 * </pre>
 * With the following code:
 * <pre>
 * RecursiveSearchResultHandler rsh = new RecurseSearchResultHandler(
 *   ldap, "member", new String[]{"uugid"});
 * </pre>
 * Will produce this result for the query (uugid=group1):
 * <pre>
 * dn: uugid=group1,ou=groups,dc=vt,dc=edu
 * uugid: group1
 * uugid: group2
 * member: uugid=group2,ou=groups,dc=vt,dc=edu
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RecursiveSearchResultHandler extends CopySearchResultHandler
  implements ExtendedSearchResultHandler
{
  /** Ldap to use for searching. */
  private Ldap ldap;

  /** Attribute to recursively search on. */
  private String searchAttribute;

  /** Attribute(s) to merge. */
  private String[] mergeAttributes;

  /** Attributes to return when searching, mergeAttributes + searchAttribute. */
  private String[] retAttrs;


  /**
   * Default constructor.
   */
  public RecursiveSearchResultHandler() {}


  /**
   * Creates a new <code>RecursiveAttributeHandler</code> with the supplied
   * search attribute and merge attributes.
   *
   * @param  searchAttr  <code>String</code>
   * @param  mergeAttrs  <code>String[]</code>
   */
  public RecursiveSearchResultHandler(
    final String searchAttr, final String[] mergeAttrs)
  {
    this(null, searchAttr, mergeAttrs);
  }


  /**
   * Creates a new <code>RecursiveAttributeHandler</code> with the supplied
   * ldap, search attribute, and merge attributes.
   *
   * @param  l  <code>Ldap</code>
   * @param  searchAttr  <code>String</code>
   * @param  mergeAttrs  <code>String[]</code>
   */
  public RecursiveSearchResultHandler(
    final Ldap l, final String searchAttr, final String[] mergeAttrs)
  {
    this.ldap = l;
    this.searchAttribute = searchAttr;
    this.mergeAttributes = mergeAttrs;
    this.initalizeReturnAttributes();
  }


  /** {@inheritDoc} */
  public Ldap getSearchResultLdap()
  {
    return this.ldap;
  }


  /** {@inheritDoc} */
  public void setSearchResultLdap(final Ldap l)
  {
    this.ldap = l;
  }


  /**
   * Returns the attribute name that will be recursively searched on.
   *
   * @return  <code>String</code> attribute name
   */
  public String getSearchAttribute()
  {
    return this.searchAttribute;
  }


  /**
   * Sets the attribute name that will be recursively searched on.
   *
   * @param  s <code>String</code>
   */
  public void setSearchAttribute(final String s)
  {
    this.searchAttribute = s;
    this.initalizeReturnAttributes();
  }


  /**
   * Returns the attribute names that will be merged by the recursive search.
   *
   * @return  <code>String[]</code> attribute names
   */
  public String[] getMergeAttributes()
  {
    return this.mergeAttributes;
  }


  /**
   * Sets the attribute name that will be merged by the recursive search.
   *
   * @param  s <code>String[]</code>
   */
  public void setMergeAttributes(final String[] s)
  {
    this.mergeAttributes = s;
    this.initalizeReturnAttributes();
  }


  /**
   * Initializes the return attributes array. Must be called after both
   * searchAttribute and mergeAttributes have been set.
   */
  protected void initalizeReturnAttributes()
  {
    if (this.mergeAttributes != null && this.searchAttribute != null) {
      // return attributes must include the search attribute
      this.retAttrs = new String[this.mergeAttributes.length + 1];
      System.arraycopy(
        this.mergeAttributes, 0, this.retAttrs, 0, this.mergeAttributes.length);
      this.retAttrs[this.retAttrs.length - 1] = this.searchAttribute;
    }
  }


  /** {@inheritDoc} */
  public List<SearchResult> process(
    final SearchCriteria sc,
    final NamingEnumeration<? extends SearchResult> en,
    final Class<?>[] ignore)
    throws NamingException
  {
    return this.processInternal(super.process(sc, en, ignore));
  }


  /** {@inheritDoc} */
  public List<SearchResult> process(
    final SearchCriteria sc,
    final List<? extends SearchResult> l)
    throws NamingException
  {
    return this.processInternal(super.process(sc, l));
  }


  /**
   * Recursively searches a list of attributes and merges those results with
   * the existing search result set.
   *
   * @param  results  <code>List</code> of search results to merge with
   *
   * @return  <code>List</code> of merged search results
   *
   * @throws  NamingException  if an error occurs reading attribute values
   */
  private List<SearchResult> processInternal(final List<SearchResult> results)
    throws NamingException
  {
    for (SearchResult sr : results) {
      final List<String> searchedDns = new ArrayList<String>();
      if (sr.getAttributes().get(this.searchAttribute) != null) {
        searchedDns.add(sr.getName());
        this.readSearchAttribute(sr.getAttributes(), searchedDns);
      } else {
        this.recursiveSearch(
          sr.getName(), sr.getAttributes(), searchedDns);
      }
    }
    return results;
  }


  /**
   * Reads the values of {@link #searchAttribute} from the supplied attributes
   * and calls {@link #recursiveSearch} for each.
   *
   * @param  attrs  to read
   * @param  searchedDns  list of DNs whose attributes have been read
   *
   * @throws  NamingException  if a search error occurs
   */
  private void readSearchAttribute(
    final Attributes attrs, final List<String> searchedDns)
    throws NamingException
  {
    if (attrs != null) {
      final Attribute attr = attrs.get(this.searchAttribute);
      if (attr != null) {
        final NamingEnumeration<?> en = attr.getAll();
        while (en.hasMore()) {
          final Object rawValue = en.next();
          if (rawValue instanceof String) {
            this.recursiveSearch((String) rawValue, attrs, searchedDns);
          }
        }
      }
    }
  }


  /**
   * Recursively gets the attribute(s) {@link #mergeAttributes} for the supplied
   * dn and adds the values to the supplied attributes.
   *
   * @param  dn  to get attribute(s) for
   * @param  attrs  to merge with
   * @param  searchedDns  list of DNs that have been searched for
   *
   * @throws  NamingException  if a search error occurs
   */
  private void recursiveSearch(
    final String dn,
    final Attributes attrs,
    final List<String> searchedDns)
    throws NamingException
  {
    if (!searchedDns.contains(dn)) {

      Attributes newAttrs = null;
      try {
        newAttrs = this.ldap.getAttributes(dn, this.retAttrs);
      } catch (NamingException e) {
        if (this.logger.isWarnEnabled()) {
          this.logger.warn(
            "Error retreiving attribute(s): " + Arrays.toString(this.retAttrs),
            e);
        }
      }
      searchedDns.add(dn);

      if (newAttrs != null) {
        // recursively search new attributes
        this.readSearchAttribute(newAttrs, searchedDns);

        // merge new attribute values
        for (String s : this.mergeAttributes) {
          final Attribute newAttr = newAttrs.get(s);
          if (newAttr != null) {
            final Attribute oldAttr = attrs.get(s);
            if (oldAttr == null) {
              attrs.put(newAttr);
            } else {
              final NamingEnumeration<?> newValues = newAttr.getAll();
              while (newValues.hasMore()) {
                oldAttr.add(newValues.next());
              }
            }
          }
        }
      }
    }
  }
}
