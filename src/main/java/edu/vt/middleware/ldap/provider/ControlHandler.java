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
package edu.vt.middleware.ldap.provider;

import edu.vt.middleware.ldap.control.RequestControl;
import edu.vt.middleware.ldap.control.ResponseControl;

/**
 * Handles provider specific request and response controls.
 *
 * @param  <T>  type of provider control
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ControlHandler<T>
{


  /**
   * Returns the OID of the supplied control.
   *
   * @param  control  to return oid for
   *
   * @return  control oid
   */
  String getOID(T control);


  /**
   * Converts the supplied control to a provider specific request control.
   *
   * @param  requestControl  to convert
   *
   * @return  provider specific controls
   */
  T processRequest(RequestControl requestControl);


  /**
   * Converts the supplied provider control to a response control.
   *
   * @param  responseControl  to convert
   *
   * @return  control
   */
  ResponseControl processResponse(T responseControl);
}
