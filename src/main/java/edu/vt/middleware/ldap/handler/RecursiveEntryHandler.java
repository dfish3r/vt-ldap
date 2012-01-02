/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
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
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * This recursively searches based on a supplied attribute and merges those
 * results into the original entry. For the following LDIF:
 *
 * <pre>
   dn: uugid=group1,ou=groups,dc=vt,dc=edu
   uugid: group1
   member: uugid=group2,ou=groups,dc=vt,dc=edu

   dn: uugid=group2,ou=groups,dc=vt,dc=edu
   uugid: group2
 * </pre>
 *
 * <p>With the following code:</p>
 *
 * <pre>
   RecursiveEntryHandler reh = new RecursiveEntryHandler(
     conn, "member", new String[]{"uugid"});
 * </pre>
 *
 * <p>Will produce this result for the query (uugid=group1):</p>
 *
 * <pre>
   dn: uugid=group1,ou=groups,dc=vt,dc=edu
   uugid: group1
   uugid: group2
   member: uugid=group2,ou=groups,dc=vt,dc=edu
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision: 2193 $ $Date: 2011-12-15 17:01:04 -0500 (Thu, 15 Dec 2011) $
 */
public class RecursiveEntryHandler extends AbstractLdapEntryHandler
  implements ExtendedLdapEntryHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 829;

  /** Connection to use for searching. */
  private Connection connection;

  /** Attribute to recursively search on. */
  private String searchAttribute;

  /** Attribute(s) to merge. */
  private String[] mergeAttributes;

  /** Attributes to return when searching, mergeAttributes + searchAttribute. */
  private String[] retAttrs;


  /** Default constructor. */
  public RecursiveEntryHandler() {}


  /**
   * Creates a new recursive entry handler.
   *
   * @param  searchAttr  attribute to search on
   * @param  mergeAttrs  attribute names to merge
   */
  public RecursiveEntryHandler(
    final String searchAttr,
    final String[] mergeAttrs)
  {
    this(null, searchAttr, mergeAttrs);
  }


  /**
   * Creates a new recursive entry handler.
   *
   * @param  c  connection
   * @param  searchAttr  attribute to search on
   * @param  mergeAttrs  attribute names to merge
   */
  public RecursiveEntryHandler(
    final Connection c,
    final String searchAttr,
    final String[] mergeAttrs)
  {
    connection = c;
    searchAttribute = searchAttr;
    mergeAttributes = mergeAttrs;
    initalizeReturnAttributes();
  }


  /** {@inheritDoc} */
  @Override
  public Connection getResultConnection()
  {
    return connection;
  }


  /** {@inheritDoc} */
  @Override
  public void setResultConnection(final Connection c)
  {
    connection = c;
  }


  /**
   * Returns the attribute name that will be recursively searched on.
   *
   * @return  attribute name
   */
  public String getSearchAttribute()
  {
    return searchAttribute;
  }


  /**
   * Sets the attribute name that will be recursively searched on.
   *
   * @param  name  of the search attribute
   */
  public void setSearchAttribute(final String name)
  {
    searchAttribute = name;
    initalizeReturnAttributes();
  }


  /**
   * Returns the attribute names that will be merged by the recursive search.
   *
   * @return  attribute names
   */
  public String[] getMergeAttributes()
  {
    return mergeAttributes;
  }


  /**
   * Sets the attribute name that will be merged by the recursive search.
   *
   * @param  mergeAttrs  attribute names to merge
   */
  public void setMergeAttributes(final String[] mergeAttrs)
  {
    mergeAttributes = mergeAttrs;
    initalizeReturnAttributes();
  }


  /**
   * Initializes the return attributes array. Must be called after both
   * searchAttribute and mergeAttributes have been set.
   */
  protected void initalizeReturnAttributes()
  {
    if (mergeAttributes != null && searchAttribute != null) {
      // return attributes must include the search attribute
      retAttrs = new String[mergeAttributes.length + 1];
      System.arraycopy(mergeAttributes, 0, retAttrs, 0, mergeAttributes.length);
      retAttrs[retAttrs.length - 1] = searchAttribute;
    }
  }


  /** {@inheritDoc} */
  @Override
  public HandlerResult process(
    final SearchCriteria criteria,
    final LdapEntry entry)
    throws LdapException
  {
    // Recursively searches a list of attributes and merges those results with
    // the existing entry.
    final List<String> searchedDns = new ArrayList<String>();
    if (entry.getAttribute(searchAttribute) != null) {
      searchedDns.add(entry.getDn());
      readSearchAttribute(entry, searchedDns);
    } else {
      recursiveSearch(entry.getDn(), entry, searchedDns);
    }
    return new HandlerResult(entry);
  }


  /**
   * Reads the values of {@link #searchAttribute} from the supplied attributes
   * and calls {@link #recursiveSearch} for each.
   *
   * @param  entry  to read
   * @param  searchedDns  list of DNs whose attributes have been read
   *
   * @throws  LdapException  if a search error occurs
   */
  private void readSearchAttribute(
    final LdapEntry entry,
    final List<String> searchedDns)
    throws LdapException
  {
    if (entry != null) {
      final LdapAttribute attr = entry.getAttribute(searchAttribute);
      if (attr != null && !attr.isBinary()) {
        for (String s : attr.getStringValues()) {
          recursiveSearch(s, entry, searchedDns);
        }
      }
    }
  }


  /**
   * Recursively gets the attribute(s) {@link #mergeAttributes} for the supplied
   * dn and adds the values to the supplied attributes.
   *
   * @param  dn  to get attribute(s) for
   * @param  entry  to merge with
   * @param  searchedDns  list of DNs that have been searched for
   *
   * @throws  LdapException  if a search error occurs
   */
  private void recursiveSearch(
    final String dn,
    final LdapEntry entry,
    final List<String> searchedDns)
    throws LdapException
  {
    if (!searchedDns.contains(dn)) {

      LdapEntry newEntry = null;
      try {
        final SearchOperation search = new SearchOperation(connection);
        final SearchRequest sr = SearchRequest.newObjectScopeSearchRequest(
          dn,
          retAttrs);
        final LdapResult result = search.execute(sr).getResult();
        newEntry = result.getEntry(dn);
      } catch (LdapException e) {
        logger.warn(
          "Error retreiving attribute(s): {}",
          Arrays.toString(retAttrs),
          e);
      }
      searchedDns.add(dn);

      if (newEntry != null) {
        // recursively search new attributes
        readSearchAttribute(newEntry, searchedDns);

        // merge new attribute values
        for (String s : mergeAttributes) {
          final LdapAttribute newAttr = newEntry.getAttribute(s);
          if (newAttr != null) {
            final LdapAttribute oldAttr = entry.getAttribute(s);
            if (oldAttr == null) {
              entry.addAttribute(newAttr);
            } else {
              if (newAttr.isBinary()) {
                for (byte[] value : newAttr.getBinaryValues()) {
                  oldAttr.addBinaryValue(value);
                }
              } else {
                for (String value : newAttr.getStringValues()) {
                  oldAttr.addStringValue(value);
                }
              }
            }
          }
        }
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return
      LdapUtil.computeHashCode(
        HASH_CODE_SEED,
        mergeAttributes,
        retAttrs,
        searchAttribute);
  }
}
