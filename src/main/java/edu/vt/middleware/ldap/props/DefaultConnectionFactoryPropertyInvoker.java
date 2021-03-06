/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
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
 * Handles properties for {@link
 * edu.vt.middleware.ldap.DefaultConnectionFactory}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultConnectionFactoryPropertyInvoker
  extends AbstractPropertyInvoker
{


  /**
   * Creates a new default connection factory property invoker for the supplied
   * class.
   *
   * @param  c  class that has setter methods
   */
  public DefaultConnectionFactoryPropertyInvoker(final Class<?> c)
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
        newValue = createTypeFromPropertyValue(Provider.class, value);
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
