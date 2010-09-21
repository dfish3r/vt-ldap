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

import javax.naming.directory.SearchResult;

/**
 * <code>CaseSearchResultHandler</code> provides the ability to modify the case
 * of ldap search result DNs, attribute names, and attribute values.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class CaseChangeSearchResultHandler extends CopySearchResultHandler
{

  /** Enum to define the type of case change. */
  public enum CaseChange {

    /** no case change. */
    NONE,

    /** lower case. */
    LOWER,

    /** upper case. */
    UPPER;


    /**
     * This changes the supplied string based on the supplied case change.
     *
     * @param  cc  <code>CaseChange</code> to perform
     * @param  string  <code>String</code> to modify
     *
     * @return  <code>String</code> that has been changed
     */
    public static String perform(final CaseChange cc, final String string)
    {
      String s = null;
      if (CaseChange.LOWER == cc) {
        s = string.toLowerCase();
      } else if (CaseChange.UPPER == cc) {
        s = string.toUpperCase();
      } else if (CaseChange.NONE == cc) {
        s = string;
      }
      return s;
    }
  }


  /** Type of case modification to make to the entry DN. */
  private CaseChange dnCaseChange = CaseChange.NONE;

  /** Attribute handler for case modifications. */
  private CaseChangeAttributeHandler attributeHandler =
    new CaseChangeAttributeHandler();


  /** Creates a new <code>CaseSearchResultHandler</code>. */
  public CaseChangeSearchResultHandler()
  {
    this.setAttributeHandler(new AttributeHandler[] {this.attributeHandler});
  }


  /**
   * Returns the DN case change.
   *
   * @return  <code>CaseChange</code>
   */
  public CaseChange getDnCaseChange()
  {
    return this.dnCaseChange;
  }


  /**
   * Sets the DN case change.
   *
   * @param  caseChange  <code>CaseChange</code>
   */
  public void setDnCaseChange(final CaseChange caseChange)
  {
    this.dnCaseChange = caseChange;
  }


  /**
   * Returns the attribute name case change.
   *
   * @return  <code>CaseChange</code>
   */
  public CaseChange getAttributeNameCaseChange()
  {
    return this.attributeHandler.getAttributeNameCaseChange();
  }


  /**
   * Sets the attribute name case change.
   *
   * @param  caseChange  <code>CaseChange</code>
   */
  public void setAttributeNameCaseChange(final CaseChange caseChange)
  {
    this.attributeHandler.setAttributeNameCaseChange(caseChange);
  }


  /**
   * Returns the attribute value case change.
   *
   * @return  <code>CaseChange</code>
   */
  public CaseChange getAttributeValueCaseChange()
  {
    return this.attributeHandler.getAttributeValueCaseChange();
  }


  /**
   * Sets the attribute value case change.
   *
   * @param  caseChange  <code>CaseChange</code>
   */
  public void setAttributeValueCaseChange(final CaseChange caseChange)
  {
    this.attributeHandler.setAttributeValueCaseChange(caseChange);
  }


  /** {@inheritDoc} */
  protected String processDn(final SearchCriteria sc, final SearchResult sr)
  {
    return CaseChange.perform(this.dnCaseChange, sr.getName());
  }
}
