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
    ldapAttributes = new LdapAttributes(sb);
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  s  dn for this entry
   */
  public LdapEntry(final String s)
  {
    this();
    setDn(s);
  }


  /**
   * Creates a new ldap entry.
   *
   * @param  s  dn for this entry
   * @param  la  ldap attributes for this entry
   */
  public LdapEntry(final String s, final LdapAttributes la)
  {
    this();
    setDn(s);
    setLdapAttributes(la);
  }


  /**
   * Returns the DN.
   *
   * @return  entry DN
   */
  public String getDn()
  {
    return dn;
  }


  /**
   * Sets the DN.
   *
   * @param  s  dn to set
   */
  public void setDn(final String s)
  {
    dn = s;
  }


  /**
   * Returns the ldap attributes.
   *
   * @return  ldap attributes
   */
  public LdapAttributes getLdapAttributes()
  {
    return ldapAttributes;
  }


  /**
   * Sets the ldap attributes.
   *
   * @param  la  ldap attributes
   */
  public void setLdapAttributes(final LdapAttributes la)
  {
    ldapAttributes = la;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    hc += dn != null ? dn.hashCode() : 0;
    hc += ldapAttributes != null ? ldapAttributes.hashCode() : 0;
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
    return String.format("dn=>%s%s", dn, ldapAttributes);
  }
}
