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
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

/**
 * <code>AttributesProcessor</code> provides methods to help with the processing
 * of Attributes objects using an AttributeHandler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AttributesProcessor
{


  /** Default constructor. */
  private AttributesProcessor() {}


  /**
   * Process the attributes of a ldap search search.
   *
   * @param  sc  <code>SearchCriteria</code> used to find search result
   * @param  attrs  <code>Attributes</code> to pass to the handler
   * @param  handler  <code>AttributeHandler</code> to process attributes
   *
   * @return  <code>Attributes</code> handler processed attributes
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public static Attributes executeHandler(
    final SearchCriteria sc,
    final Attributes attrs,
    final AttributeHandler handler)
    throws NamingException
  {
    return executeHandler(sc, attrs, handler, null);
  }


  /**
   * Process the attributes of a ldap search search.
   * Any exceptions passed into this method will be ignored and
   * results will be returned as if no exception occurred.
   *
   * @param  sc  <code>SearchCriteria</code> used to find search result
   * @param  attrs  <code>Attributes</code> to pass to the handler
   * @param  handler  <code>AttributeHandler</code> to process attributes
   * @param  ignore  <code>Class[]</code> of exception types to ignore
   *
   * @return  <code>Attributes</code> handler processed attributes
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public static Attributes executeHandler(
    final SearchCriteria sc,
    final Attributes attrs,
    final AttributeHandler handler,
    final Class<?>[] ignore)
    throws NamingException
  {
    Attributes newAttrs = null;
    if (handler != null) {
      newAttrs = new BasicAttributes(attrs.isCaseIgnored());
      for (Attribute attr : handler.process(sc, attrs.getAll(), ignore)) {
        newAttrs.put(attr);
      }
    } else {
      newAttrs = attrs;
    }
    return newAttrs;
  }
}
