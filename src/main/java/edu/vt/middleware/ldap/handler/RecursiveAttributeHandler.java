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

import java.util.HashSet;
import java.util.Set;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Recursively searches for attributes of the same name and combines them into
 * one attribute. Attribute values must represent DNs in the LDAP.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RecursiveAttributeHandler extends CopyLdapAttributeHandler
  implements ExtendedLdapAttributeHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 853;

  /** Connection to use for searching. */
  private Connection connection;

  /** Attribute name to search for. */
  private String attributeName;


  /**
   * Creates a new recursive attribute handler.
   *
   * @param  name  of the attribute
   */
  public RecursiveAttributeHandler(final String name)
  {
    this(null, name);
  }


  /**
   * Creates a new recursive attribute handler.
   *
   * @param  c  connection
   * @param  name  of the attribute
   */
  public RecursiveAttributeHandler(final Connection c, final String name)
  {
    connection = c;
    attributeName = name;
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
  public String getAttributeName()
  {
    return attributeName;
  }


  /**
   * Sets the attribute name that will be recursively searched on.
   *
   * @param  name  of the attribute
   */
  public void setAttributeName(final String name)
  {
    attributeName = name;
  }


  /** {@inheritDoc} */
  @Override
  public void process(final SearchCriteria criteria, final LdapAttribute attr)
    throws LdapException
  {
    if (attr != null) {
      attr.setName(processName(criteria, attr.getName()));
      if (attr.getName().equals(attributeName)) {
        if (attr.isBinary()) {
          final Set<byte[]> newValues = new HashSet<byte[]>(attr.size());
          for (byte[] value : attr.getBinaryValues()) {
            newValues.add(processValue(criteria, value));
          }
          attr.clear();
          attr.addBinaryValues(newValues);
        } else {
          final Set<String> newValues = new HashSet<String>(attr.size());
          for (String value : attr.getStringValues()) {
            final Set<String> recursiveValues = recursiveSearch(
              processValue(criteria, value), new HashSet<String>());
            for (String s : recursiveValues) {
              newValues.add(processValue(criteria, s));
            }
          }
          attr.clear();
          attr.addStringValues(newValues);
        }
      }
    }
  }


  /**
   * Recursively gets the attribute {@link #attributeName} for the supplied dn.
   *
   * @param  dn  to get attribute for
   * @param  searchedDns  list of DNs that have been searched for
   *
   * @return  set of attribute values found by recursively searching
   *
   * @throws  LdapException  if a search error occurs
   */
  private Set<String> recursiveSearch(
    final String dn,
    final Set<String> searchedDns)
    throws LdapException
  {
    final Set<String> results = new HashSet<String>();
    if (!searchedDns.contains(dn)) {

      LdapEntry entry = null;
      try {
        final SearchOperation search = new SearchOperation(connection);
        final LdapResult result = search.execute(
          SearchRequest.newObjectScopeSearchRequest(
            dn, new String[] {attributeName})).getResult();
        entry = result.getEntry(dn);
        results.add(dn);
      } catch (LdapException e) {
        logger.warn(
          "Error retreiving attribute: {}", attributeName, e);
      }
      searchedDns.add(dn);
      if (entry != null) {
        final LdapAttribute attr = entry.getAttribute(attributeName);
        if (attr != null && !attr.isBinary()) {
          for (String s : attr.getStringValues()) {
            results.addAll(recursiveSearch(s, searchedDns));
          }
        }
      }
    }
    return results;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(HASH_CODE_SEED, attributeName);
  }
}
