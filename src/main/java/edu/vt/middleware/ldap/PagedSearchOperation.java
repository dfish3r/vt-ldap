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

import edu.vt.middleware.ldap.handler.LdapResultHandler;
import edu.vt.middleware.ldap.handler.SearchCriteria;

/**
 * Executes a paged ldap search operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class PagedSearchOperation
  extends AbstractSearchOperation<PagedSearchRequest>
{


  /**
   * Creates a new paged search operation.
   *
   * @param  lc  ldap connection
   */
  public PagedSearchOperation(final LdapConnection lc)
  {
    this.ldapConnection = lc;
    this.initialize(lc.getLdapConfig());
  }


  /** {@inheritDoc} */
  protected LdapResponse<LdapResult> invoke(final PagedSearchRequest request)
    throws LdapException
  {
    final LdapResult lr =
      this.ldapConnection.getProviderConnection().pagedSearch(request);
    final LdapResultHandler[] handler = request.getLdapResultHandler();
    if (handler != null && handler.length > 0) {
      final SearchCriteria sc = new SearchCriteria(request);
      for (int i = 0; i < handler.length; i++) {
        if (handler[i] != null) {
          handler[i].process(sc, lr);
        }
      }
    }
    return new LdapResponse<LdapResult>(lr);
  }


  /** {@inheritDoc} */
  protected void initializeRequest(
    final PagedSearchRequest request, final LdapConfig config)
  {
    super.initializeRequest(request, config);
    if (request.getPagedResultsSize() == null) {
      request.setPagedResultsSize(config.getPagedResultsSize());
    }
  }
}
