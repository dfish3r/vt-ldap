/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.handler;

import javax.naming.directory.Attributes;

/**
 * <code>SearchCriteria</code> contains the attributes used to perform ldap
 * searches.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class SearchCriteria
{

  /** dn */
  private String dn;

  /** filter */
  private String filter;

  /** filter arguments */
  private Object[] filterArgs;

  /** return attributes */
  private String[] returnAttrs;

  /** match attributes */
  private Attributes matchAttrs;


  /** Default constructor. */
  public SearchCriteria() {}


  /**
   * Creates a new search criteria with the supplied dn.
   *
   * @param  s  to set dn
   */
  public SearchCriteria(final String s)
  {
    this.dn = s;
  }


  /**
   * Gets the dn.
   *
   * @return  dn
   */
  public String getDn()
  {
    return this.dn;
  }


  /**
   * Sets the dn.
   *
   * @param  s  to set dn
   */
  public void setDn(final String s)
  {
    this.dn = s;
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
  public Object[] getFilterArgs()
  {
    return this.filterArgs;
  }


  /**
   * Sets the filter arguments.
   *
   * @param  o  to set filter argumets
   */
  public void setFilterArgs(final Object[] o)
  {
    this.filterArgs = o;
  }


  /**
   * Gets the return attributes.
   *
   * @return  return attributes
   */
  public String[] getReturnAttrs()
  {
    return this.returnAttrs;
  }


  /**
   * Sets the return attributes.
   *
   * @param  s  to set return attributes
   */
  public void setReturnAttrs(final String[] s)
  {
    this.returnAttrs = s;
  }


  /**
   * Gets the match attributes.
   *
   * @return  match attributes
   */
  public Attributes getMatchAttrs()
  {
    return this.matchAttrs;
  }


  /**
   * Sets the match attributes.
   *
   * @param  a  to set match attributes
   */
  public void setMatchAttrs(final Attributes a)
  {
    this.matchAttrs = a;
  }
}
