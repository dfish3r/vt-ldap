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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.bean.LdapAttribute;
import edu.vt.middleware.ldap.bean.LdapEntry;
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
  private static final long serialVersionUID = 5951425968736507129L;

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(AbstractDsml.class);


  /**
   * This will take the results of a prior LDAP query and convert it to a DSML
   * <code>Document</code>.
   *
   * @param  results  <code>Iterator</code> of LDAP search results
   *
   * @return  <code>Document</code>
   */
  public abstract Document createDsml(final Iterator<SearchResult> results);


  /**
   * This will take the result of a prior LDAP query and convert it to a DSML
   * <code>Document</code>.
   *
   * @param  result  <code>SearchResult</code> to convert
   *
   * @return  <code>Document</code>
   */
  public Document createDsml(final SearchResult result)
  {
    final List<SearchResult> l = new ArrayList<SearchResult>();
    l.add(result);
    return this.createDsml(l.iterator());
  }


  /**
   * This will take a LDAP search result and convert it to a DSML entry element.
   *
   * @param  entryName  <code>QName</code> name of element to create
   * @param  result  <code>SearchResult</code> to convert
   * @param  ns  <code>Namespace</code> of DSML
   *
   * @return  <code>Document</code>
   *
   * @throws  NamingException  if an error occurs while reading the search
   * result
   */
  protected Element createDsmlEntry(
    final QName entryName,
    final SearchResult result,
    final Namespace ns)
    throws NamingException
  {
    // create Element to hold result content
    final Element entryElement = DocumentHelper.createElement(entryName);

    if (result != null) {

      final LdapEntry entry = new LdapEntry(result);
      final String dn = entry.getDn();
      if (dn != null) {
        entryElement.addAttribute("dn", dn);
      }

      for (LdapAttribute attr : entry.getLdapAttributes().getAttributes()) {
        final String attrName = attr.getName();
        final List<?> attrValues = attr.getValues();
        final Element attrElement = createDsmlAttribute(
          attrName,
          attrValues,
          ns);
        if (attrElement.hasContent()) {
          entryElement.add(attrElement);
        }
      }
    }

    return entryElement;
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
    final Element attrElement = DocumentHelper.createElement("");

    if (attrName != null) {

      attrElement.setQName(new QName("attr", ns));
      attrElement.addAttribute("name", attrName);
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
            if (LOG.isWarnEnabled()) {
              LOG.warn(
                "Could not cast attribute value as a byte[]" +
                " or a String");
            }
          }
          if (value != null) {
            final Element valueElement = attrElement.addElement(
              new QName("value", ns));
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
   * This will write the supplied LDAP search results to the supplied output
   * stream in the form of DSML.
   *
   * @param  results  <code>Iterator</code> of LDAP search results
   * @param  out  <code>OutputStream</code> to write to
   *
   * @throws  IOException  if an error occurs while writing to the output stream
   */
  public void outputDsml(
    final Iterator<SearchResult> results,
    final OutputStream out)
    throws IOException
  {
    output(createDsml(results), out);
  }


  /**
   * This will write the supplied LDAP search result to the supplied output
   * stream in the form of DSML.
   *
   * @param  result  <code>SearchResult</code> to convert
   * @param  out  <code>OutputStream</code> to write to
   *
   * @throws  IOException  if an error occurs while writing to the output stream
   */
  public void outputDsml(final SearchResult result, final OutputStream out)
    throws IOException
  {
    output(createDsml(result), out);
  }


  /**
   * This will write the supplied document to the supplied output stream.
   *
   * @param  doc  <code>Document</code> to write
   * @param  out  <code>OutputStream</code> to write to
   *
   * @throws  IOException  if an error occurs while writing to the output stream
   */
  protected void output(final Document doc, final OutputStream out)
    throws IOException
  {
    if (doc != null && out != null) {
      final XMLWriter writer = new XMLWriter(
        out,
        OutputFormat.createPrettyPrint());
      writer.write(doc);
    }
  }


  /**
   * This will convert the supplied LDAP search results to a string in the form
   * of DSML.
   *
   * @param  results  <code>Iterator</code> of LDAP search results
   *
   * @return  <code>String</code> of DSML
   */
  public String outputDsmlToString(final Iterator<SearchResult> results)
  {
    return outputToString(createDsml(results));
  }


  /**
   * This will convert the supplied document to a string.
   *
   * @param  doc  <code>Document</code> to convert
   *
   * @return  <code>String</code> of document contents
   */
  protected String outputToString(final Document doc)
  {
    final StringWriter out = new StringWriter();
    if (doc != null) {
      final XMLWriter writer = new XMLWriter(
        out,
        OutputFormat.createPrettyPrint());
      try {
        writer.write(doc);
      } catch (IOException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Could not write XML to StringWriter", e);
        }
      }
    }
    return out.toString();
  }


  /**
   * This will take a Reader containing a DSML <code>Document</code> and convert
   * it to an Iterator of LDAP search results.
   *
   * @param  reader  <code>Reader</code> containing DSML content
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  DocumentException  if an error occurs building a document from the
   * reader
   * @throws  IOException  if an I/O error occurs
   */
  public Iterator<SearchResult> createSearchResults(final Reader reader)
    throws DocumentException, IOException
  {
    final Document dsml = new SAXReader().read(reader);
    return createSearchResults(dsml);
  }


  /**
   * This will take a DSML <code>Document</code> and convert it to an Iterator
   * of LDAP search results.
   *
   * @param  doc  <code>Document</code> of DSML
   *
   * @return  <code>Iterator</code> - of LDAP search results
   */
  public abstract Iterator<SearchResult> createSearchResults(
    final Document doc);


  /**
   * This will take a DSML <code>Element</code> containing an entry of type
   * <entry/> and convert it to a LDAP search result.
   *
   * @param  entryElement  <code>Element</code> of DSML content
   *
   * @return  <code>SearchResult</code>
   */
  protected SearchResult createSearchResult(final Element entryElement)
  {
    String name = "";
    final Attributes entryAttributes = new BasicAttributes(true);

    if (entryElement != null) {

      name = entryElement.attributeValue("dn");
      if (name == null) {
        name = "";
      }

      if (entryElement.hasContent()) {

        // load the attr elements
        final Iterator<?> attrIterator = entryElement.elementIterator("attr");
        while (attrIterator.hasNext()) {
          final Element attrElement = (Element) attrIterator.next();
          if (attrElement != null) {
            final String attrName = attrElement.attributeValue("name");
            if (attrName != null && attrElement.hasContent()) {
              final Attribute entryAttribute = new BasicAttribute(attrName);
              final Iterator<?> valueIterator = attrElement.elementIterator(
                "value");
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
        }
      }
    }

    return new SearchResult(name, null, entryAttributes);
  }
}
