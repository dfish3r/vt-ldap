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
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.cache.Cache;
import edu.vt.middleware.ldap.provider.SearchIterator;

/**
 * Executes an ldap search operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class SearchOperation extends AbstractSearchOperation<SearchRequest>
{


  /**
   * Creates a new search operation.
   *
   * @param  c  connection
   */
  public SearchOperation(final Connection c)
  {
    connection = c;
    initialize(c.getConnectionConfig());
  }


  /**
   * Creates a new search operation.
   *
   * @param  conn  connection
   * @param  c  cache
   */
  public SearchOperation(
    final Connection conn, final Cache<SearchRequest> c)
  {
    connection = conn;
    cache = c;
    initialize(conn.getConnectionConfig());
  }


  /** {@inheritDoc} */
  @Override
  protected Response<LdapResult> executeSearch(final SearchRequest request)
    throws LdapException
  {
    final LdapResult lr = new LdapResult(request.getSortBehavior());
    final SearchIterator si = connection.getProviderConnection().search(
      request);
    try {
      while (si.hasNext()) {
        lr.addEntry(si.next());
      }
    } finally {
      si.close();
    }
    executeLdapResultHandlers(request, lr);
    return new Response<LdapResult>(lr, null);
  }
}
