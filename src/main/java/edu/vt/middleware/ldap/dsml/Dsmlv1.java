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
package edu.vt.middleware.ldap.dsml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.bean.LdapAttribute;
import edu.vt.middleware.ldap.bean.LdapEntry;
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

  /** serial version uid. */
  private static final long serialVersionUID = 1047858330816575821L;


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
          final SearchResult sr = results.next();
          final Element entryElement = this.createDsmlEntry(
            new QName("entry", ns),
            sr,
            ns);
          entriesElement.add(entryElement);
        }
      } catch (NamingException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("Error creating Element from SearchResult", e);
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
    final List<?> attrValues,
    final Namespace ns)
  {
    Element attrElement = DocumentHelper.createElement("");

    if (attrName != null) {
      if (attrName.equalsIgnoreCase("objectclass")) {

        attrElement.setQName(new QName("objectclass", ns));
        if (attrValues != null) {
          final Iterator<?> i = attrValues.iterator();
          while (i.hasNext()) {
            final Object rawValue = i.next();
            String value = null;
            boolean isBase64 = false;
            if (rawValue instanceof String) {
              value = (String) rawValue;
            } else if (rawValue instanceof byte[]) {
              value = LdapUtil.base64Encode((byte[]) rawValue);
              isBase64 = true;
            } else {
              if (this.logger.isWarnEnabled()) {
                this.logger.warn(
                  "Could not cast attribute value as a byte[]" +
                  " or a String");
              }
            }
            if (value != null) {
              final Element ocValueElement = attrElement.addElement(
                new QName("oc-value", ns));
              ocValueElement.addText(value);
              if (isBase64) {
                ocValueElement.addAttribute("encoding", "base64");
              }
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
      final Iterator<?> entryIterator = doc.selectNodes(
        "/dsml:dsml/dsml:directory-entries/dsml:entry").iterator();
      while (entryIterator.hasNext()) {
        final LdapEntry result = this.createSearchResult(
          (Element) entryIterator.next());
        if (result != null) {
          results.add(result.toSearchResult());
        }
      }
    }

    return results.iterator();
  }


  /**
   * This will take a DSML <code>Element</code> containing an entry of type
   * <dsml:entry name="name"/> and convert it to an LDAP entry.
   *
   * @param  entryElement  <code>Element</code> of DSML content
   *
   * @return  <code>LdapEntry</code>
   */
  protected LdapEntry createSearchResult(final Element entryElement)
  {
    final LdapEntry ldapEntry = new LdapEntry();
    ldapEntry.setDn("");

    if (entryElement != null) {

      final String name = entryElement.attributeValue("dn");
      if (name != null) {
        ldapEntry.setDn(name);
      }

      if (entryElement.hasContent()) {

        final Iterator<?> ocIterator = entryElement.elementIterator(
          "objectclass");
        while (ocIterator.hasNext()) {
          final Element ocElement = (Element) ocIterator.next();
          if (ocElement != null && ocElement.hasContent()) {
            final String ocName = "objectClass";
            final LdapAttribute ldapAttribute = new LdapAttribute(ocName);
            final Iterator<?> valueIterator = ocElement.elementIterator(
              "oc-value");
            while (valueIterator.hasNext()) {
              final Element valueElement = (Element) valueIterator.next();
              if (valueElement != null) {
                final String value = valueElement.getText();
                if (value != null) {
                  final String encoding = valueElement.attributeValue(
                    "encoding");
                  if (encoding != null && encoding.equals("base64")) {
                    ldapAttribute.getValues().add(LdapUtil.base64Decode(value));
                  } else {
                    ldapAttribute.getValues().add(value);
                  }
                }
              }
            }
            ldapEntry.getLdapAttributes().addAttribute(ldapAttribute);
          }
        }

        ldapEntry.getLdapAttributes().addAttributes(
          super.createSearchResult(entryElement).getLdapAttributes()
              .getAttributes());
      }
    }

    return ldapEntry;
  }
}
