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
package edu.vt.middleware.ldap.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapAttributes;
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * This recursively searches based on a supplied attribute and merges those
 * results into the original result set. For the following LDIF:
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
   RecursiveResultHandler rrh = new RecursiveResultHandler(
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
 * @version  $Revision$ $Date$
 */
public class RecursiveResultHandler extends CopyLdapResultHandler
  implements ExtendedLdapResultHandler
{

  /** Ldap connection to use for searching. */
  private LdapConnection ldapConnection;

  /** Attribute to recursively search on. */
  private String searchAttribute;

  /** Attribute(s) to merge. */
  private String[] mergeAttributes;

  /** Attributes to return when searching, mergeAttributes + searchAttribute. */
  private String[] retAttrs;


  /** Default constructor. */
  public RecursiveResultHandler() {}


  /**
   * Creates a new recursive attribute handler.
   *
   * @param  searchAttr  attribute to search on
   * @param  mergeAttrs  attribute names to merge
   */
  public RecursiveResultHandler(
    final String searchAttr, final String[] mergeAttrs)
  {
    this(null, searchAttr, mergeAttrs);
  }


  /**
   * Creates a new recursive attribute handler.
   *
   * @param  lc  ldap connection
   * @param  searchAttr  attribute to search on
   * @param  mergeAttrs  attribute names to merge
   */
  public RecursiveResultHandler(
    final LdapConnection lc,
    final String searchAttr,
    final String[] mergeAttrs)
  {
    ldapConnection = lc;
    searchAttribute = searchAttr;
    mergeAttributes = mergeAttrs;
    initalizeReturnAttributes();
  }


  /** {@inheritDoc} */
  @Override
  public LdapConnection getResultLdapConnection()
  {
    return ldapConnection;
  }


  /** {@inheritDoc} */
  @Override
  public void setResultLdapConnection(final LdapConnection lc)
  {
    ldapConnection = lc;
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
      System.arraycopy(
        mergeAttributes,
        0,
        retAttrs,
        0,
        mergeAttributes.length);
      retAttrs[retAttrs.length - 1] = searchAttribute;
    }
  }


  /** {@inheritDoc} */
  @Override
  public void process(final SearchCriteria sc, final LdapResult lr)
    throws LdapException
  {
    // Recursively searches a list of attributes and merges those results with
    // the existing search result set.
    for (LdapEntry le : lr.getEntries()) {
      final List<String> searchedDns = new ArrayList<String>();
      if (le.getLdapAttributes().getAttribute(searchAttribute) != null) {
        searchedDns.add(le.getDn());
        readSearchAttribute(le.getLdapAttributes(), searchedDns);
      } else {
        recursiveSearch(le.getDn(), le.getLdapAttributes(), searchedDns);
      }
    }
  }


  /**
   * Reads the values of {@link #searchAttribute} from the supplied attributes
   * and calls {@link #recursiveSearch} for each.
   *
   * @param  attrs  to read
   * @param  searchedDns  list of DNs whose attributes have been read
   *
   * @throws  LdapException  if a search error occurs
   */
  private void readSearchAttribute(
    final LdapAttributes attrs,
    final List<String> searchedDns)
    throws LdapException
  {
    if (attrs != null) {
      final LdapAttribute attr = attrs.getAttribute(searchAttribute);
      if (attr != null) {
        for (Object rawValue : attr.getValues()) {
          if (rawValue instanceof String) {
            recursiveSearch((String) rawValue, attrs, searchedDns);
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
   * @throws  LdapException  if a search error occurs
   */
  private void recursiveSearch(
    final String dn,
    final LdapAttributes attrs,
    final List<String> searchedDns)
    throws LdapException
  {
    if (!searchedDns.contains(dn)) {

      LdapAttributes newAttrs = null;
      try {
        final SearchOperation search = new SearchOperation(ldapConnection);
        final SearchRequest sr = SearchRequest.newObjectScopeSearchRequest(
          dn, retAttrs);
        final LdapResult result = search.execute(sr).getResult();
        newAttrs = result.getEntry(dn).getLdapAttributes();
      } catch (LdapException e) {
        logger.warn(
          "Error retreiving attribute(s): {}",
          Arrays.toString(retAttrs),
          e);
      }
      searchedDns.add(dn);

      if (newAttrs != null) {
        // recursively search new attributes
        readSearchAttribute(newAttrs, searchedDns);

        // merge new attribute values
        for (String s : mergeAttributes) {
          final LdapAttribute newAttr = newAttrs.getAttribute(s);
          if (newAttr != null) {
            final LdapAttribute oldAttr = attrs.getAttribute(s);
            if (oldAttr == null) {
              attrs.addAttribute(newAttr);
            } else {
              for (Object o : newAttr.getValues()) {
                oldAttr.getValues().add(o);
              }
            }
          }
        }
      }
    }
  }
}
