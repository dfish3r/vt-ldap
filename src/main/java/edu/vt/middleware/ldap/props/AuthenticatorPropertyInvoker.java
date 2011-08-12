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

import edu.vt.middleware.ldap.auth.DnResolver;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;

/**
 * Handles properties for {@link edu.vt.middleware.ldap.auth.Authenticator}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticatorPropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new authenticator property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public AuthenticatorPropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (DnResolver.class.isAssignableFrom(type)) {
        newValue = createTypeFromPropertyValue(DnResolver.class, value);
      } else if (AuthenticationHandler.class.isAssignableFrom(type)) {
        newValue = createTypeFromPropertyValue(
          AuthenticationHandler.class,
          value);
      } else if (AuthenticationResultHandler[].class.isAssignableFrom(type)) {
        newValue = createArrayTypeFromPropertyValue(
          AuthenticationResultHandler.class,
          value);
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
