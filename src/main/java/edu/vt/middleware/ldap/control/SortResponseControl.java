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

import edu.vt.middleware.ldap.ResultCode;

/**
 * Response control for server side sorting. See RFC 2891.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SortResponseControl extends AbstractControl
                                 implements ResponseControl
{

  /** OID of this control. */
  public static final String OID = "1.2.840.113556.1.4.474";

  /** Result of the server side sorting. */
  private ResultCode sortResult;

  /** Failed attribute name. */
  private String attributeName;


  /**
   * Default constructor.
   */
  public SortResponseControl() {}


  /**
   * Creates a new sort response control.
   *
   * @param  critical  whether this control is critical
   */
  public SortResponseControl(final boolean critical)
  {
    setCriticality(critical);
  }


  /**
   * Creates a new sort response control.
   *
   * @param  code  result of the sort
   * @param  critical  whether this control is critical
   */
  public SortResponseControl(final ResultCode code, final boolean critical)
  {
    setSortResult(code);
    setCriticality(critical);
  }


  /**
   * Creates a new sort response control.
   *
   * @param  code  result of the sort
   * @param  attrName  name of the failed attribute
   * @param  critical  whether this control is critical
   */
  public SortResponseControl(
    final ResultCode code, final String attrName, final boolean critical)
  {
    setSortResult(code);
    setAttributeName(attrName);
    setCriticality(critical);
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return OID;
  }


  /**
   * Returns the result code of the server side sort.
   *
   * @return  result code
   */
  public ResultCode getSortResult()
  {
    return sortResult;
  }


  /**
   * Sets the result code of the server side sort.
   *
   * @param  code  result code
   */
  public void setSortResult(final ResultCode code)
  {
    sortResult = code;
  }


  /**
   * Returns the attribute name that caused the sort to fail.
   *
   * @return  attribute name
   */
  public String getAttributeName()
  {
    return attributeName;
  }


  /**
   * Sets the attribute name that caused the sort to fail.
   *
   * @param  name  of an attribute
   */
  public void setAttributeName(final String name)
  {
    attributeName = name;
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
        "[%s@%d::criticality=%s, sortResult=%s, attributeName=%s]",
        getClass().getName(),
        hashCode(),
        criticality,
        sortResult,
        attributeName);
  }
}
