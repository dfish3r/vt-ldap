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
import edu.vt.middleware.ldap.handler.ExtendedLdapAttributeHandler;
import edu.vt.middleware.ldap.handler.ExtendedLdapResultHandler;
import edu.vt.middleware.ldap.handler.LdapAttributeHandler;
import edu.vt.middleware.ldap.handler.LdapResultHandler;
import edu.vt.middleware.ldap.handler.SearchCriteria;

/**
 * Provides common implementation to ldap search operations.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public abstract class AbstractSearchOperation<Q extends SearchRequest>
  extends AbstractLdapOperation<Q, LdapResult>
{

  /** Cache to use when performing searches. */
  protected Cache<Q> cache;


  /**
   * Returns the cache to check when performing search operations. When a cache
   * is provided it will be populated as new searches are performed and used
   * when a search request hits the cache.
   *
   * @return  cache
   */
  public Cache<Q> getCache()
  {
    return this.cache;
  }


  /**
   * Sets the cache.
   *
   * @param  c  cache to set
   */
  public void setCache(final Cache<Q> c)
  {
    this.cache = c;
  }


  /** {@inheritDoc} */
  protected void initializeRequest(
    final Q request, final LdapConnectionConfig lc)
  {
    request.setLdapResultHandlers(
      this.initializeLdapResultHandlers(request, this.ldapConnection));
  }


  /**
   * Initializes those ldap result handlers that require access to the ldap
   * connection.
   *
   * @param  request  to read result handlers from
   * @param  conn  to provide to result handlers
   * @return  initialized result handlers
   */
  protected LdapResultHandler[] initializeLdapResultHandlers(
    final Q request, final LdapConnection conn)
  {
    final LdapResultHandler[] handler = request.getLdapResultHandlers();
    if (handler != null && handler.length > 0) {
      for (LdapResultHandler h : handler) {
        if (ExtendedLdapResultHandler.class.isInstance(h)) {
          ((ExtendedLdapResultHandler) h).setResultLdapConnection(conn);
        }

        final LdapAttributeHandler[] attrHandler = h.getAttributeHandler();
        if (attrHandler != null && attrHandler.length > 0) {
          for (LdapAttributeHandler ah : attrHandler) {
            if (ExtendedLdapAttributeHandler.class.isInstance(ah)) {
              ((ExtendedLdapAttributeHandler) ah).setResultLdapConnection(
                conn);
            }
          }
        }
      }
    }
    return handler;
  }


  /**
   * Performs the ldap search.
   *
   * @param  request  to invoke search with
   * @return  ldap result
   * @throws LdapException if an error occurs
   */
  protected abstract LdapResult executeSearch(final Q request)
    throws LdapException;


  /** {@inheritDoc} */
  protected LdapResponse<LdapResult> invoke(final Q request)
    throws LdapException
  {
    LdapResult lr = null;
    if (this.cache != null) {
      lr = this.cache.get(request);
      if (lr == null) {
        lr = this.executeSearch(request);
        this.cache.put(request, lr);
      }
    } else {
      lr = this.executeSearch(request);
    }
    return new LdapResponse<LdapResult>(lr);
  }


  /**
   * Processes each ldap result handler after a search has been performed.
   *
   * @param  request  the search was performed with
   * @param  lr  ldap result of the search
   * @throws LdapException if an error occurs processing a handler
   */
  protected void executeLdapResultHandlers(
    final SearchRequest request, final LdapResult lr)
    throws LdapException
  {
    final LdapResultHandler[] handler = request.getLdapResultHandlers();
    if (handler != null && handler.length > 0) {
      final SearchCriteria sc = new SearchCriteria(request);
      for (int i = 0; i < handler.length; i++) {
        if (handler[i] != null) {
          handler[i].process(sc, lr);
        }
      }
    }
  }
}
