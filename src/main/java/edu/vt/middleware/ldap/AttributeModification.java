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
package edu.vt.middleware.ldap;

/**
 * Contains the data required to modify an ldap attribute.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class AttributeModification
{
  /** Type of modification to perform. */
  private AttributeModificationType attrMod;

  /** Attribute to modify. */
  private LdapAttribute attribute;

  /** Default constructor. */
  public AttributeModification() {}


  /**
   * Creates a new attribute modification.
   *
   * @param  mod  attribute modification type
   * @param  attr  to modify
   */
  public AttributeModification(
    final AttributeModificationType mod, final LdapAttribute attr)
  {
    setAttributeModificationType(mod);
    setAttribute(attr);
  }


  /**
   * Returns the attribute modification type.
   *
   * @return  attribute modification type
   */
  public AttributeModificationType getAttributeModificationType()
  {
    return attrMod;
  }


  /**
   * Sets the attribute modification type.
   *
   * @param  amt  attribute modification type
   */
  public void setAttributeModificationType(final AttributeModificationType amt)
  {
    attrMod = amt;
  }


  /**
   * Returns the ldap attribute.
   *
   * @return  ldap attribute
   */
  public LdapAttribute getAttribute()
  {
    return attribute;
  }


  /**
   * Sets the ldap attribute.
   *
   * @param  attr  ldap attribute
   */
  public void setAttribute(final LdapAttribute attr)
  {
    attribute = attr;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  String of the form $Classname@hashCode::propName=propValue.
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "%s@%d::attrMod=%s, attribute=%s",
        getClass().getName(),
        hashCode(),
        attrMod,
        attribute);
  }
}
