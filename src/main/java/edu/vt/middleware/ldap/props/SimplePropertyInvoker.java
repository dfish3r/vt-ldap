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
 * Handles simple properties common to all objects.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SimplePropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new simple property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public SimplePropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    return convertSimpleType(type, value);
  }
}
