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
package edu.vt.middleware.ldap.provider.control;

import edu.vt.middleware.ldap.control.RequestControl;
import edu.vt.middleware.ldap.control.ResponseControl;

/**
 * Handles provider specific response controls.
 *
 * @param  <T>  type of provider specific control
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ResponseControlHandler<T> extends ControlHandler
{


  /**
   * Converts the supplied provider control to a response control. The request
   * control is provided if there is an associated request control for the
   * response control. Otherwise it is null.
   *
   * @param  requestControl  with the same oid
   * @param  responseControl  to convert
   * @return  control
   */
  ResponseControl processResponse(
    RequestControl requestControl, T responseControl);
}
