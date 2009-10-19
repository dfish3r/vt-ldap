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
package edu.vt.middleware.ldap.handler;

import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import edu.vt.middleware.ldap.Ldap;

/**
 * <code>RecursiveAttributeHandler</code> will recursively search for
 * attributes of the same name and combine them into one attribute.
 * Attribute values must represent DNs in the LDAP.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RecursiveAttributeHandler extends CopyAttributeHandler
{
  /** Ldap to use for searching. */
  private Ldap ldap;

  /** Attribute name to search for. */
  private String attributeName;


  /**
   * Creates a new <code>RecursiveAttributeHandler</code> with the supplied
   * ldap and attribute name.
   *
   * @param  ldap  <code>Ldap</code>
   * @param  attrName  <code>String</code>
   */
  public RecursiveAttributeHandler(final Ldap ldap, final String attrName)
  {
    this.ldap = ldap;
    this.attributeName = attrName;
  }


  /** {@inheritDoc}. */
  protected Attribute processResult(
    final SearchCriteria sc, final Attribute attr)
    throws NamingException
  {
    Attribute newAttr = null;
    if (attr != null) {
      newAttr = new BasicAttribute(attr.getID(), attr.isOrdered());
      if (attr.getID().equals(this.attributeName)) {
        final NamingEnumeration<?> en = attr.getAll();
        while (en.hasMore()) {
          final Object rawValue = this.processValue(sc, en.next());
          if (rawValue instanceof String) {
            final List<String> recursiveValues = this.recursiveSearch(
              (String) rawValue, new ArrayList<String>());
            for (String s : recursiveValues) {
              newAttr.add(this.processValue(sc, s));
            }
          } else {
            newAttr.add(rawValue);
          }
        }
      } else {
        final NamingEnumeration<?> en = attr.getAll();
        while (en.hasMore()) {
          newAttr.add(this.processValue(sc, en.next()));
        }
      }
    }
    return newAttr;
  }


  /**
   * Recursively gets the attribute {@link #attributeName} for the supplied dn.
   *
   * @param  dn  to get attribute for
   * @param  searchedDns  list of DNs that have been searched for
   * @return  list of attribute values found by recursively searching
   * @throws NamingException if a search error occurs
   */
  private List<String> recursiveSearch(
    final String dn, final List<String> searchedDns)
    throws NamingException
  {
    final List<String> results = new ArrayList<String>();
    if (!searchedDns.contains(dn)) {
      results.add(dn);
      final Attributes attrs = this.ldap.getAttributes(
        dn, new String[]{this.attributeName});
      searchedDns.add(dn);
      if (attrs != null) {
        final Attribute attr = attrs.get(this.attributeName);
        if (attr != null) {
          final NamingEnumeration<?> en = attr.getAll();
          while (en.hasMore()) {
            final Object rawValue = en.next();
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