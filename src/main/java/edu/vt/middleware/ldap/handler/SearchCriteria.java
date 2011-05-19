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
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.SearchRequest;

/**
 * Contains the attributes used to perform ldap searches.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchCriteria
{

  /** dn. */
  private String dn;

  /** filter. */
  private String filter;

  /** filter arguments. */
  private Object[] filterArgs;

  /** return attributes. */
  private String[] returnAttrs;


  /** Default constructor. */
  public SearchCriteria() {}


  /**
   * Creates a new search criteria.
   *
   * @param  s  to set dn
   */
  public SearchCriteria(final String s)
  {
    dn = s;
  }


  /**
   * Creates a new search criteria.
   *
   * @param  request  to set properties with
   */
  public SearchCriteria(final SearchRequest request)
  {
    setDn(request.getBaseDn());
    if (request.getSearchFilter() != null) {
      setFilter(request.getSearchFilter().getFilter());
      setFilterArgs(request.getSearchFilter().getFilterArgs().toArray());
    }
    setReturnAttrs(request.getReturnAttributes());
  }


  /**
   * Gets the dn.
   *
   * @return  dn
   */
  public String getDn()
  {
    return dn;
  }


  /**
   * Sets the dn.
   *
   * @param  s  to set dn
   */
  public void setDn(final String s)
  {
    dn = s;
  }


  /**
   * Gets the filter.
   *
   * @return  filter
   */
  public String getFilter()
  {
    return filter;
  }


  /**
   * Sets the filter.
   *
   * @param  s  to set filter
   */
  public void setFilter(final String s)
  {
    filter = s;
  }


  /**
   * Gets the filter arguments.
   *
   * @return  filter args
   */
  public Object[] getFilterArgs()
  {
    return filterArgs;
  }


  /**
   * Sets the filter arguments.
   *
   * @param  o  to set filter arguments
   */
  public void setFilterArgs(final Object[] o)
  {
    filterArgs = o;
  }


  /**
   * Gets the return attributes.
   *
   * @return  return attributes
   */
  public String[] getReturnAttrs()
  {
    return returnAttrs;
  }


  /**
   * Sets the return attributes.
   *
   * @param  s  to set return attributes
   */
  public void setReturnAttrs(final String[] s)
  {
    returnAttrs = s;
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
        "[%s@%d::dn=%s, filter=%s, filterArgs=%s, returnAttrs=%s]",
        getClass().getName(),
        hashCode(),
        dn,
        filter,
        filterArgs,
        returnAttrs);
  }
}
