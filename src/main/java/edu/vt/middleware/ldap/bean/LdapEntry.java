/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.bean;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

/**
 * <code>LdapEntry</code> represents a single ldap entry.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class LdapEntry extends AbstractLdapBean
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 43;

  /** Distinguished name for this entry. */
  private String dn;

  /** Attributes contained in this entry. */
  private LdapAttributes ldapAttributes = new LdapAttributes();


  /** Default constructor. */
  public LdapEntry() {}


  /**
   * This creates a new <code>LdapEntry</code> with the supplied <code>
   * LdapEntry</code>.
   *
   * @param  le  <code>LdapEntry</code>
   */
  public LdapEntry(final LdapEntry le)
  {
    this.setDn(le.getDn());
    this.setLdapAttributes(le.getLdapAttributes());
  }


  /**
   * This creates a new <code>LdapEntry</code> with the supplied DN and
   * attributes.
   *
   * @param  dn  <code>String</code>
   * @param  la  <code>LdapAttributes</code>
   */
  public LdapEntry(final String dn, final LdapAttributes la)
  {
    this.setDn(dn);
    this.setLdapAttributes(la);
  }


  /**
   * This creates a new <code>LdapEntry</code> with the supplied search result.
   *
   * @param  sr  <code>SearchResult</code>
   *
   * @throws  NamingException  if the search result cannot be read
   */
  public LdapEntry(final SearchResult sr)
    throws NamingException
  {
    this.setEntry(sr);
  }


  /**
   * This returns the DN for this <code>LdapEntry</code>.
   *
   * @return  <code>String</code>
   */
  public String getDn()
  {
    return this.dn;
  }


  /**
   * This returns the <code>LdapAttributes</code> for this <code>
   * LdapEntry</code>.
   *
   * @return  <code>LdapAttributes</code>
   */
  public LdapAttributes getLdapAttributes()
  {
    return this.ldapAttributes;
  }


  /**
   * This sets this <code>LdapEntry</code> with the supplied search result.
   *
   * @param  sr  <code>SearchResult</code>
   *
   * @throws  NamingException  if the search result cannot be read
   */
  public void setEntry(final SearchResult sr)
    throws NamingException
  {
    this.setDn(sr.getName());
    this.setLdapAttributes(new LdapAttributes(sr.getAttributes()));
  }


  /**
   * This sets the DN for this <code>LdapEntry</code>.
   *
   * @param  dn  <code>String</code>
   */
  public void setDn(final String dn)
  {
    this.dn = dn;
  }


  /**
   * This sets the attributes for this <code>LdapEntry</code>.
   *
   * @param  a  <code>LdapAttribute</code>
   */
  public void setLdapAttributes(final LdapAttributes a)
  {
    if (a != null) {
      this.ldapAttributes = a;
    }
  }


  /** {@inheritDoc}. */
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
   * This returns a string representation of this object.
   *
   * @return  <code>String</code>
   */
  public String toString()
  {
    final StringBuffer sb = new StringBuffer();
    sb.append("dn=>").append(this.dn);
    sb.append(this.ldapAttributes.toString());
    return sb.toString();
  }


  /**
   * This returns a <code>SearchResult</code> that represents this entry.
   *
   * @return  <code>SearchResult</code>
   */
  public SearchResult toSearchResult()
  {
    return new SearchResult(this.dn, null, this.ldapAttributes.toAttributes());
  }
}
