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
 * Contains the data required to perform an ldap compare operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class CompareRequest implements LdapRequest
{
  /** DN to compare. */
  protected String compareDn;

  /** Attribute to compare. */
  protected LdapAttribute attribute;


  /** Default constructor. */
  public CompareRequest() {}


  /**
   * Creates a new compare request.
   *
   * @param  dn  containing the attribute to compare
   * @param  la  attribute to compare
   */
  public CompareRequest(final String dn, final LdapAttribute la)
  {
    this.setDn(dn);
    this.setAttribute(la);
  }


  /**
   * Returns the DN to compare.
   *
   * @return  DN
   */
  public String getDn()
  {
    return this.compareDn;
  }


  /**
   * Sets the DN to compare.
   *
   * @param  dn  to compare
   */
  public void setDn(final String dn)
  {
    this.compareDn = dn;
  }


  /**
   * Returns the attribute containing the value to compare. If this attribute
   * contains multiple values, only the first value as return by the underlying
   * collection is used.
   *
   * @return  attribute to compare
   */
  public LdapAttribute getAttribute()
  {
    return this.attribute;
  }


  /**
   * Sets the attribute to compare.
   *
   * @param  la  attribute to compare
   */
  public void setAttribute(final LdapAttribute la)
  {
    this.attribute = la;
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
        "%s@%d: compareDn=%s, attribute=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.compareDn,
        this.attribute);
  }
}
