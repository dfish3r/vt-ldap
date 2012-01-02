/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.cache.Cache;
import edu.vt.middleware.ldap.handler.HandlerResult;
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
   * @param  conn  connection
   */
  public SearchOperation(final Connection conn)
  {
    super(conn, null);
  }


  /**
   * Creates a new search operation.
   *
   * @param  conn  connection
   * @param  c  cache
   */
  public SearchOperation(final Connection conn, final Cache<SearchRequest> c)
  {
    super(conn, c);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<LdapResult> executeSearch(final SearchRequest request)
    throws LdapException
  {
    final LdapResult lr = new LdapResult(request.getSortBehavior());
    final SearchIterator si = getConnection().getProviderConnection().search(
      request);
    try {
      while (si.hasNext()) {
        final LdapEntry le = si.next();
        if (le != null) {
          final HandlerResult hr = executeLdapEntryHandlers(request, le);
          if (hr.getLdapEntry() != null) {
            lr.addEntry(hr.getLdapEntry());
          }
          if (hr.getAbortSearch()) {
            logger.debug("Aborting search on entry=%s", le);
            break;
          }
        }
      }
    } finally {
      si.close();
    }

    final Response<Void> response = si.getResponse();
    return
      new Response<LdapResult>(
        lr,
        response.getResultCode(),
        response.getControls());
  }
}
