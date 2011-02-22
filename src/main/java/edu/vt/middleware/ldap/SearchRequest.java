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
 * Contains the data required to perform an ldap search operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class SearchRequest implements LdapRequest
{
  /** DN to search. */
  protected String baseDn = "";

  /** Search filter to execute. */
  protected SearchFilter filter;

  /** Attributes to return. */
  protected String[] retAttrs;

  /** Search scope.*/
  protected SearchScope scope;

  /** Time search operation will block. */
  protected long timeLimit;

  /** Number of entries to return. */
  protected long countLimit;

  /** Batch size to return results in. */
  protected int batchSize = -1;

  /** How to handle aliases. */
  protected DerefAliases derefAliases;

  /** How to handle referrals. */
  protected ReferralBehavior referralBehavior;

  /** Whether to return only attribute types. */
  protected boolean typesOnly;

  /** Binary attribute names. */
  protected String[] binaryAttrs;

  /** Sort behavior of results. */
  protected SortBehavior sortBehavior = SortBehavior.getDefaultSortBehavior();

  /** Ldap result handlers. */
  protected LdapResultHandler[] handlers;

  /** Search result codes to ignore. */
  protected ResultCode[] searchIgnoreResultCodes = new ResultCode[] {
    ResultCode.TIME_LIMIT_EXCEEDED, ResultCode.SIZE_LIMIT_EXCEEDED, };


  /** Default constructor. */
  public SearchRequest() {}


  /**
   * Creates a new search request.
   *
   * @param  sf  search filter
   */
  public SearchRequest(final SearchFilter sf)
  {
    this.setSearchFilter(sf);
  }


  /**
   * Creates a new search request.
   *
   * @param  sf  search filter
   * @param  attrs  to return
   */
  public SearchRequest(final SearchFilter sf, final String[] attrs)
  {
    this.setSearchFilter(sf);
    this.setReturnAttributes(attrs);
  }


  /**
   * Creates a new search request.
   *
   * @param  sf  search filter
   * @param  attrs  to return
   * @param  srh  search result handlers
   */
  public SearchRequest(
    final SearchFilter sf,
    final String[] attrs,
    final LdapResultHandler[] srh)
  {
    this.setSearchFilter(sf);
    this.setReturnAttributes(attrs);
    this.setLdapResultHandlers(srh);
  }


  /**
   * Creates a new search request.
   *
   * @param  dn  to search
   */
  public SearchRequest(final String dn)
  {
    this.setBaseDn(dn);
  }


  /**
   * Creates a new search request.
   *
   * @param  dn  to search
   * @param  sf  search filter
   */
  public SearchRequest(final String dn, final SearchFilter sf)
  {
    this.setBaseDn(dn);
    this.setSearchFilter(sf);
  }


  /**
   * Creates a new search request.
   *
   * @param  dn  to search
   * @param  sf  search filter
   * @param  attrs  to return
   */
  public SearchRequest(
    final String dn, final SearchFilter sf, final String[] attrs)
  {
    this.setBaseDn(dn);
    this.setSearchFilter(sf);
    this.setReturnAttributes(attrs);
  }


  /**
   * Creates a new search request.
   *
   * @param  dn  to search
   * @param  sf  search filter
   * @param  attrs  to return
   * @param  srh  search result handlers
   */
  public SearchRequest(
    final String dn,
    final SearchFilter sf,
    final String[] attrs,
    final LdapResultHandler[] srh)
  {
    this.setBaseDn(dn);
    this.setSearchFilter(sf);
    this.setReturnAttributes(attrs);
    this.setLdapResultHandlers(srh);
  }


  /**
   * Returns the base DN.
   *
   * @return  base DN
   */
  public String getBaseDn()
  {
    return this.baseDn;
  }


  /**
   * Sets the base DN.
   *
   * @param  dn base DN
   */
  public void setBaseDn(final String dn)
  {
    this.baseDn = dn;
  }


  /**
   * Returns the search filter.
   *
   * @return  search filter
   */
  public SearchFilter getSearchFilter()
  {
    return this.filter;
  }


  /**
   * Sets the search filter.
   *
   * @param  sf  search filter
   */
  public void setSearchFilter(final SearchFilter sf)
  {
    this.filter = sf;
  }


  /**
   * Returns the search return attributes.
   *
   * @return  search return attributes
   */
  public String[] getReturnAttributes()
  {
    return this.retAttrs;
  }


  /**
   * Sets the search return attributes.
   *
   * @param  attrs  search return attributes
   */
  public void setReturnAttributes(final String[] attrs)
  {
    this.retAttrs = attrs;
  }


  /**
   * Gets the search scope.
   *
   * @return  search scope
   */
  public SearchScope getSearchScope()
  {
    return this.scope;
  }


  /**
   * Sets the search scope.
   *
   * @param  ss  search scope
   */
  public void setSearchScope(final SearchScope ss)
  {
    this.scope = ss;
  }


  /**
   * Returns the time limit.
   *
   * @return  time limit
   */
  public long getTimeLimit()
  {
    return this.timeLimit;
  }


  /**
   * Sets the time limit.
   *
   * @param  l  time limit
   */
  public void setTimeLimit(final long l)
  {
    this.timeLimit = l;
  }


  /**
   * Returns the count limit.
   *
   * @return  count limit
   */
  public long getCountLimit()
  {
    return this.countLimit;
  }


  /**
   * Sets the count limit.
   *
   * @param  l  count limit
   */
  public void setCountLimit(final long l)
  {
    this.countLimit = l;
  }


  /**
   * Returns the batch size.
   *
   * @return  batch size
   */
  public int getBatchSize()
  {
    return this.batchSize;
  }


  /**
   * Sets the batch size.
   *
   * @param  i  batch size
   */
  public void setBatchSize(final int i)
  {
    this.batchSize = i;
  }


  /**
   * Returns how to dereference aliases.
   *
   * @return  how to dereference aliases
   */
  public DerefAliases getDerefAliases()
  {
    return this.derefAliases;
  }


  /**
   * Sets how to dereference aliases.
   *
   * @param  da  how to dereference aliases
   */
  public void setDerefAliases(final DerefAliases da)
  {
    this.derefAliases = da;
  }


  /**
   * Returns how to handle referrals.
   *
   * @return  how to handle referrals
   */
  public ReferralBehavior getReferralBehavior()
  {
    return this.referralBehavior;
  }


  /**
   * Sets how to handle referrals.
   *
   * @param  rb  how to handle referrals
   */
  public void setReferralBehavior(final ReferralBehavior rb)
  {
    this.referralBehavior = rb;
  }


  /**
   * Returns whether to return only attribute types.
   *
   * @return  whether to return only attribute types
   */
  public boolean getTypesOnly()
  {
    return this.typesOnly;
  }


  /**
   * Sets whether to return only attribute types.
   *
   * @param  b  whether to return only attribute types
   */
  public void setTypeOnly(final boolean b)
  {
    this.typesOnly = b;
  }


  /**
   * Returns names of binary attributes.
   *
   * @return  binary attribute names
   */
  public String[] getBinaryAttributes()
  {
    return this.binaryAttrs;
  }


  /**
   * Sets names of binary attributes.
   *
   * @param  s  binary attribute names
   */
  public void setBinaryAttributes(final String[] s)
  {
    this.binaryAttrs = s;
  }


  /**
   * Returns the sort behavior.
   *
   * @return  sort behavior
   */
  public SortBehavior getSortBehavior()
  {
    return this.sortBehavior;
  }


  /**
   * Sets the sort behavior.
   *
   * @param  sb  sort behavior
   */
  public void setSortBehavior(final SortBehavior sb)
  {
    this.sortBehavior = sb;
  }


  /**
   * Returns the ldap result handlers.
   *
   * @return  ldap result handlers
   */
  public LdapResultHandler[] getLdapResultHandlers()
  {
    return this.handlers;
  }


  /**
   * Sets the ldap result handlers.
   *
   * @param  lrh  ldap result handlers
   */
  public void setLdapResultHandlers(final LdapResultHandler[] lrh)
  {
    this.handlers = lrh;
  }


  /**
   * Returns the search ignore result codes.
   *
   * @return  result codes to ignore
   */
  public ResultCode[] getSearchIgnoreResultCodes()
  {
    return this.searchIgnoreResultCodes;
  }


  /**
   * Sets the search ignore result codes.
   *
   * @param  codes  to ignore
   */
  public void setSearchIgnoreResultCodes(final ResultCode[] codes)
  {
    this.searchIgnoreResultCodes = codes;
  }


  /**
   * Returns a search request initialized with the supplied request.
   *
   * @param  sr  search request to read properties from
   * @return  search request
   */
  public static SearchRequest newSearchRequest(final SearchRequest sr)
  {
    final SearchRequest request = new SearchRequest();
    request.setBaseDn(sr.getBaseDn());
    request.setSearchFilter(sr.getSearchFilter());
    request.setReturnAttributes(sr.getReturnAttributes());
    request.setSearchScope(sr.getSearchScope());
    request.setTimeLimit(sr.getTimeLimit());
    request.setCountLimit(sr.getCountLimit());
    request.setBatchSize(sr.getBatchSize());
    request.setDerefAliases(sr.getDerefAliases());
    request.setReferralBehavior(sr.getReferralBehavior());
    request.setTypeOnly(sr.getTypesOnly());
    request.setBinaryAttributes(sr.getBinaryAttributes());
    request.setSortBehavior(sr.getSortBehavior());
    request.setLdapResultHandlers(sr.getLdapResultHandlers());
    request.setSearchIgnoreResultCodes(sr.getSearchIgnoreResultCodes());
    return request;
  }


  /**
   * Returns a search request initialized for use with an object level search
   * scope.
   *
   * @param  dn  of an ldap entry
   * @return  search request
   */
  public static SearchRequest newObjectScopeSearchRequest(final String dn)
  {
    return newObjectScopeSearchRequest(dn, null);
  }


  /**
   * Returns a search request initialized for use with an object level search
   * scope.
   *
   * @param  dn  of an ldap entry
   * @param attrs  to return
   * @return  search request
   */
  public static SearchRequest newObjectScopeSearchRequest(
    final String dn, final String[] attrs)
  {
    return newObjectScopeSearchRequest(
      dn, attrs, new SearchFilter("(objectClass=*)"));
  }


  /**
   * Returns a search request initialized for use with an object level search
   * scope.
   *
   * @param  dn  of an ldap entry
   * @param attrs  to return
   * @param filter  to execute on the ldap entry
   * @return  search request
   */
  public static SearchRequest newObjectScopeSearchRequest(
    final String dn, final String[] attrs, final SearchFilter filter)
  {
    final SearchRequest request = new SearchRequest();
    request.setBaseDn(dn);
    request.setSearchFilter(filter);
    request.setReturnAttributes(attrs);
    request.setSearchScope(SearchScope.OBJECT);
    return request;
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
        "%s@%d::baseDn=%s, searchFilter=%s, returnAttributes=%s, " +
        "searchScope=%s, timeLimit=%s, countLimit=%s, batchSize=%s, " +
        "derefAliases=%s, referralBehavior=%s, typesOnly=%s, " +
        "binaryAttributes=%s, sortBehavior=%s, searchResultHandler=%s",
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
        this.handlers != null ? Arrays.asList(this.handlers) : null);
  }
}
