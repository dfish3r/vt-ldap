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

import edu.vt.middleware.ldap.provider.Provider;

/**
 * Handles properties for {@link edu.vt.middleware.ldap.ConnectionFactory}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConnectionFactoryPropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new connection factory property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public ConnectionFactoryPropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (Provider.class.isAssignableFrom(type)) {
        newValue = createTypeFromPropertyValue(
          Provider.class,
          value);
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
