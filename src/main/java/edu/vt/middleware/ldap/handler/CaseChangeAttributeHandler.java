/*
  $Id: BinaryAttributeHandler.java 1330 2010-05-23 22:10:53Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1330 $
  Updated: $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
*/
package edu.vt.middleware.ldap.handler;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import edu.vt.middleware.ldap.handler.CaseChangeSearchResultHandler.CaseChange;

/**
 * <code>CaseChangeAttributeHandler</code> provides the ability to modify the
 * case of attribute names and attribute values.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class CaseChangeAttributeHandler extends CopyAttributeHandler
{

  /** Type of case modification to make to the attribute names. */
  private CaseChange attributeNameCaseChange = CaseChange.NONE;

  /** Type of case modification to make to the attributes values. */
  private CaseChange attributeValueCaseChange = CaseChange.NONE;


  /**
   * Returns the attribute name case change.
   *
   * @return  <code>CaseChange</code>
   */
  public CaseChange getAttributeNameCaseChange()
  {
    return this.attributeNameCaseChange;
  }


  /**
   * Sets the attribute name case change.
   *
   * @param  caseChange  <code>CaseChange</code>
   */
  public void setAttributeNameCaseChange(final CaseChange caseChange)
  {
    this.attributeNameCaseChange = caseChange;
  }


  /**
   * Returns the attribute value case change.
   *
   * @return  <code>CaseChange</code>
   */
  public CaseChange getAttributeValueCaseChange()
  {
    return this.attributeValueCaseChange;
  }


  /**
   * Sets the attribute value case change.
   *
   * @param  caseChange  <code>CaseChange</code>
   */
  public void setAttributeValueCaseChange(final CaseChange caseChange)
  {
    this.attributeValueCaseChange = caseChange;
  }


  /** {@inheritDoc} */
  protected Attribute processResult(
    final SearchCriteria sc,
    final Attribute attr)
    throws NamingException
  {
    Attribute newAttr = null;
    if (attr != null) {
      newAttr = new BasicAttribute(
        CaseChange.perform(this.attributeNameCaseChange, attr.getID()),
        attr.isOrdered());

      final NamingEnumeration<?> en = attr.getAll();
      while (en.hasMore()) {
        newAttr.add(this.processValue(sc, en.next()));
      }
    }
    return newAttr;
  }


  /** {@inheritDoc} */
  protected Object processValue(final SearchCriteria sc, final Object value)
  {
    if (value instanceof String) {
      return CaseChange.perform(this.attributeValueCaseChange, (String) value);
    } else {
      return value;
    }
  }
}
