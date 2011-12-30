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
import edu.vt.middleware.ldap.handler.ExtendedLdapEntryHandler;
import edu.vt.middleware.ldap.handler.HandlerResult;
import edu.vt.middleware.ldap.handler.LdapEntryHandler;
import edu.vt.middleware.ldap.handler.SearchCriteria;

/**
 * Provides common implementation for search operations.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public abstract class AbstractSearchOperation<Q extends SearchRequest>
  extends AbstractOperation<Q, LdapResult>
{

  /** Cache to use when performing searches. */
  private Cache<Q> cache;


  /**
   * Creates a new abstract search connection.
   *
   * @param  conn  to use for this operation
   * @param  c  cache
   */
  public AbstractSearchOperation(final Connection conn, final Cache<Q> c)
  {
    super(conn);
    cache = c;
  }


  /**
   * Returns the cache to check when performing search operations. When a cache
   * is provided it will be populated as new searches are performed and used
   * when a search request hits the cache.
   *
   * @return  cache
   */
  public Cache<Q> getCache()
  {
    return cache;
  }


  /**
   * Sets the cache.
   *
   * @param  c  cache to set
   */
  public void setCache(final Cache<Q> c)
  {
    cache = c;
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final Q request, final ConnectionConfig cc)
  {
    initializeLdapEntryHandlers(request, getConnection());
  }


  /**
   * Initializes those ldap entry handlers that require access to the ldap
   * connection.
   *
   * @param  request  to read entry handlers from
   * @param  c  to provide to entry handlers
   */
  protected void initializeLdapEntryHandlers(
    final Q request, final Connection c)
  {
    final LdapEntryHandler[] handlers = request.getLdapEntryHandlers();
    if (handlers != null && handlers.length > 0) {
      for (LdapEntryHandler h : handlers) {
        if (ExtendedLdapEntryHandler.class.isInstance(h)) {
          ((ExtendedLdapEntryHandler) h).setResultConnection(c);
        }
      }
    }
  }


  /**
   * Performs the ldap search.
   *
   * @param  request  to invoke search with
   * @return  ldap result
   * @throws LdapException if an error occurs
   */
  protected abstract Response<LdapResult> executeSearch(final Q request)
    throws LdapException;


  /** {@inheritDoc} */
  @Override
  protected Response<LdapResult> invoke(final Q request)
    throws LdapException
  {
    logger.debug("invoke request={}", request);
    Response<LdapResult> response = null;
    if (cache != null) {
      final LdapResult lr = cache.get(request);
      if (lr == null) {
        response = executeSearch(request);
        cache.put(request, response.getResult());
        logger.debug("invoke stored result={} in cache", response.getResult());
      } else {
        logger.debug("invoke found result={} in cache", lr);
        response = new Response<LdapResult>(lr, null);
      }
    } else {
      response = executeSearch(request);
    }
    logger.debug("invoke response={} for request={}", response, request);
    return response;
  }


  /**
   * Processes each ldap entry handler after a search has been performed.
   * Returns a handler result containing an ldap entry processed by all
   * handlers. If any handler indicates that the search should be aborted, that
   * flag is returned to the search operation after all handlers have been
   * invoked.
   *
   * @param  request  the search was performed with
   * @param  entry  from a search
   *
   * @return  handler result
   *
   * @throws LdapException if an error occurs processing a handler
   */
  protected HandlerResult executeLdapEntryHandlers(
    final SearchRequest request, final LdapEntry entry)
    throws LdapException
  {
    LdapEntry processedEntry = entry;
    boolean abort = false;
    final LdapEntryHandler[] handler = request.getLdapEntryHandlers();
    if (handler != null && handler.length > 0) {
      final SearchCriteria sc = new SearchCriteria(request);
      for (int i = 0; i < handler.length; i++) {
        if (handler[i] != null) {
          final HandlerResult hr = handler[i].process(sc, processedEntry);
          if (hr.getAbortSearch()) {
            abort = true;
          }
          processedEntry = hr.getLdapEntry();
        }
      }
    }
    return new HandlerResult(processedEntry, abort);
  }
}
