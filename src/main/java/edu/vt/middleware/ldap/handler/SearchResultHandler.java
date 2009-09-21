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

import javax.naming.directory.SearchResult;

/**
 * SearchResultHandler provides post search processing of ldap search results.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface SearchResultHandler
  extends ResultHandler<SearchResult, SearchResult>
{


  /**
   * Gets the attribute handlers.
   *
   * @return  <code>AttributeHandler[]</code>
   */
  AttributeHandler[] getAttributeHandler();


  /**
   * Sets the attribute handlers.
   *
   * @param  ah  <code>AttributeHandler[]</code>
   */
  void setAttributeHandler(final AttributeHandler[] ah);
}
