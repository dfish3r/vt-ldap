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
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.LdapUtil;

/**
 * <code>BinaryAttributeHandler</code> ensures that any attribute that contains
 * a value of type byte[] is base64 encoded.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BinaryAttributeHandler extends CopyAttributeHandler
{


  /**
   * This base64 encodes the supplied value if it is of type byte[].
   *
   * @param  sc  <code>SearchCriteria</code> used to find enumeration
   * @param  value  <code>Object</code> to process
   *
   * @return  <code>Object</code>
   */
  protected Object processValue(final SearchCriteria sc, final Object value)
  {
    if (value instanceof byte[]) {
      return LdapUtil.base64Encode((byte[]) value);
    } else {
      return value;
    }
  }
}
