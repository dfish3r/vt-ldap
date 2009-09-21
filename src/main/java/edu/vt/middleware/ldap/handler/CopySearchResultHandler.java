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

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

/**
 * <code>CopySearchResultHandler</code> converts a NamingEnumeration of search
 * results into a List of search results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CopySearchResultHandler extends CopyResultHandler<SearchResult>
  implements SearchResultHandler
{

  /** Attribute handler. */
  private AttributeHandler[] attributeHandler;


  /** {@inheritDoc}. */
  public AttributeHandler[] getAttributeHandler()
  {
    return this.attributeHandler;
  }


  /** {@inheritDoc}. */
  public void setAttributeHandler(final AttributeHandler[] ah)
  {
    this.attributeHandler = ah;
  }


  /**
   * This will return a deep copy of the supplied <code>SearchResult</code>.
   *
   * @param  sc  <code>SearchCriteria</code> used to find enumeration
   * @param  sr  <code>SearchResult</code> to copy
   *
   * @return  <code>SearchResult</code>
   *
   * @throws  NamingException  if the result cannot be read
   */
  protected SearchResult processResult(
    final SearchCriteria sc,
    final SearchResult sr)
    throws NamingException
  {
    return
      new SearchResult(
        this.processDn(sc, sr),
        sr.getClassName(),
        sr.getObject(),
        this.processAttributes(sc, sr),
        sr.isRelative());
  }


  /**
   * Process the dn of a ldap search result.
   *
   * @param  sc  <code>SearchCriteria</code> used to find search result
   * @param  sr  <code>SearchResult</code> to extract the dn from
   *
   * @return  <code>String</code> processed dn
   */
  protected String processDn(final SearchCriteria sc, final SearchResult sr)
  {
    return sr.getName();
  }


  /**
   * Process the attributes of a ldap search search.
   *
   * @param  sc  <code>SearchCriteria</code> used to find search result
   * @param  sr  <code>SearchResult</code> to extract the attributes from
   *
   * @return  <code>Attributes</code> processed attributes
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  protected Attributes processAttributes(
    final SearchCriteria sc,
    final SearchResult sr)
    throws NamingException
  {
    Attributes newAttrs = sr.getAttributes();
    if (this.attributeHandler != null && this.attributeHandler.length > 0) {
      for (AttributeHandler ah : this.attributeHandler) {
        newAttrs = AttributesProcessor.executeHandler(sc, newAttrs, ah);
      }
    }
    return newAttrs;
  }
}
