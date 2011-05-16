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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.LdapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Writes DSML version 1 to a {@link Writer} using an {@link LdapResult}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class Dsmlv1Writer
{

  /** Document builder factory. */
  protected static final DocumentBuilderFactory DOC_BUILDER_FACTORY =
    DocumentBuilderFactory.newInstance();

  /** Transformer factory. */
  protected static final TransformerFactory TRANSFORMER_FACTORY =
    TransformerFactory.newInstance();

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Writer to write to. */
  protected final Writer dsmlWriter;


  /** Initialize the document builder factory. */
  static {
    DOC_BUILDER_FACTORY.setNamespaceAware(true);
  }


  /**
   * Creates a new dsml writer.
   *
   * @param  writer  to write DSML to
   */
  public Dsmlv1Writer(final Writer writer)
  {
    dsmlWriter = writer;
  }


  /**
   * Writes the supplied ldap result to the writer.
   *
   * @param  result  ldap result to write
   * @throws  IOException  if an error occurs using the writer
   */
  public void write(final LdapResult result)
    throws IOException
  {
    try {
      final Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(
        "{http://xml.apache.org/xslt}indent-amount", "2");

      final StreamResult sr = new StreamResult(dsmlWriter);
      final DOMSource source = new DOMSource(createDsml(result));
      transformer.transform(source, sr);
      dsmlWriter.flush();
    } catch (ParserConfigurationException e) {
      throw new IOException(e);
    } catch (TransformerException e) {
      throw new IOException(e);
    }
  }


  /**
   * Creates DSML that corresponds to the supplied ldap result.
   *
   * @param  lr  ldap result to parse
   *
   * @throws  ParserConfigurationException  if a document builder cannot be
   * created
   *
   * @return  DSML
   */
  protected Document createDsml(final LdapResult lr)
    throws ParserConfigurationException
  {
    final DocumentBuilder db = DOC_BUILDER_FACTORY.newDocumentBuilder();
    final DOMImplementation domImpl = db.getDOMImplementation();
    final Document doc = domImpl.createDocument(
      "http://www.dsml.org/DSML", "dsml:dsml", null);
    doc.setXmlStandalone(true);

    final Element entriesElement = doc.createElement("dsml:directory-entries");
    doc.getDocumentElement().appendChild(entriesElement);

    // build document object from result
    if (lr != null) {
      for (LdapEntry le : lr.getEntries()) {
        final Element entryElement = doc.createElement("dsml:entry");
        if (le.getDn() != null) {
          entryElement.setAttribute("dn", le.getDn());
        }
        for (Element e : createDsmlAttributes(doc, le.getAttributes())) {
          entryElement.appendChild(e);
        }
        entriesElement.appendChild(entryElement);
      }
    }

    return doc;
  }


  /**
   * Returns a list of <dsml:attr/> elements for the supplied attributes.
   *
   * @param  doc  to source elements from
   * @param  ldapAttributes  to iterate over
   * @return  list of elements contains attributes
   */
  protected List<Element> createDsmlAttributes(
    final Document doc, final Collection<LdapAttribute> ldapAttributes)
  {
    final List<Element> attrElements = new ArrayList<Element>();
    for (LdapAttribute attr : ldapAttributes) {
      final String attrName = attr.getName();
      final Set<?> attrValues = attr.getValues();
      Element attrElement = null;
      if (attrName.equalsIgnoreCase("objectclass")) {
        attrElement = createObjectclassElement(doc, attrValues);
        if (attrElement.hasChildNodes()) {
          attrElements.add(0, attrElement);
        }
      } else {
        attrElement = createAttrElement(doc, attrName, attrValues);
        if (attrElement.hasChildNodes()) {
          attrElements.add(attrElement);
        }
      }
    }
    return attrElements;
  }


  /**
   * Returns a <dsml:attr/> element for the supplied name and values.
   *
   * @param  doc  to source elements from
   * @param  name  of the attribute to add
   * @param  values  of the attribute to add
   * @return  element containing the attribute
   */
  protected Element createAttrElement(
    final Document doc, final String name, final Set<?> values)
  {
    final Element attrElement = doc.createElement("dsml:attr");
    attrElement.setAttribute("name", name);
    for (Object o : values) {
      final Element valueElement = doc.createElement("dsml:value");
      attrElement.appendChild(valueElement);
      setAttrValue(doc, valueElement, o);
    }
    return attrElement;
  }


  /**
   * Returns a <dsml:objectclass/> element for the supplied values.
   *
   * @param  doc  to source elements from
   * @param  values  of the attribute to add
   * @return  element containing the attribute values
   */
  protected Element createObjectclassElement(
    final Document doc, final Set<?> values)
  {
    final Element ocElement = doc.createElement("dsml:objectclass");
    for (Object o : values) {
      final Element ocValueElement = doc.createElement("dsml:oc-value");
      ocElement.appendChild(ocValueElement);
      setAttrValue(doc, ocValueElement, o);
    }
    return ocElement;
  }


  /**
   * Adds the supplied object to the value element taking into account whether
   * the object needs to be base64 encoded.
   *
   * @param  doc  to create nodes with
   * @param  valueElement  to append value to
   * @param  o  to create node for
   */
  protected void setAttrValue(
    final Document doc, final Element valueElement, final Object o)
  {
    String value = null;
    boolean isBase64 = false;
    if (o instanceof String) {
      value = (String) o;
    } else if (o instanceof byte[]) {
      value = LdapUtil.base64Encode((byte[]) o);
      isBase64 = true;
    } else {
      logger.warn(
        "Could not cast attribute value as a byte[] or a String");
    }
    if (value != null) {
      valueElement.appendChild(doc.createTextNode(value));
      if (isBase64) {
        valueElement.setAttribute("encoding", "base64");
      }
    }
  }
}
