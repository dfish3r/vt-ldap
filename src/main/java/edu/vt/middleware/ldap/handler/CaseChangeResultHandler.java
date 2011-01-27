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

import edu.vt.middleware.ldap.LdapEntry;

/**
 * Provides the ability to modify the case of ldap search result DNs, attribute
 * names, and attribute values.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class CaseChangeResultHandler extends CopyLdapResultHandler
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
     * @param  cc  case change to perform
     * @param  string  to modify
     *
     * @return  string that has been changed
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


  /** Default constructor. */
  public CaseChangeResultHandler()
  {
    this.setAttributeHandler(
      new LdapAttributeHandler[] {this.attributeHandler});
  }


  /**
   * Returns the DN case change.
   *
   * @return  case change
   */
  public CaseChange getDnCaseChange()
  {
    return this.dnCaseChange;
  }


  /**
   * Sets the DN case change.
   *
   * @param  cc  case change
   */
  public void setDnCaseChange(final CaseChange cc)
  {
    this.dnCaseChange = cc;
  }


  /**
   * Returns the attribute name case change.
   *
   * @return  case change
   */
  public CaseChange getAttributeNameCaseChange()
  {
    return this.attributeHandler.getAttributeNameCaseChange();
  }


  /**
   * Sets the attribute name case change.
   *
   * @param  cc  case change
   */
  public void setAttributeNameCaseChange(final CaseChange cc)
  {
    this.attributeHandler.setAttributeNameCaseChange(cc);
  }


  /**
   * Returns the attribute value case change.
   *
   * @return  case change
   */
  public CaseChange getAttributeValueCaseChange()
  {
    return this.attributeHandler.getAttributeValueCaseChange();
  }


  /**
   * Sets the attribute value case change.
   *
   * @param  cc  case change
   */
  public void setAttributeValueCaseChange(final CaseChange cc)
  {
    this.attributeHandler.setAttributeValueCaseChange(cc);
  }


  /** {@inheritDoc} */
  protected String processDn(final SearchCriteria sc, final LdapEntry le)
  {
    return CaseChange.perform(this.dnCaseChange, le.getDn());
  }
}
