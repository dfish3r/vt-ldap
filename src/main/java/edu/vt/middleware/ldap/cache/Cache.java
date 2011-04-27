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
package edu.vt.middleware.ldap.cache;

import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Interface for cache implementations.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public interface Cache<Q extends SearchRequest>
{


  /**
   * Returns the ldap result for the supplied request.
   *
   * @param  request  to find ldap result with
   * @return  ldap result
   */
  LdapResult get(Q request);


  /**
   * Stores the ldap result for the supplied request.
   *
   * @param  request  used to find ldap result
   * @param  lr  found with request
   */
  void put(Q request, LdapResult lr);
}
