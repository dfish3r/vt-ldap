/*
  $Id$

  Copyright (C) 2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.asn1;

/**
 * Describes the tag of a DER-encoded type.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface DERTag
{


  /**
   * Gets the decimal value of the tag.
   *
   * @return  decimal tag number.
   */
  int getTagNo();


  /**
   * Gets the name of the tag.
   *
   * @return  tag name.
   */
  String name();


  /**
   * Determines whether the tag is constructed or primitive.
   *
   * @return  true if constructed, false if primitive.
   */
  boolean isConstructed();
}
