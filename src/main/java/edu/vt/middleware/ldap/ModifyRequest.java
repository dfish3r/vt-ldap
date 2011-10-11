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

import java.util.Arrays;

/**
 * Contains the data required to perform an ldap modify operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class ModifyRequest extends AbstractRequest
{

  /** DN to modify. */
  protected String modifyDn;

  /** Attribute modifications. */
  protected AttributeModification[] attrMods;


  /** Default constructor. */
  public ModifyRequest() {}


  /**
   * Creates a new modify request.
   *
   * @param  dn  to modify
   * @param  mod  attribute modification
   */
  public ModifyRequest(final String dn, final AttributeModification mod)
  {
    setDn(dn);
    setAttributeModifications(new AttributeModification[] {mod});
  }


  /**
   * Creates a new modify request.
   *
   * @param  dn  to modify
   * @param  mods  attribute modifications
   */
  public ModifyRequest(final String dn, final AttributeModification[] mods)
  {
    setDn(dn);
    setAttributeModifications(mods);
  }


  /**
   * Returns the DN to modify.
   *
   * @return  DN
   */
  public String getDn()
  {
    return modifyDn;
  }


  /**
   * Sets the DN to modify.
   *
   * @param  dn  to modify
   */
  public void setDn(final String dn)
  {
    modifyDn = dn;
  }


  /**
   * Returns the attribute modifications.
   *
   * @return  attribute modifications
   */
  public AttributeModification[] getAttributeModifications()
  {
    return attrMods;
  }


  /**
   * Sets the attribute modifications.
   *
   * @param  mods  attribute modifications
   */
  public void setAttributeModifications(final AttributeModification[] mods)
  {
    attrMods = mods;
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
        "[%s@%d::modifyDn=%s, attrMods=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        modifyDn,
        attrMods != null ? Arrays.asList(attrMods) : null,
        getControls() != null ? Arrays.asList(getControls()) : null);
  }
}
