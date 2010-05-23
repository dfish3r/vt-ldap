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
      if (String[].class == type) {
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
