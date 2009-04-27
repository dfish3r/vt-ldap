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
package edu.vt.middleware.ldap.dsml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 * <code>Dsmlv1</code> contains functions for converting LDAP search result sets
 * into DSML version 1.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public final class Dsmlv1 extends AbstractDsml
{

  /** Log for this class */
  private static final Log LOG = LogFactory.getLog(Dsmlv1.class);


  /** Default constructor. */
  public Dsmlv1() {}


  /**
   * This will take the results of a prior LDAP query and convert it to a DSML
   * <code>Document</code>.
   *
   * @param  results  <code>Iterator</code> of LDAP search results
   *
   * @return  <code>Document</code>
   */
  public Document createDsml(final Iterator<SearchResult> results)
  {
    final Namespace ns = new Namespace("dsml", "http://www.dsml.org/DSML");
    final Document doc = DocumentHelper.createDocument();
    final Element dsmlElement = doc.addElement(new QName("dsml", ns));
    final Element entriesElement = dsmlElement.addElement(
      new QName("directory-entries", ns));

    // build document object from results
    if (results != null) {
      try {
        while (results.hasNext()) {
          try {
            final SearchResult sr = results.next();
            final Element entryElement = this.createDsmlEntry(
              new QName("entry", ns),
              sr,
              ns);
            entriesElement.add(entryElement);
          } catch (ClassCastException e) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Could not cast item in Iterator as a SearchResult");
            }
          }
        }
      } catch (NamingException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error creating Element from SearchResult", e);
        }
      }
    }

    return doc;
  }


  /**
   * This will take an attribute name and it's values and return a DSML
   * attribute element.
   *
   * @param  attrName  <code>String</code>
   * @param  attrValues  <code>List</code>
   * @param  ns  <code>Namespace</code> of DSML
   *
   * @return  <code>Element</code>
   */
  protected Element createDsmlAttribute(
    final String attrName,
    final List attrValues,
    final Namespace ns)
  {
    Element attrElement = DocumentHelper.createElement("");

    if (attrName != null) {
      if (attrName.equalsIgnoreCase("objectclass")) {

        attrElement.setQName(new QName("objectclass", ns));
        if (attrValues != null) {
          final Iterator i = attrValues.iterator();
          while (i.hasNext()) {
            final String value = (String) i.next();
            if (value != null) {
              final Element ocValueElement = attrElement.addElement(
                new QName("oc-value", ns));
              ocValueElement.addText(value);
            }
          }
        }
      } else {
        attrElement = super.createDsmlAttribute(attrName, attrValues, ns);
      }
    }

    return attrElement;
  }


  /**
   * This will take a DSML <code>Document</code> and convert it to an Iterator
   * of LDAP search results.
   *
   * @param  doc  <code>Document</code> of DSML
   *
   * @return  <code>Iterator</code> - of LDAP search results
   */
  public Iterator<SearchResult> createSearchResults(final Document doc)
  {
    final List<SearchResult> results = new ArrayList<SearchResult>();

    if (doc != null && doc.hasContent()) {
      final Iterator entryIterator = doc.selectNodes(
        "/dsml:dsml/dsml:directory-entries/dsml:entry").iterator();
      while (entryIterator.hasNext()) {
        final SearchResult result = this.createSearchResult(
          (Element) entryIterator.next());
        if (result != null) {
          results.add(result);
        }
      }
    }

    return results.iterator();
  }


  /**
   * This will take a DSML <code>Element</code> containing an entry of type
   * <dsml:entry name="name"/> and convert it to a LDAP search result.
   *
   * @param  entryElement  <code>Element</code> of DSML content
   *
   * @return  <code>SearchResult</code>
   */
  protected SearchResult createSearchResult(final Element entryElement)
  {
    String name = "";
    final Attributes entryAttributes = new BasicAttributes(true);
    SearchResult attrResults = null;

    if (entryElement != null) {

      name = entryElement.attributeValue("dn");
      if (name == null) {
        name = "";
      }

      if (entryElement.hasContent()) {

        final Iterator ocIterator = entryElement.elementIterator("objectclass");
        while (ocIterator.hasNext()) {
          final Element ocElement = (Element) ocIterator.next();
          if (ocElement != null && ocElement.hasContent()) {
            final String ocName = "objectClass";
            final Attribute entryAttribute = new BasicAttribute(ocName);
            final Iterator valueIterator = ocElement.elementIterator(
              "oc-value");
            while (valueIterator.hasNext()) {
              final Element valueElement = (Element) valueIterator.next();
              if (valueElement != null) {
                final String value = valueElement.getText();
                if (value != null) {
                  entryAttribute.add(value);
                }
              }
            }
            entryAttributes.put(entryAttribute);
          }
        }

        attrResults = super.createSearchResult(entryElement);
      }
    }

    if (attrResults != null) {
      final Attributes attrs = attrResults.getAttributes();
      if (attrs != null) {
        final NamingEnumeration<? extends Attribute> ae = attrs.getAll();
        if (ae != null) {
          try {
            while (ae.hasMore()) {
              entryAttributes.put(ae.next());
            }
          } catch (NamingException e) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Could not read attribute in SearchResult from parent");
            }
          }
        }
      }
    }
    return new SearchResult(name, null, entryAttributes);
  }
}
