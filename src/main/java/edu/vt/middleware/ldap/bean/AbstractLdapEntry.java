/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
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
 * <code>AbstractLdapEntry</code> provides a base implementation of
 * <code>LdapEntry</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractLdapEntry extends AbstractLdapBean
  implements LdapEntry
{

  /** hash code seed. */
  protected static final int HASH_CODE_SEED = 43;

  /** Distinguished name for this entry. */
  protected String dn;

  /** Attributes contained in this entry. */
  protected LdapAttributes ldapAttributes;


  /**
   * Creates a new <code>AbstractLdapEntry</code> with the supplied ldap bean
   * factory.
   *
   * @param  lbf  <code>LdapBeanFactory</code>
   */
  public AbstractLdapEntry(final LdapBeanFactory lbf)
  {
    super(lbf);
  }


  /** {@inheritDoc} */
  public String getDn()
  {
    return this.dn;
  }


  /** {@inheritDoc} */
  public LdapAttributes getLdapAttributes()
  {
    return this.ldapAttributes;
  }


  /** {@inheritDoc} */
  public void setEntry(final SearchResult sr)
    throws NamingException
  {
    this.setDn(sr.getName());
    final LdapAttributes la = this.beanFactory.newLdapAttributes();
    la.addAttributes(sr.getAttributes());
    this.setLdapAttributes(la);
  }


  /** {@inheritDoc} */
  public void setDn(final String dn)
  {
    this.dn = dn;
  }


  /** {@inheritDoc} */
  public void setLdapAttributes(final LdapAttributes a)
  {
    if (a != null) {
      this.ldapAttributes = a;
    }
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
   * This returns a string representation of this object.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return String.format("dn=>%s%s", this.dn, this.ldapAttributes);
  }


  /** {@inheritDoc} */
  public SearchResult toSearchResult()
  {
    return new SearchResult(this.dn, null, this.ldapAttributes.toAttributes());
  }
}
