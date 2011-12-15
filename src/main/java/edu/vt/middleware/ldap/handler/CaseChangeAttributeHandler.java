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

import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.handler.CaseChangeResultHandler.CaseChange;

/**
 * Provides the ability to modify the case of attribute names and attribute
 * values.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class CaseChangeAttributeHandler extends CopyLdapAttributeHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 811;

  /** Type of case modification to make to the attribute names. */
  private CaseChange attributeNameCaseChange = CaseChange.NONE;

  /** Type of case modification to make to the attributes values. */
  private CaseChange attributeValueCaseChange = CaseChange.NONE;


  /**
   * Returns the attribute name case change.
   *
   * @return  case change
   */
  public CaseChange getAttributeNameCaseChange()
  {
    return attributeNameCaseChange;
  }


  /**
   * Sets the attribute name case change.
   *
   * @param  cc  case change
   */
  public void setAttributeNameCaseChange(final CaseChange cc)
  {
    attributeNameCaseChange = cc;
  }


  /**
   * Returns the attribute value case change.
   *
   * @return  case change
   */
  public CaseChange getAttributeValueCaseChange()
  {
    return attributeValueCaseChange;
  }


  /**
   * Sets the attribute value case change.
   *
   * @param  cc  case change
   */
  public void setAttributeValueCaseChange(final CaseChange cc)
  {
    attributeValueCaseChange = cc;
  }


  /** {@inheritDoc} */
  @Override
  protected String processName(final SearchCriteria sc, final String name)
  {
    return CaseChange.perform(attributeNameCaseChange, name);
  }


  /** {@inheritDoc} */
  @Override
  protected String processValue(final SearchCriteria sc, final String value)
  {
    return CaseChange.perform(attributeValueCaseChange, value);
  }


  /** {@inheritDoc} */
  @Override
  protected byte[] processValue(final SearchCriteria sc, final byte[] value)
  {
    return value;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(
      HASH_CODE_SEED, attributeNameCaseChange, attributeValueCaseChange);
  }
}
