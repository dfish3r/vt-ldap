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

import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;

/**
 * Handles properties for
 * {@link edu.vt.middleware.ldap.auth.AuthenticationRequest}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticationRequestPropertyInvoker
  extends AbstractPropertyInvoker
{


  /**
   * Creates a new authentication request property invoker for the supplied
   * class.
   *
   * @param  c  class that has setter methods
   */
  public AuthenticationRequestPropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (Credential.class.isAssignableFrom(type)) {
        newValue = new Credential(value);
      } else if (AuthorizationHandler[].class.isAssignableFrom(type)) {
        newValue = createArrayTypeFromPropertyValue(
          AuthorizationHandler.class,
          value);
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
