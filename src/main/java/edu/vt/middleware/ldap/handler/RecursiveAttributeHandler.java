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
import java.util.List;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Recursively searches for attributes of the same name and combine them into
 * one attribute. Attribute values must represent DNs in the LDAP.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RecursiveAttributeHandler extends CopyLdapAttributeHandler
  implements ExtendedLdapAttributeHandler
{

  /** Ldap connection to use for searching. */
  private LdapConnection ldapConnection;

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
   * @param  lc  ldap connection
   * @param  name  of the attribute
   */
  public RecursiveAttributeHandler(final LdapConnection lc, final String name)
  {
    ldapConnection = lc;
    attributeName = name;
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
  public void process(final SearchCriteria sc, final LdapAttribute attr)
    throws LdapException
  {
    if (attr != null) {
      attr.setName(processName(sc, attr.getName()));
      if (attr.getName().equals(attributeName)) {
        final List<Object> newValues =
          new ArrayList<Object>(attr.getValues().size());
        for (Object o : attr.getValues()) {
          final Object rawValue = processValue(sc, o);
          if (rawValue instanceof String) {
            final List<String> recursiveValues = recursiveSearch(
              (String) rawValue,
              new ArrayList<String>());
            for (String s : recursiveValues) {
              newValues.add(processValue(sc, s));
            }
          } else {
            newValues.add(rawValue);
          }
        }
        attr.getValues().clear();
        attr.getValues().addAll(newValues);
      }
    }
  }


  /**
   * Recursively gets the attribute {@link #attributeName} for the supplied dn.
   *
   * @param  dn  to get attribute for
   * @param  searchedDns  list of DNs that have been searched for
   *
   * @return  list of attribute values found by recursively searching
   *
   * @throws  LdapException  if a search error occurs
   */
  private List<String> recursiveSearch(
    final String dn,
    final List<String> searchedDns)
    throws LdapException
  {
    final List<String> results = new ArrayList<String>();
    if (!searchedDns.contains(dn)) {

      LdapEntry entry = null;
      try {
        final SearchOperation search = new SearchOperation(ldapConnection);
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
        if (attr != null) {
          for (Object rawValue : attr.getValues()) {
            if (rawValue instanceof String) {
              results.addAll(
                recursiveSearch((String) rawValue, searchedDns));
            }
          }
        }
      }
    }
    return results;
  }
}
