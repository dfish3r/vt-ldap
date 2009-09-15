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
package edu.vt.middleware.ldap;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

/**
 * <code>AttributesFactory</code> provides convenience methods for creating
 * <code>Attributes</code> and <code>Attribute</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AttributesFactory
{

  /** Default constructor. */
  private AttributesFactory() {}


  /**
   * Creates a new <code>Attributes</code> with the supplied name.
   * Attributes will be case-insensitive.
   *
   * @param  name of the attribute
   * @return  <code>Attributes</code>
   */
  public static Attributes createAttributes(final String name)
  {
    return createAttributes(name, LdapConstants.DEFAULT_IGNORE_CASE);
  }


  /**
   * Creates a new <code>Attributes</code> with the supplied name.
   *
   * @param  name of the attribute
   * @param  ignoreCase whether to ignore the case of attribute values
   * @return  <code>Attributes</code>
   */
  public static Attributes createAttributes(
    final String name, final boolean ignoreCase)
  {
    return createAttributes(name, null, ignoreCase);
  }


  /**
   * Creates a new <code>Attributes</code> with the supplied name and value.
   * Attributes will be case-insensitive.
   *
   * @param  name of the attribute
   * @param  value of the attribute
   * @return  <code>Attributes</code>
   */
  public static Attributes createAttributes(
    final String name, final Object value)
  {
    return createAttributes(name, value, LdapConstants.DEFAULT_IGNORE_CASE);
  }


  /**
   * Creates a new <code>Attributes</code> with the supplied name and value.
   *
   * @param  name of the attribute
   * @param  value of the attribute
   * @param  ignoreCase whether to ignore the case of attribute values
   * @return  <code>Attributes</code>
   */
  public static Attributes createAttributes(
    final String name, final Object value, final boolean ignoreCase)
  {
    if (value == null) {
      return createAttributes(name, null, ignoreCase);
    } else {
      return createAttributes(name, new Object[] {value}, ignoreCase);
    }
  }


  /**
   * Creates a new <code>Attributes</code> with the supplied name and values.
   * Attributes will be case-insensitive.
   *
   * @param  name of the attribute
   * @param  values of the attribute
   * @return  <code>Attributes</code>
   */
  public static Attributes createAttributes(
    final String name, final Object[] values)
  {
    return createAttributes(name, values, LdapConstants.DEFAULT_IGNORE_CASE);
  }


  /**
   * Creates a new <code>Attributes</code> with the supplied name and values.
   *
   * @param  name of the attribute
   * @param  values of the attribute
   * @param  ignoreCase whether to ignore the case of attribute values
   * @return  <code>Attributes</code>
   */
  public static Attributes createAttributes(
    final String name, final Object[] values, final boolean ignoreCase)
  {
    final Attributes attrs = new BasicAttributes(ignoreCase);
    attrs.put(createAttribute(name, values));
    return attrs;
  }


  /**
   * Creates a new <code>Attribute</code> with the supplied name.
   *
   * @param  name of the attribute
   */
  public static Attribute createAttribute(
    final Attributes attrs, final String name)
  {
    return createAttribute(name, null);
  }


  /**
   * Creates a new <code>Attribute</code> with the supplied name and value.
   *
   * @param  name of the attribute
   * @param  value of the attribute
   */
  public static Attribute createAttribute(final String name, final Object value)
  {
    if (value == null) {
      return createAttribute(name, null);
    } else {
      return createAttribute(name, new Object[] {value});
    }
  }


  /**
   * Creates a new <code>Attribute</code> with the supplied name and values.
   *
   * @param  name of the attribute
   * @param  values of the attribute
   */
  public static Attribute createAttribute(
    final String name, final Object[] values)
  {
    final Attribute attr = new BasicAttribute(name);
    if (values != null) {
      for (Object o : values) {
        attr.add(o);
      }
    }
    return attr;
  }
}
