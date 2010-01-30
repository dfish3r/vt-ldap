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

import java.util.Collection;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * <code>LdapAttributes</code> represents a collection of ldap attribute.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapAttributes
{


  /**
   * This returns a <code>Collection</code> of <code>LdapAttribute</code> for
   * this <code>LdapAttributes</code>.
   *
   * @return  <code>List</code>
   */
  Collection<LdapAttribute> getAttributes();


  /**
   * This returns the <code>LdapAttribute</code> for this <code>
   * LdapAttributes</code> with the supplied name.
   *
   * @param  name  <code>String</code>
   *
   * @return  <code>LdapAttribute</code>
   */
  LdapAttribute getAttribute(final String name);


  /**
   * This returns an array of all the attribute names for this <code>
   * LdapAttributes</code>.
   *
   * @return  <code>String[]</code>
   */
  String[] getAttributeNames();


  /**
   * This adds a new attribute to this <code>LdapAttributes</code>.
   *
   * @param  a  <code>LdapAttribute</code>
   */
  void addAttribute(final LdapAttribute a);


  /**
   * This adds a new attribute to this <code>LdapAttributes</code> with the
   * supplied name and value.
   *
   * @param  name  <code>String</code>
   * @param  value  <code>Object</code>
   */
  void addAttribute(final String name, final Object value);


  /**
   * This adds a new attribute to this <code>LdapAttributes</code> with the
   * supplied name and values.
   *
   * @param  name  <code>String</code>
   * @param  values  <code>List</code>
   */
  void addAttribute(final String name, final List<?> values);


  /**
   * This adds a <code>Collection</code> of attributes to this <code>
   * LdapAttributes</code>. The collection should contain <code>
   * LdapAttribute</code> objects.
   *
   * @param  c  <code>Collection</code>
   */
  void addAttributes(final Collection<LdapAttribute> c);


  /**
   * This adds the attributes in the supplied <code>Attributes</code> to this
   * <code>LdapAttributes</code>.
   *
   * @param  a  <code>Attributes</code>
   *
   * @throws  NamingException  if the attributes cannot be read
   */
  void addAttributes(final Attributes a)
    throws NamingException;


  /**
   * This removes an attribute from this <code>LdapAttributes</code>.
   *
   * @param  a  <code>LdapAttribute</code>
   */
  void removeAttribute(final LdapAttribute a);


  /**
   * This removes the attribute with the supplied name.
   *
   * @param  name  <code>String</code>
   */
  void removeAttribute(final String name);


  /**
   * This removes a <code>Collection</code> of attributes from this <code>
   * LdapAttributes</code>. The collection should contain <code>
   * LdapAttribute</code> objects.
   *
   * @param  c  <code>Collection</code>
   */
  void removeAttributes(final Collection<LdapAttribute> c);


  /**
   * This removes the attributes in the supplied <code>Attributes</code> from
   * this <code>LdapAttributes</code>.
   *
   * @param  a  <code>Attributes</code>
   *
   * @throws  NamingException  if the attributes cannot be read
   */
  void removeAttributes(final Attributes a)
    throws NamingException;


  /**
   * This returns the number of attributes in this attributes.
   *
   * @return  <code>int</code>
   */
  int size();


  /** This removes all attributes from this <code>LdapAttributes</code>. */
  void clear();


  /**
   * This returns an <code>Attributes</code> that represents this entry.
   *
   * @return  <code>Attributes</code>
   */
  Attributes toAttributes();
}
