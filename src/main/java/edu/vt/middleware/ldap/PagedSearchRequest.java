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

import java.util.Arrays;

import edu.vt.middleware.ldap.handler.LdapResultHandler;

/**
 * Contains the data required to perform a paged ldap search operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class PagedSearchRequest extends SearchRequest
{
  /** Paged results size. */
  protected Integer pagedResultsSize;


  /** Default constructor. */
  public PagedSearchRequest() {}


  /**
   * Creates a new paged search request.
   *
   * @param  sf  search filter
   */
  public PagedSearchRequest(final SearchFilter sf)
  {
    super(sf);
  }


  /**
   * Creates a new paged search request.
   *
   * @param  sf  search filter
   * @param  attrs  to return
   */
  public PagedSearchRequest(final SearchFilter sf, final String[] attrs)
  {
    super(sf, attrs);
  }


  /**
   * Creates a new paged search request.
   *
   * @param  sf  search filter
   * @param  attrs  to return
   * @param  srh  search result handlers
   */
  public PagedSearchRequest(
    final SearchFilter sf,
    final String[] attrs,
    final LdapResultHandler[] srh)
  {
    super(sf, attrs, srh);
  }


  /**
   * Creates a new paged search request.
   *
   * @param  dn  to search
   */
  public PagedSearchRequest(final String dn)
  {
    super(dn);
  }


  /**
   * Creates a new paged search request.
   *
   * @param  dn  to search
   * @param  sf  search filter
   */
  public PagedSearchRequest(final String dn, final SearchFilter sf)
  {
    super(dn, sf);
  }


  /**
   * Creates a new paged search request.
   *
   * @param  dn  to search
   * @param  sf  search filter
   * @param  attrs  to return
   */
  public PagedSearchRequest(
    final String dn, final SearchFilter sf, final String[] attrs)
  {
    super(dn, sf, attrs);
  }


  /**
   * Creates a new paged search request.
   *
   * @param  dn  to search
   * @param  sf  search filter
   * @param  attrs  to return
   * @param  srh  search result handlers
   */
  public PagedSearchRequest(
    final String dn,
    final SearchFilter sf,
    final String[] attrs,
    final LdapResultHandler[] srh)
  {
    super(dn, sf, attrs, srh);
  }


  /**
   * Returns the paged results size.
   *
   * @return  paged results size
   */
  public Integer getPagedResultsSize()
  {
    return this.pagedResultsSize;
  }


  /**
   * Sets the paged results size.
   *
   * @param  i  paged results size
   */
  public void setPagedResultsSize(final int i)
  {
    this.pagedResultsSize = i;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
    String.format(
      "%s@%d::dn=%s, searchFilter=%s, returnAttributes=%s, searchScope=%s, " +
      "timeLimit=%s, countLimit=%s, batchSize=%s, derefAliases=%s, " +
      "referralBehavior=%s, typesOnly=%s, binaryAttributes=%s, " +
      "sortBehavior=%s, searchResultHandler=%s, pagedResultsSize=%s",
      this.getClass().getName(),
      this.hashCode(),
      this.baseDn,
      this.filter,
      this.retAttrs != null ? Arrays.asList(this.retAttrs) : null,
      this.scope,
      this.timeLimit,
      this.countLimit,
      this.batchSize,
      this.derefAliases,
      this.referralBehavior,
      this.typesOnly,
      this.binaryAttrs != null ? Arrays.asList(this.binaryAttrs) : null,
      this.sortBehavior,
      this.handlers != null ? Arrays.asList(this.handlers) : null,
      this.pagedResultsSize);
  }
}
