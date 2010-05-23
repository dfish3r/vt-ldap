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

import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

/**
 * <code>Dsmlv2</code> contains functions for converting LDAP search result sets
 * into DSML version 2.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class Dsmlv2 extends AbstractDsml
{

  /** serial version uid. */
  private static final long serialVersionUID = -1503268164295032020L;


  /** Default constructor. */
  public Dsmlv2() {}


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
    final Namespace ns = new Namespace("", "urn:oasis:names:tc:DSML:2:0:core");
    final Document doc = DocumentHelper.createDocument();
    final Element dsmlElement = doc.addElement(new QName("batchResponse", ns));
    final Element entriesElement = dsmlElement.addElement(
      new QName("searchResponse", ns));

    // build document object from results
    if (result != null) {
      for (LdapEntry le : result.getEntries()) {
        final Element entryElement = this.createDsmlEntry(
          new QName("searchResultEntry", ns),
          le,
          ns);
        entriesElement.add(entryElement);
      }
    }

    final Element doneElement = entriesElement.addElement(
      new QName("searchResultDone", ns));
    final Element codeElement = doneElement.addElement(
      new QName("resultCode", ns));
    codeElement.addAttribute("code", "0");

    return doc;
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
   * This will take a DSML <code>Document</code> and convert it to a <code>
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
        "/*[name()='batchResponse']" +
        "/*[name()='searchResponse']" +
        "/*[name()='searchResultEntry']").iterator();
      while (entryIterator.hasNext()) {
        final LdapEntry le = this.createLdapEntry(
          (Element) entryIterator.next());
        if (le != null) {
          result.addEntry(le);
        }
      }
    }

    return result;
  }
}
