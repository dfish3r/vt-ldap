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
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.Ldap;

/**
 * Provides an interface for search result handlers that require the use of the
 * <code>Ldap</code> object that was used to perform the original search.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ExtendedSearchResultHandler extends SearchResultHandler
{


  /**
   * Gets the <code>Ldap</code> used by the search operation invoking this
   * handler.
   *
   * @return  <code>Ldap</code>
   */
  Ldap getSearchResultLdap();


  /**
   * Sets the <code>Ldap</code> used by the search operation invoking this
   * handler.
   *
   * @param  l  <code>Ldap</code>
   */
  void setSearchResultLdap(final Ldap l);
}
