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
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.bean.LdapEntry;
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
  private static final long serialVersionUID = 4052208816312309345L;


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
    final Namespace ns = new Namespace("", "urn:oasis:names:tc:DSML:2:0:core");
    final Document doc = DocumentHelper.createDocument();
    final Element dsmlElement = doc.addElement(new QName("batchResponse", ns));
    final Element entriesElement = dsmlElement.addElement(
      new QName("searchResponse", ns));

    // build document object from results
    if (results != null) {
      try {
        while (results.hasNext()) {
          final SearchResult sr = results.next();
          final Element entryElement = this.createDsmlEntry(
            new QName("searchResultEntry", ns),
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
    final List<SearchResult> results = new ArrayList<SearchResult>();

    if (doc != null && doc.hasContent()) {
      final Iterator<?> entryIterator = doc.selectNodes(
        "/*[name()='batchResponse']" +
        "/*[name()='searchResponse']" +
        "/*[name()='searchResultEntry']").iterator();
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
}
