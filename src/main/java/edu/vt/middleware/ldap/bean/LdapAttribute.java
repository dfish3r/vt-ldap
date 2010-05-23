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

import java.util.Set;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

/**
 * <code>LdapAttribute</code> represents a single ldap attribute. Ldap attribute
 * values must be unique per http://tools.ietf.org/html/rfc4512#section-2.3. For
 * any given attribute, the values must all be of the same type.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapAttribute
{


  /**
   * This returns the name of this <code>LdapAttribute</code>.
   *
   * @return  <code>String</code>
   */
  String getName();


  /**
   * This returns the value(s) of this <code>LdapAttribute</code>.
   *
   * @return  <code>Set</code>
   */
  Set<Object> getValues();


  /**
   * This returns the value(s) of this <code>LdapAttribute</code> Values are
   * encoded in base64 format if the underlying value is of type byte[]. The
   * returned set is unmodifiable.
   *
   * @return  unmodifiable <code>Set</code>
   */
  Set<String> getStringValues();


  /**
   * This sets this <code>LdapAttribute</code> using the supplied attribute.
   *
   * @param  attribute  <code>Attribute</code>
   *
   * @throws  NamingException  if the attribute values cannot be read
   */
  void setAttribute(final Attribute attribute)
    throws NamingException;


  /**
   * This sets the name of this <code>LdapAttribute</code>.
   *
   * @param  name  <code>String</code>
   */
  void setName(final String name);


  /**
   * This returns an <code>Attribute</code> that represents the values in this
   * <code>LdapAttribute</code>.
   *
   * @return  <code>Attribute</code>
   */
  Attribute toAttribute();
}
