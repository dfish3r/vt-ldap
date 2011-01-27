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
 * Simple bean for an ldap entry. Contains a DN and ldap attributes.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdapEntry extends AbstractLdapBean
{
  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 43;

  /** Distinguished name for this entry. */
  protected String dn;

  /** Attributes contained in this entry. */
  protected LdapAttributes ldapAttributes;


  /** Default constructor. */
  public LdapEntry()
  {
    this(SortBehavior.getDefaultSortBehavior());
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  sb  sort behavior
   */
  public LdapEntry(final SortBehavior sb)
  {
    super(sb);
    this.ldapAttributes = new LdapAttributes(sb);
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  dn  for this entry
   */
  public LdapEntry(final String dn)
  {
    this();
    this.setDn(dn);
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  dn  for this entry
   * @param  la  ldap attributes for this entry
   */
  public LdapEntry(final String dn, final LdapAttributes la)
  {
    this();
    this.setDn(dn);
    this.setLdapAttributes(la);
  }


  /**
   * Returns the DN.
   *
   * @return  entry DN
   */
  public String getDn()
  {
    return this.dn;
  }


  /**
   * Sets the DN.
   *
   * @param  dn  to set
   */
  public void setDn(final String dn)
  {
    this.dn = dn;
  }


  /**
   * Returns the ldap attributes.
   *
   * @return  ldap attributes
   */
  public LdapAttributes getLdapAttributes()
  {
    return this.ldapAttributes;
  }


  /**
   * Sets the ldap attributes.
   *
   * @param  la  ldap attributes
   */
  public void setLdapAttributes(final LdapAttributes la)
  {
    this.ldapAttributes = la;
  }


  /** {@inheritDoc} */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    if (this.getDn() != null) {
      hc += this.getDn().hashCode();
    }
    hc += this.getLdapAttributes().hashCode();
    return hc;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return String.format("dn=>%s%s", this.dn, this.ldapAttributes);
  }
}
