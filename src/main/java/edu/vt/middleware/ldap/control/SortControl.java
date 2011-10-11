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

  /** OID of this control. */
  public static final String REQUEST_OID = "1.2.840.113556.1.4.473";

  /** OID of this control. */
  public static final String RESPONSE_OID = "1.2.840.113556.1.4.474";

  /** sort keys. */
  private SortKey[] sortKeys;


  /**
   * Default constructor.
   */
  public SortControl() {}


  /**
   * Creates a new sort control.
   *
   * @param  keys  sort keys
   */
  public SortControl(final SortKey[] keys)
  {
    setSortKeys(keys);
  }


  /**
   * Creates a new sort control.
   *
   * @param  keys  sort keys
   * @param  critical  whether this control is critical
   */
  public SortControl(final SortKey[] keys, final boolean critical)
  {
    setSortKeys(keys);
    setCriticality(critical);
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
   * @param  keys  sort keys
   */
  public void setSortKeys(final SortKey[] keys)
  {
    sortKeys = keys;
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
