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
import edu.vt.middleware.ldap.LdapUtil;

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

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 821;

  /** Type of case modification to make to the entry DN. */
  private CaseChange dnCaseChange = CaseChange.NONE;

  /** Attribute handler for case modifications. */
  private CaseChangeAttributeHandler caseChangeAttributeHandler =
    new CaseChangeAttributeHandler();


  /** Default constructor. */
  public CaseChangeResultHandler()
  {
    setAttributeHandlers(
      new LdapAttributeHandler[] {caseChangeAttributeHandler});
  }


  /**
   * Returns the DN case change.
   *
   * @return  case change
   */
  public CaseChange getDnCaseChange()
  {
    return dnCaseChange;
  }


  /**
   * Sets the DN case change.
   *
   * @param  cc  case change
   */
  public void setDnCaseChange(final CaseChange cc)
  {
    dnCaseChange = cc;
  }


  /**
   * Returns the attribute name case change.
   *
   * @return  case change
   */
  public CaseChange getAttributeNameCaseChange()
  {
    return caseChangeAttributeHandler.getAttributeNameCaseChange();
  }


  /**
   * Sets the attribute name case change.
   *
   * @param  cc  case change
   */
  public void setAttributeNameCaseChange(final CaseChange cc)
  {
    caseChangeAttributeHandler.setAttributeNameCaseChange(cc);
  }


  /**
   * Returns the attribute value case change.
   *
   * @return  case change
   */
  public CaseChange getAttributeValueCaseChange()
  {
    return caseChangeAttributeHandler.getAttributeValueCaseChange();
  }


  /**
   * Sets the attribute value case change.
   *
   * @param  cc  case change
   */
  public void setAttributeValueCaseChange(final CaseChange cc)
  {
    caseChangeAttributeHandler.setAttributeValueCaseChange(cc);
  }


  /** {@inheritDoc} */
  @Override
  protected String processDn(
    final SearchCriteria criteria, final LdapEntry entry)
  {
    return CaseChange.perform(dnCaseChange, entry.getDn());
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(
      HASH_CODE_SEED, dnCaseChange, getAttributeHandlers());
  }
}
