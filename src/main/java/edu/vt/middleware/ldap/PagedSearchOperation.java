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
    ldapConnection = lc;
    initialize(lc.getLdapConnectionConfig());
  }


  /**
   * Creates a new paged search operation.
   *
   * @param  lc  ldap connection
   * @param  c  cache
   */
  public PagedSearchOperation(
    final LdapConnection lc, final Cache<PagedSearchRequest> c)
  {
    ldapConnection = lc;
    cache = c;
    initialize(lc.getLdapConnectionConfig());
  }


  /** {@inheritDoc} */
  @Override
  protected LdapResult executeSearch(final PagedSearchRequest request)
    throws LdapException
  {
    final LdapResult lr =
      ldapConnection.getProviderConnection().pagedSearch(request);
    executeLdapResultHandlers(request, lr);
    return lr;
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final PagedSearchRequest request, final LdapConnectionConfig config)
  {
    super.initializeRequest(request, config);
  }
}
