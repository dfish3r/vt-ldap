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
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.bean.LdapAttribute;
import edu.vt.middleware.ldap.bean.LdapAttributes;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
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
    Document dsml = null;
    try {
      final LdapResult lr = this.beanFactory.newLdapResult();
      lr.addEntries(results);
      dsml = this.createDsml(lr);
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Error creating Element from SearchResult", e);
      }
    }
    return dsml;
  }


  /**
   * This will take the results of a prior LDAP query and convert it to a DSML
   * <code>Document</code>.
   *
   * @param  result  <code>LdapResult</code>
   *
   * @return  <code>Document</code>
   */
  public Document createDsml(final LdapResult result)
  {
    final Namespace ns = new Namespace("dsml", "http://www.dsml.org/DSML");
    final Document doc = DocumentHelper.createDocument();
    final Element dsmlElement = doc.addElement(new QName("dsml", ns));
    final Element entriesElement = dsmlElement.addElement(
      new QName("directory-entries", ns));

    // build document object from result
    if (result != null) {
      for (LdapEntry le : result.getEntries()) {
        final Element entryElement = this.createDsmlEntry(
          new QName("entry", ns),
          le,
          ns);
        entriesElement.add(entryElement);
      }
    }

    return doc;
  }


  /** {@inheritDoc} */
  protected List<Element> createDsmlAttributes(
    final LdapAttributes ldapAttributes, final Namespace ns)
  {
    final List<Element> attrElements = new ArrayList<Element>();
    for (LdapAttribute attr : ldapAttributes.getAttributes()) {
      final String attrName = attr.getName();
      final Set<?> attrValues = attr.getValues();
      Element attrElement = null;
      if (attrName.equalsIgnoreCase("objectclass")) {
        attrElement = createDsmlAttribute(
          attrName,
          attrValues,
          ns,
          "objectclass",
          null,
          "oc-value");
        if (attrElement.hasContent()) {
          attrElements.add(0, attrElement);
        }
      } else {
        attrElement = createDsmlAttribute(
          attrName,
          attrValues,
          ns,
          "attr",
          "name",
          "value");
        if (attrElement.hasContent()) {
          attrElements.add(attrElement);
        }
      }
    }
    return attrElements;
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
    return this.createLdapResult(doc).toSearchResults().iterator();
  }


  /**
   * This will take a DSML <code>Document</code> and convert it to an <code>
   * LdapResult</code>.
   *
   * @param  doc  <code>Document</code> of DSML
   *
   * @return  <code>LdapResult</code>
   */
  public LdapResult createLdapResult(final Document doc)
  {
    final LdapResult result = this.beanFactory.newLdapResult();

    if (doc != null && doc.hasContent()) {
      final Iterator<?> entryIterator = doc.selectNodes(
        "/dsml:dsml/dsml:directory-entries/dsml:entry").iterator();
      while (entryIterator.hasNext()) {
        final LdapEntry le = this.createLdapEntry(
          (Element) entryIterator.next());
        if (result != null) {
          result.addEntry(le);
        }
      }
    }

    return result;
  }


  /**
   * This will take a DSML <code>Element</code> containing an entry of type
   * <dsml:entry name="name"/> and convert it to an LDAP entry.
   *
   * @param  entryElement  <code>Element</code> of DSML content
   *
   * @return  <code>LdapEntry</code>
   */
  protected LdapEntry createLdapEntry(final Element entryElement)
  {
    final LdapEntry ldapEntry = this.beanFactory.newLdapEntry();
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
            final LdapAttribute ldapAttribute =
              this.beanFactory.newLdapAttribute();
            ldapAttribute.setName(ocName);
            final Iterator<?> valueIterator = ocElement.elementIterator(
              "oc-value");
            while (valueIterator.hasNext()) {
              final Element valueElement = (Element) valueIterator.next();
              if (valueElement != null) {
                final String value = valueElement.getText();
                if (value != null) {
                  final String encoding = valueElement.attributeValue(
                    "encoding");
                  if (encoding != null && "base64".equals(encoding)) {
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
          super.createLdapEntry(entryElement).getLdapAttributes()
              .getAttributes());
      }
    }

    return ldapEntry;
  }
}
