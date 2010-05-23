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
package edu.vt.middleware.ldap.handler;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

/**
 * <code>EntryDnSearchResultHandler</code> adds the search result DN as an
 * attribute to the result set. Provides a client side implementation of RFC
 * 5020.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class EntryDnSearchResultHandler extends CopySearchResultHandler
{

  /**
   * Attribute name for the entry dn. The value of this constant is {@value}.
   */
  private String dnAttributeName = "entryDN";

  /**
   * Whether to add the entry dn if an attribute of the same name exists. The
   * value of this constant is {@value}.
   */
  private boolean addIfExists;


  /**
   * Returns the DN attribute name.
   *
   * @return  <code>String</code>
   */
  public String getDnAttributeName()
  {
    return this.dnAttributeName;
  }


  /**
   * Sets the DN attribute name.
   *
   * @param  s  <code>String</code>
   */
  public void setDnAttributeName(final String s)
  {
    this.dnAttributeName = s;
  }


  /**
   * Returns whether to add the entryDN if an attribute of the same name exists.
   *
   * @return  <code>boolean</code>
   */
  public boolean isAddIfExists()
  {
    return this.addIfExists;
  }


  /**
   * Sets whether to add the entryDN if an attribute of the same name exists.
   *
   * @param  b  <code>boolean</code>
   */
  public void setAddIfExists(final boolean b)
  {
    this.addIfExists = b;
  }


  /** {@inheritDoc} */
  protected Attributes processAttributes(
    final SearchCriteria sc,
    final SearchResult sr)
    throws NamingException
  {
    final Attributes newAttrs = sr.getAttributes();
    if (newAttrs.get(this.dnAttributeName) == null) {
      newAttrs.put(this.dnAttributeName, sr.getName());
    } else if (this.addIfExists) {
      newAttrs.get(this.dnAttributeName).add(sr.getName());
    }
    return newAttrs;
  }
}
