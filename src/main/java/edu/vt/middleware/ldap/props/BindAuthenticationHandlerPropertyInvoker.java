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

import edu.vt.middleware.ldap.control.Control;
import edu.vt.middleware.ldap.sasl.SaslConfig;

/**
 * Handles properties for
 * {@link edu.vt.middleware.ldap.auth.BindAuthenticationHandler}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BindAuthenticationHandlerPropertyInvoker
  extends AbstractPropertyInvoker
{


  /**
   * Creates a new bind authentication handler property invoker for the supplied
   * class.
   *
   * @param  c  class that has setter methods
   */
  public BindAuthenticationHandlerPropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (SaslConfig.class.isAssignableFrom(type)) {
        if ("null".equals(value)) {
          newValue = null;
        } else {
          if (PropertyValueParser.isParamsOnlyConfig(value)) {
            final PropertyValueParser configParser =
              new PropertyValueParser(
                value, "edu.vt.middleware.ldap.sasl.SaslConfig");
            newValue = configParser.initializeType();
          } else if (PropertyValueParser.isConfig(value)) {
            final PropertyValueParser configParser =
              new PropertyValueParser(value);
            newValue = configParser.initializeType();
          } else {
            newValue = instantiateType(SaslConfig.class, value);
          }
        }
      } else if (Control[].class.isAssignableFrom(type)) {
        newValue = createArrayTypeFromPropertyValue(Control.class, value);
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
