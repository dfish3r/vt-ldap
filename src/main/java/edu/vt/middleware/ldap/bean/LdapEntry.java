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
package edu.vt.middleware.ldap.bean;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

/**
 * <code>LdapEntry</code> represents a single ldap entry.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapEntry
{


  /**
   * This returns the DN for this <code>LdapEntry</code>.
   *
   * @return  <code>String</code>
   */
  String getDn();


  /**
   * This returns the <code>LdapAttributes</code> for this <code>
   * LdapEntry</code>.
   *
   * @return  <code>LdapAttributes</code>
   */
  LdapAttributes getLdapAttributes();


  /**
   * This sets this <code>LdapEntry</code> with the supplied search result.
   *
   * @param  sr  <code>SearchResult</code>
   *
   * @throws  NamingException  if the search result cannot be read
   */
  void setEntry(final SearchResult sr)
    throws NamingException;


  /**
   * This sets the DN for this <code>LdapEntry</code>.
   *
   * @param  dn  <code>String</code>
   */
  void setDn(final String dn);


  /**
   * This sets the attributes for this <code>LdapEntry</code>.
   *
   * @param  a  <code>LdapAttribute</code>
   */
  void setLdapAttributes(final LdapAttributes a);


  /**
   * This returns a <code>SearchResult</code> that represents this entry.
   *
   * @return  <code>SearchResult</code>
   */
  SearchResult toSearchResult();
}
