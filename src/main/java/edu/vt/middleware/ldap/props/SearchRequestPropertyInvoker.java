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

import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.control.RequestControl;
import edu.vt.middleware.ldap.handler.LdapEntryHandler;

/**
 * Handles properties for {@link edu.vt.middleware.ldap.SearchRequest}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchRequestPropertyInvoker extends AbstractPropertyInvoker
{


  /**
   * Creates a new search request property invoker for the supplied class.
   *
   * @param  c  class that has setter methods
   */
  public SearchRequestPropertyInvoker(final Class<?> c)
  {
    initialize(c);
  }


  /** {@inheritDoc} */
  @Override
  protected Object convertValue(final Class<?> type, final String value)
  {
    Object newValue = value;
    if (type != String.class) {
      if (SearchFilter.class.isAssignableFrom(type)) {
        newValue = new SearchFilter(value);
      } else if (RequestControl[].class.isAssignableFrom(type)) {
        newValue = createArrayTypeFromPropertyValue(
          RequestControl.class, value);
      } else if (LdapEntryHandler[].class.isAssignableFrom(type)) {
        newValue = createArrayTypeFromPropertyValue(
          LdapEntryHandler.class,
          value);
      } else if (ResultCode[].class.isAssignableFrom(type)) {
        newValue = createArrayEnumFromPropertyValue(
          ResultCode.class, value);
      } else {
        newValue = convertSimpleType(type, value);
      }
    }
    return newValue;
  }
}
