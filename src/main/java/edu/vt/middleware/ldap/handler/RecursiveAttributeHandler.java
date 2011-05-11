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
import edu.vt.middleware.ldap.LdapAttributes;
import edu.vt.middleware.ldap.LdapConnection;
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
    this.ldapConnection = lc;
    this.attributeName = name;
  }


  /** {@inheritDoc} */
  public LdapConnection getResultLdapConnection()
  {
    return this.ldapConnection;
  }


  /** {@inheritDoc} */
  public void setResultLdapConnection(final LdapConnection lc)
  {
    this.ldapConnection = lc;
  }


  /**
   * Returns the attribute name that will be recursively searched on.
   *
   * @return  attribute name
   */
  public String getAttributeName()
  {
    return this.attributeName;
  }


  /**
   * Sets the attribute name that will be recursively searched on.
   *
   * @param  name  of the attribute
   */
  public void setAttributeName(final String name)
  {
    this.attributeName = name;
  }


  /** {@inheritDoc} */
  public void process(final SearchCriteria sc, final LdapAttribute attr)
    throws LdapException
  {
    if (attr != null) {
      attr.setName(this.processName(sc, attr.getName()));
      if (attr.getName().equals(this.attributeName)) {
        final List<Object> newValues =
          new ArrayList<Object>(attr.getValues().size());
        for (Object o : attr.getValues()) {
          final Object rawValue = this.processValue(sc, o);
          if (rawValue instanceof String) {
            final List<String> recursiveValues = this.recursiveSearch(
              (String) rawValue,
              new ArrayList<String>());
            for (String s : recursiveValues) {
              newValues.add(this.processValue(sc, s));
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

      LdapAttributes attrs = null;
      try {
        final SearchOperation search = new SearchOperation(this.ldapConnection);
        final LdapResult result = search.execute(
          SearchRequest.newObjectScopeSearchRequest(
            dn, new String[] {this.attributeName})).getResult();
        attrs = result.getEntry(dn).getLdapAttributes();
        results.add(dn);
      } catch (LdapException e) {
        this.logger.warn(
          "Error retreiving attribute: {}", this.attributeName, e);
      }
      searchedDns.add(dn);
      if (attrs != null) {
        final LdapAttribute attr = attrs.getAttribute(this.attributeName);
        if (attr != null) {
          for (Object rawValue : attr.getValues()) {
            if (rawValue instanceof String) {
              results.addAll(
                this.recursiveSearch((String) rawValue, searchedDns));
            }
          }
        }
      }
    }
    return results;
  }
}
