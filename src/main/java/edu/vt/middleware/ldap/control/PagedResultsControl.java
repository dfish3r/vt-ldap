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

  /** OID of this control. */
  public static final String OID = "1.2.840.113556.1.4.319";

  /** paged results size. */
  private int resultSize;

  /** server generated cookie. */
  private byte[] cookie;


  /**
   * Default constructor.
   */
  public PagedResultsControl() {}


  /**
   * Creates a new paged results control.
   *
   * @param  size  paged results size
   */
  public PagedResultsControl(final int size)
  {
    setSize(size);
  }


  /**
   * Creates a new paged results control.
   *
   * @param  size  paged results size
   * @param  critical  whether this control is critical
   */
  public PagedResultsControl(final int size, final boolean critical)
  {
    setSize(size);
    setCriticality(critical);
  }


  /**
   * Creates a new paged results control.
   *
   * @param  size  paged results size
   * @param  value  paged results cookie
   * @param  critical  whether this control is critical
   */
  public PagedResultsControl(
    final int size, final byte[] value, final boolean critical)
  {
    setSize(size);
    setCookie(value);
    setCriticality(critical);
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return OID;
  }


  /**
   * Returns the paged results size.
   *
   * @return  paged results size
   */
  public int getSize()
  {
    return resultSize;
  }


  /**
   * Sets the paged results size.
   *
   * @param  size  paged results size
   */
  public void setSize(final int size)
  {
    resultSize = size;
  }


  /**
   * Returns the paged results cookie.
   *
   * @return  paged results cookie
   */
  public byte[] getCookie()
  {
    return cookie;
  }


  /**
   * Sets the paged results cookie.
   *
   * @param  value  paged results cookie
   */
  public void setCookie(final byte[] value)
  {
    cookie = value;
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
        "[%s@%d::criticality=%s, size=%s, cookie=%s]",
        getClass().getName(),
        hashCode(),
        criticality,
        resultSize,
        cookie);
  }
}
