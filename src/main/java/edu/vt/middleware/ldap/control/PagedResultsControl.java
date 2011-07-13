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
package edu.vt.middleware.ldap.control;

/**
 * Request control for PagedResults. See RFC 2696.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PagedResultsControl extends AbstractControl
{

  /** paged results size. */
  private int size;


  /**
   * Default constructor.
   */
  public PagedResultsControl() {}


  /**
   * Creates a new paged results.
   *
   * @param  i  paged results size
   */
  public PagedResultsControl(final int i)
  {
    setSize(i);
  }


  /**
   * Creates a new paged results.
   *
   * @param  i  paged results size
   * @param  b  whether this control is critical
   */
  public PagedResultsControl(final int i, final boolean b)
  {
    setSize(i);
    setCriticality(b);
  }


  /**
   * Returns the paged results size.
   *
   * @return  paged results size
   */
  public int getSize()
  {
    return size;
  }


  /**
   * Sets the paged results size.
   *
   * @param  i  paged results size
   */
  public void setSize(final int i)
  {
    size = i;
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
        "[%s@%d::criticality=%s, size=%s]",
        getClass().getName(),
        hashCode(),
        criticality,
        size);
  }
}
