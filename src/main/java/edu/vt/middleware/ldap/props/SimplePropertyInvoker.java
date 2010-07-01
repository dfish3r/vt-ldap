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
package edu.vt.middleware.ldap.props;

import java.lang.reflect.Array;

/**
 * <code>SimplePropertyInvoker</code> stores setter methods for a class to make
 * method invocation of simple properties easier.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SimplePropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new <code>SimplePropertyInvoker</code> for the supplied class.
   *
   * @param  c  <code>Class</code> that has setter methods
   */
  public SimplePropertyInvoker(final Class<?> c)
  {
    this.initialize(c, "");
  }


  /** {@inheritDoc} */
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (Class.class.isAssignableFrom(type)) {
        if ("null".equals(value)) {
          newValue = null;
        } else {
          newValue = createClass(value);
        }
      } else if (Class[].class.isAssignableFrom(type)) {
        if ("null".equals(value)) {
          newValue = null;
        } else {
          final String[] classes = value.split(",");
          newValue = Array.newInstance(Class.class, classes.length);
          for (int i = 0; i < classes.length; i++) {
            Array.set(newValue, i, createClass(classes[i]));
          }
        }
      } else if (type.isEnum()) {
        for (Object o : type.getEnumConstants()) {
          final Enum<?> e = (Enum<?>) o;
          if (e.name().equals(value)) {
            newValue = o;
          }
        }
      } else if (String[].class == type) {
        newValue = value.split(",");
      } else if (Object[].class == type) {
        newValue = value.split(",");
      } else if (float.class == type) {
        newValue = Float.parseFloat(value);
      } else if (int.class == type) {
        newValue = Integer.parseInt(value);
      } else if (long.class == type) {
        newValue = Long.parseLong(value);
      } else if (short.class == type) {
        newValue = Short.parseShort(value);
      } else if (double.class == type) {
        newValue = Double.parseDouble(value);
      } else if (boolean.class == type) {
        newValue = Boolean.valueOf(value);
      }
    }
    return newValue;
  }
}
