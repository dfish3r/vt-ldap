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

import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import edu.vt.middleware.ldap.LdapUtil;

/**
 * <code>LdapAttribute</code> represents a single ldap attribute.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class LdapAttribute extends AbstractLdapBean
{

  /** hash code seed */
  protected static final int HASH_CODE_SEED = 41;

  /** Name for this attribute */
  private String name;

  /** Values for this attrbute */
  private List<Object> values = new ArrayList<Object>();


  /** Default constructor. */
  public LdapAttribute() {}


  /**
   * This creates a new <code>LdapAttribute</code> with the supplied <code>
   * LdapAttribute</code>.
   *
   * @param  la  <code>LdapAttribute</code>
   */
  public LdapAttribute(final LdapAttribute la)
  {
    this.setName(la.getName());
    this.values.addAll(la.getValues());
  }


  /**
   * This creates a new <code>LdapAttribute</code> with the supplied name and
   * value.
   *
   * @param  name  <code>String</code>
   * @param  value  <code>Object</code>
   */
  public LdapAttribute(final String name, final Object value)
  {
    this.setName(name);
    this.values.add(value);
  }


  /**
   * This creates a new <code>LdapAttribute</code> with the supplied name and
   * values.
   *
   * @param  name  <code>String</code>
   * @param  values  <code>List</code>
   */
  public LdapAttribute(final String name, final List<?> values)
  {
    this.setName(name);
    this.values.addAll(values);
  }


  /**
   * This creates a new <code>LdapAttribute</code> with the supplied attribute.
   *
   * @param  attribute  <code>Attribute</code>
   *
   * @throws  NamingException  if the attribute values cannot be read
   */
  public LdapAttribute(final Attribute attribute)
    throws NamingException
  {
    this.setAttribute(attribute);
  }


  /**
   * This returns the name of this <code>LdapAttribute</code>.
   *
   * @return  <code>String</code>
   */
  public String getName()
  {
    return this.name;
  }


  /**
   * This returns the value(s) of this <code>LdapAttribute</code>.
   *
   * @return  <code>List</code>
   */
  public List<Object> getValues()
  {
    return this.values;
  }


  /**
   * This returns the value(s) of this <code>LdapAttribute</code> Values are
   * encoded in base64 format if the underlying value is of type byte[].
   *
   * @return  <code>List</code>
   */
  public List<String> getStringValues()
  {
    final List<String> encodedValues = new ArrayList<String>();
    for (Object o : this.values) {
      encodedValues.add(LdapUtil.base64Encode(o));
    }
    return encodedValues;
  }


  /**
   * This sets this <code>LdapAttribute</code> using the supplied attribute.
   *
   * @param  attribute  <code>Attribute</code>
   *
   * @throws  NamingException  if the attribute values cannot be read
   */
  public void setAttribute(final Attribute attribute)
    throws NamingException
  {
    this.setName(attribute.getID());

    final NamingEnumeration<?> ne = attribute.getAll();
    while (ne.hasMore()) {
      this.values.add(ne.next());
    }
  }


  /**
   * This sets the name of this <code>LdapAttribute</code>.
   *
   * @param  name  <code>String</code>
   */
  public void setName(final String name)
  {
    this.name = name;
  }


  /** {@inheritDoc} */
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    if (this.name != null) {
      hc += this.name.hashCode();
    }
    for (Object o : this.values) {
      if (o != null) {
        hc += o.hashCode();
      }
    }
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
    sb.append(this.name).append(this.values);
    return sb.toString();
  }


  /**
   * This returns an <code>Attribute</code> that represents the values in this
   * <code>LdapAttribute</code>.
   *
   * @return  <code>Attribute</code>
   */
  public Attribute toAttribute()
  {
    final Attribute attribute = new BasicAttribute(this.name);
    for (Object o : this.values) {
      attribute.add(o);
    }
    return attribute;
  }
}
