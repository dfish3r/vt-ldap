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

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration with common BER/DER universal tag types.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public enum UniversalDERTag implements DERTag
{

  /** BOOLEAN type */
  BOOL(1, false),

  /** INTEGER type */
  INT(2, false),

  /** BITSTRING type */
  BITSTR(3, false),

  /** OCTETSTRING type */
  OCTSTR(4, false),

  /** OBJECT IDENTIFIER type */
  OID(6, false),

  /** ENUMERATED type */
  ENUM(10, false),

  /** SEQUENCE type */
  SEQ(16, true),

  /** SET type */
  SET(17, true),

  /** CONTEXT type */
  CTX(128, false);


  /** Maps tag values to tags. */
  private static final Map<Integer, UniversalDERTag> TAG_MAP =
      new HashMap<Integer, UniversalDERTag>();

  /** Tag number */
  private int tagNo;

  /** Flag indicating whether value is primitive or constructed. */
  private boolean constructed;


  /** Initializes tag mapping. */
  static
  {
    for (UniversalDERTag tag : UniversalDERTag.values()) {
      TAG_MAP.put(tag.getTagNo(), tag);
    }
  }


  /**
   * Creates a new universal DER tag.
   *
   * @param  number  of the tag
   * @param  isConstructed  whether this tag is primitive or constructed
   */
  UniversalDERTag(final int number, final boolean isConstructed)
  {
    tagNo = number;
    constructed = isConstructed;
  }


  /**
   * Gets the decimal value of the tag.
   *
   * @return  decimal tag number.
   */
  public int getTagNo()
  {
    return tagNo;
  }


  /**
   * Determines whether the tag is constructed or primitive.
   *
   * @return  true if constructed, false if primitive.
   */
  public boolean isConstructed()
  {
    return constructed;
  }


  /**
   * Looks up a tag object from a tag number.
   *
   * @param  tag  tag number.
   *
   * @return  tag object corresponding to given tag.
   *
   * @throws  IllegalArgumentException  if tag is unknown
   */
  public static UniversalDERTag fromTagNo(final int tag)
  {
    final UniversalDERTag derTag = TAG_MAP.get(tag);
    if (derTag == null) {
      throw new IllegalArgumentException("Unknown tag " + tag);
    }
    return derTag;
  }
}
