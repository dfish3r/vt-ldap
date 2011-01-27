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
package edu.vt.middleware.ldap.dsml;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapAttributes;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.SortBehavior;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * <code>AbstractDsml</code> contains functions for converting LDAP search
 * result sets into DSML.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractDsml implements Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -5626050181955100494L;

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Ldap result sort behavior. */
  protected SortBehavior sortBehavior = SortBehavior.getDefaultSortBehavior();


  /**
   * Returns the sort behavior for ldap results.
   *
   * @return  <code>SortBehavior</code>
   */
  public SortBehavior getSortBehavior()
  {
    return this.sortBehavior;
  }


  /**
   * Sets the sort behavior for ldap results.
   *
   * @param  sb  <code>SortBehavior</code>
   */
  public void setSortBehavior(final SortBehavior sb)
  {
    this.sortBehavior = sb;
  }


  /**
   * This will take the results of a prior LDAP query and convert it to a DSML
   * <code>Document</code>.
   *
   * @param  result  <code>LdapResult</code>
   *
   * @return  <code>Document</code>
   */
  public abstract Document createDsml(final LdapResult result);


  /**
   * This will take an LDAP search result and convert it to a DSML entry
   * element.
   *
   * @param  entryName  <code>QName</code> name of element to create
   * @param  ldapEntry  <code>LdapEntry</code> to convert
   * @param  ns  <code>Namespace</code> of DSML
   *
   * @return  <code>Document</code>
   */
  protected Element createDsmlEntry(
    final QName entryName,
    final LdapEntry ldapEntry,
    final Namespace ns)
  {
    // create Element to hold result content
    final Element entryElement = DocumentHelper.createElement(entryName);

    if (ldapEntry != null) {

      final String dn = ldapEntry.getDn();
      if (dn != null) {
        entryElement.addAttribute("dn", dn);
      }

      for (Element e :
           createDsmlAttributes(ldapEntry.getLdapAttributes(), ns)) {
        entryElement.add(e);
      }
    }

    return entryElement;
  }


  /**
   * This will return a list of DSML attribute elements from the supplied <code>
   * LdapAttributes</code>.
   *
   * @param  ldapAttributes  <code>LdapAttributes</code>
   * @param  ns  <code>Namespace</code> of DSML
   *
   * @return  <code>List</code> of elements
   */
  protected List<Element> createDsmlAttributes(
    final LdapAttributes ldapAttributes,
    final Namespace ns)
  {
    final List<Element> attrElements = new ArrayList<Element>();
    for (LdapAttribute attr : ldapAttributes.getAttributes()) {
      final String attrName = attr.getName();
      final Set<?> attrValues = attr.getValues();
      final Element attrElement = createDsmlAttribute(
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
    return attrElements;
  }


  /**
   * This will take an attribute name and it's values and return a DSML
   * attribute element.
   *
   * @param  attrName  <code>String</code>
   * @param  attrValues  <code>Set</code>
   * @param  ns  <code>Namespace</code> of DSML
   * @param  elementName  <code>String</code> of the attribute element
   * @param  elementAttrName  <code>String</code> of the attribute element
   * @param  elementValueName  <code>String</code> of the value element
   *
   * @return  <code>Element</code>
   */
  protected Element createDsmlAttribute(
    final String attrName,
    final Set<?> attrValues,
    final Namespace ns,
    final String elementName,
    final String elementAttrName,
    final String elementValueName)
  {
    final Element attrElement = DocumentHelper.createElement("");

    if (attrName != null) {

      attrElement.setQName(new QName(elementName, ns));
      if (elementAttrName != null) {
        attrElement.addAttribute(elementAttrName, attrName);
      }
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
            final Element valueElement = attrElement.addElement(
              new QName(elementValueName, ns));
            valueElement.addText(value);
            if (isBase64) {
              valueElement.addAttribute("encoding", "base64");
            }
          }
        }
      }
    }

    return attrElement;
  }


  /**
   * This will write the supplied LDAP result to the supplied writer in the form
   * of DSML.
   *
   * @param  result  <code>LdapResult</code>
   * @param  writer  <code>Writer</code> to write to
   *
   * @throws  IOException  if an error occurs while writing
   */
  public void outputDsml(final LdapResult result, final Writer writer)
    throws IOException
  {
    final XMLWriter xmlWriter = new XMLWriter(
      writer,
      OutputFormat.createPrettyPrint());
    xmlWriter.write(createDsml(result));
    writer.flush();
  }


  /**
   * This will take a Reader containing a DSML <code>Document</code> and convert
   * it to an <code>LdapResult</code>.
   *
   * @param  reader  <code>Reader</code> containing DSML content
   *
   * @return  <code>LdapResult</code>
   *
   * @throws  DocumentException  if an error occurs building a document from the
   * reader
   * @throws  IOException  if an I/O error occurs
   */
  public LdapResult importDsml(final Reader reader)
    throws DocumentException, IOException
  {
    final Document dsml = new SAXReader().read(reader);
    return createLdapResult(dsml);
  }


  /**
   * This will take a DSML <code>Document</code> and convert it to an Iterator
   * of LDAP search results.
   *
   * @param  doc  <code>Document</code> of DSML
   *
   * @return  <code>Iterator</code> - of LDAP search results
   */
  protected abstract LdapResult createLdapResult(final Document doc);


  /**
   * This will take a DSML <code>Element</code> containing an entry of type
   * <entry/> and convert it to an LDAP entry.
   *
   * @param  entryElement  <code>Element</code> of DSML content
   *
   * @return  <code>LdapEntry</code>
   */
  protected LdapEntry createLdapEntry(final Element entryElement)
  {
    final LdapEntry ldapEntry = new LdapEntry(this.sortBehavior);
    ldapEntry.setDn("");

    if (entryElement != null) {

      final String name = entryElement.attributeValue("dn");
      if (name != null) {
        ldapEntry.setDn(name);
      }

      if (entryElement.hasContent()) {

        // load the attribute elements
        final Iterator<?> attrIterator = entryElement.elementIterator("attr");
        while (attrIterator.hasNext()) {
          final Element attrElement = (Element) attrIterator.next();
          final String attrName = attrElement.attributeValue("name");
          if (attrName != null && attrElement.hasContent()) {
            final LdapAttribute ldapAttribute = new LdapAttribute(
              this.sortBehavior);
            ldapAttribute.setName(attrName);

            final Iterator<?> valueIterator = attrElement.elementIterator(
              "value");
            while (valueIterator.hasNext()) {
              final Element valueElement = (Element) valueIterator.next();
              final String value = valueElement.getText();
              if (value != null) {
                final String encoding = valueElement.attributeValue("encoding");
                if (encoding != null && "base64".equals(encoding)) {
                  ldapAttribute.getValues().add(LdapUtil.base64Decode(value));
                } else {
                  ldapAttribute.getValues().add(value);
                }
              }
            }
            ldapEntry.getLdapAttributes().addAttribute(ldapAttribute);
          }
        }
      }
    }

    return ldapEntry;
  }
}
