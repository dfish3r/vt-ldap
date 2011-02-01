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
 * Contains the data required to perform an ldap add operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class AddRequest implements LdapRequest
{
  /** DN to create. */
  protected String createDn;

  /** Attributes to add to the newly created entry. */
  protected LdapAttributes attributes;


  /** Default constructor. */
  public AddRequest() {}


  /**
   * Creates a new add request.
   *
   * @param  dn  to create
   * @param  la  attributes to add
   */
  public AddRequest(final String dn, final LdapAttributes la)
  {
    this.setDn(dn);
    this.setLdapAttributes(la);
  }


  /**
   * Returns the DN to create.
   *
   * @return  DN
   */
  public String getDn()
  {
    return this.createDn;
  }


  /**
   * Sets the DN to create.
   *
   * @param  dn  to create
   */
  public void setDn(final String dn)
  {
    this.createDn = dn;
  }


  /**
   * Returns the attributes to add.
   *
   * @return  attributes
   */
  public LdapAttributes getLdapAttributes()
  {
    return this.attributes;
  }


  /**
   * Sets the attributes to add.
   *
   * @param  la  to add
   */
  public void setLdapAttributes(final LdapAttributes la)
  {
    this.attributes = la;
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
        "%s@%d: createDn=%s, attributes=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.createDn,
        this.attributes);
  }
}