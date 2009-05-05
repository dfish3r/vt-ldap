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
package edu.vt.middleware.ldap.props;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * <code>AbstractPropertyConfig</code> provides a base implementation of <code>
 * PropertyConfig</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractPropertyConfig implements PropertyConfig
{


  /** {@inheritDoc}. */
  public abstract String getPropertiesDomain();


  /** {@inheritDoc}. */
  public abstract void setEnvironmentProperties(
    final String name,
    final String value);


  /** {@inheritDoc}. */
  public void setEnvironmentProperties(final Properties properties)
  {
    if (properties != null) {
      final Enumeration<?> en = properties.keys();
      if (en != null) {
        while (en.hasMoreElements()) {
          final String name = (String) en.nextElement();
          final String value = (String) properties.get(name);
          this.setEnvironmentProperties(name, value);
        }
      }
    }
  }


  /**
   * See {@link #setEnvironmentProperties(String,String)}.
   *
   * @param  properties  <code>Hashtable</code> of environment properties
   */
  public void setEnvironmentProperties(
    final Hashtable<String, String> properties)
  {
    if (properties != null) {
      final Enumeration<String> en = properties.keys();
      if (en != null) {
        while (en.hasMoreElements()) {
          final String name = en.nextElement();
          final String value = properties.get(name);
          this.setEnvironmentProperties(name, value);
        }
      }
    }
  }


  /** {@inheritDoc}. */
  public abstract boolean hasEnvironmentProperty(final String name);


  /**
   * Verifies that a string is not null or empty.
   *
   * @param s to verify
   * @param allowNull whether null strings are valid
   * @throws IllegalArgumentException if the string is null or empty
   */
  protected void checkStringInput(final String s, final boolean allowNull)
  {
    if (allowNull) {
      if (s != null && s.equals("")) {
        throw new IllegalArgumentException("Input cannot be empty");
      }
    } else {
      if (s == null || s.equals("")) {
        throw new IllegalArgumentException("Input cannot be null or empty");
      }
    }
  }
}
