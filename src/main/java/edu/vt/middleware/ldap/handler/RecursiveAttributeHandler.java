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
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import edu.vt.middleware.ldap.Ldap;

/**
 * <code>RecursiveAttributeHandler</code> will recursively search for attributes
 * of the same name and combine them into one attribute. Attribute values must
 * represent DNs in the LDAP.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RecursiveAttributeHandler extends CopyAttributeHandler
  implements ExtendedAttributeHandler
{

  /** Ldap to use for searching. */
  private Ldap ldap;

  /** Attribute name to search for. */
  private String attributeName;


  /**
   * Creates a new <code>RecursiveAttributeHandler</code> with the supplied
   * attribute name.
   *
   * @param  attrName  <code>String</code>
   */
  public RecursiveAttributeHandler(final String attrName)
  {
    this(null, attrName);
  }


  /**
   * Creates a new <code>RecursiveAttributeHandler</code> with the supplied ldap
   * and attribute name.
   *
   * @param  l  <code>Ldap</code>
   * @param  attrName  <code>String</code>
   */
  public RecursiveAttributeHandler(final Ldap l, final String attrName)
  {
    this.ldap = l;
    this.attributeName = attrName;
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
  public String getAttributeName()
  {
    return this.attributeName;
  }


  /**
   * Sets the attribute name that will be recursively searched on.
   *
   * @param  s  <code>String</code>
   */
  public void setAttributeName(final String s)
  {
    this.attributeName = s;
  }


  /** {@inheritDoc} */
  protected Attribute processResult(
    final SearchCriteria sc,
    final Attribute attr)
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
              (String) rawValue,
              new ArrayList<String>());
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
   *
   * @return  list of attribute values found by recursively searching
   *
   * @throws  NamingException  if a search error occurs
   */
  private List<String> recursiveSearch(
    final String dn,
    final List<String> searchedDns)
    throws NamingException
  {
    final List<String> results = new ArrayList<String>();
    if (!searchedDns.contains(dn)) {

      Attributes attrs = null;
      try {
        attrs = this.ldap.getAttributes(dn, new String[] {this.attributeName});
        results.add(dn);
      } catch (NamingException e) {
        if (this.logger.isWarnEnabled()) {
          this.logger.warn(
            "Error retreiving attribute: " + this.attributeName,
            e);
        }
      }
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
