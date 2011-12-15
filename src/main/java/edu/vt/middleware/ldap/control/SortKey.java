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

import edu.vt.middleware.ldap.LdapUtil;

/**
 * Used by {@link SortRequestControl} to declare how sorting should occur.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SortKey
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 739;

  /** attribute description. */
  private String attributeDescription;

  /** matching rule id. */
  private String matchingRuleId;

  /** reverse order. */
  private boolean reverseOrder;


  /**
   * Default constructor.
   */
  public SortKey() {}


  /**
   * Creates a new sort key.
   *
   * @param  attrDescription  attribute description
   */
  public SortKey(final String attrDescription)
  {
    setAttributeDescription(attrDescription);
  }


  /**
   * Creates a new sort key.
   *
   * @param  attrDescription  attribute description
   * @param  ruleId  matching rule id
   */
  public SortKey(final String attrDescription, final String ruleId)
  {
    setAttributeDescription(attrDescription);
    setMatchingRuleId(ruleId);
  }


  /**
   * Creates a new sort key.
   *
   * @param  attrDescription  attribute description
   * @param  ruleId  matching rule id
   * @param  reverse  reverse order
   */
  public SortKey(
    final String attrDescription,
    final String ruleId,
    final boolean reverse)
  {
    setAttributeDescription(attrDescription);
    setMatchingRuleId(ruleId);
    setReverseOrder(reverse);
  }


  /**
   * Returns the attribute description.
   *
   * @return  attribute description
   */
  public String getAttributeDescription()
  {
    return attributeDescription;
  }


  /**
   * Sets the attribute description
   *
   * @param  s  attribute description
   */
  public void setAttributeDescription(final String s)
  {
    attributeDescription = s;
  }


  /**
   * Returns the matching rule id.
   *
   * @return  matching rule id
   */
  public String getMatchingRuleId()
  {
    return matchingRuleId;
  }


  /**
   * Sets the matching rule id.
   *
   * @param  s  matching rule id
   */
  public void setMatchingRuleId(final String s)
  {
    matchingRuleId = s;
  }


  /**
   * Returns whether results should be in reverse sorted order.
   *
   * @return  whether results should be in reverse sorted order
   */
  public boolean getReverseOrder()
  {
    return reverseOrder;
  }


  /**
   * Sets whether results should be in reverse sorted order.
   *
   * @param  b  whether results should be in reverse sorted order
   */
  public void setReverseOrder(final boolean b)
  {
    reverseOrder = b;
  }


  /**
   * Returns whether the supplied object contains the same data as this control.
   * Delegates to {@link #hashCode()} implementation.
   *
   * @param  o  to compare for equality
   *
   * @return  equality result
   */
  @Override
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this ||
        (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(
      HASH_CODE_SEED, attributeDescription, matchingRuleId, reverseOrder);
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
        "[%s@%d::attributeDescription=%s, matchingRuleId=%s, reverseOrder=%s]",
        getClass().getName(),
        hashCode(),
        attributeDescription,
        matchingRuleId,
        reverseOrder);
  }
}
