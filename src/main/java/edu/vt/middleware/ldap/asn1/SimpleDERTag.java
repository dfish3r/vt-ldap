/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.asn1;

/**
 * Describe the tag of an arbitrary application-specific or private DER type.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SimpleDERTag implements DERTag
{

  /** Tag number. */
  private int tagNo;

  /** Tag name. */
  private String tagName;

  /** Flag indicating whether value is primitive or constructed. */
  private boolean constructed;


  /**
   * Creates a new simple DER tag.
   *
   * @param  number  of the tag
   * @param  name  of the tag
   * @param  isConstructed  whether this tag is primitive or constructed
   */
  public SimpleDERTag(
    final int number,
    final String name,
    final boolean isConstructed)
  {
    tagNo = number;
    tagName = name;
    constructed = isConstructed;
  }


  /** {@inheritDoc} */
  @Override
  public int getTagNo()
  {
    return tagNo;
  }


  /** {@inheritDoc} */
  @Override
  public String name()
  {
    return tagName;
  }


  /** {@inheritDoc} */
  @Override
  public boolean isConstructed()
  {
    return constructed;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return String.format("%s(%s)", tagName, tagNo);
  }
}
