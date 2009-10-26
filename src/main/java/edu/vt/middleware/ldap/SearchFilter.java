/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <code>SearchFilter</code> provides a bean for a filter and it's arguments.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class SearchFilter
{

  /** filter. */
  private String filter;

  /** filter arguments. */
  private List<Object> filterArgs = new ArrayList<Object>();


  /** Default constructor. */
  public SearchFilter() {}


  /**
   * Creates a new search filter with the supplied filter.
   *
   * @param  s  to set filter
   */
  public SearchFilter(final String s)
  {
    this.filter = s;
  }


  /**
   * Creates a new string search filter with the supplied filter and arguments.
   *
   * @param  s  to set filter
   * @param  o  to set filter arguments
   */
  public SearchFilter(final String s, final List<?> o)
  {
    this.setFilter(s);
    this.setFilterArgs(o);
  }


  /**
   * Creates a new search filter with the supplied filter and arguments.
   *
   * @param  s  to set filter
   * @param  o  to set filter arguments
   */
  public SearchFilter(final String s, final Object[] o)
  {
    this.setFilter(s);
    this.setFilterArgs(o);
  }


  /**
   * Gets the filter.
   *
   * @return  filter
   */
  public String getFilter()
  {
    return this.filter;
  }


  /**
   * Sets the filter.
   *
   * @param  s  to set filter
   */
  public void setFilter(final String s)
  {
    this.filter = s;
  }


  /**
   * Gets the filter arguments.
   *
   * @return  filter args
   */
  public List<Object> getFilterArgs()
  {
    return this.filterArgs;
  }


  /**
   * Sets the filter arguments.
   *
   * @param  o  to set filter arguments
   */
  public void setFilterArgs(final List<?> o)
  {
    if (o != null) {
      this.filterArgs.addAll(o);
    }
  }


  /**
   * Sets the filter arguments.
   *
   * @param  o  to set filter arguments
   */
  public void setFilterArgs(final Object[] o)
  {
    if (o != null) {
      this.filterArgs.addAll(Arrays.asList(o));
    }
  }


  /**
   * This returns a string representation of this search filter.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return
      String.format("filter=%s,filterArgs=%s", this.filter, this.filterArgs);
  }
}
