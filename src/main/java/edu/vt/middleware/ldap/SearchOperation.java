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
   * @param  lc  ldap connection
   */
  public SearchOperation(final LdapConnection lc)
  {
    this.ldapConnection = lc;
    this.initialize(lc.getLdapConnectionConfig());
  }


  /** {@inheritDoc} */
  protected LdapResponse<LdapResult> invoke(final SearchRequest request)
    throws LdapException
  {
    final LdapResult lr =
      this.ldapConnection.getProviderConnection().search(request);
    final LdapResultHandler[] handler = request.getLdapResultHandlers();
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
}
