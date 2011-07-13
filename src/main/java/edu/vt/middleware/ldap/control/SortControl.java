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

import java.util.Arrays;

/**
 * Request control for server side sorting. See RFC 2891.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SortControl extends AbstractControl
{

  /** sort keys. */
  private SortKey[] sortKeys;


  /**
   * Default constructor.
   */
  public SortControl() {}


  /**
   * Creates a new sort control.
   *
   * @param  sk  sort key
   */
  public SortControl(final SortKey[] sk)
  {
    setSortKeys(sk);
  }


  /**
   * Creates a new sort control.
   *
   * @param  sk  sort key
   * @param  b  whether this control is critical
   */
  public SortControl(final SortKey[] sk, final boolean b)
  {
    setSortKeys(sk);
    setCriticality(b);
  }


  /**
   * Returns the sort keys.
   *
   * @return  sort keys
   */
  public SortKey[] getSortKeys()
  {
    return sortKeys;
  }


  /**
   * Sets the sort keys.
   *
   * @param  sk  sort keys
   */
  public void setSortKeys(final SortKey[] sk)
  {
    sortKeys = sk;
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
        "[%s@%d::criticality=%s, sortKeys=%s]",
        getClass().getName(),
        hashCode(),
        criticality,
        sortKeys != null ? Arrays.asList(sortKeys) : null);
  }
}
