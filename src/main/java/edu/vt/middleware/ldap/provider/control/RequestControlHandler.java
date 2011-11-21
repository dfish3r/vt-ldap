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

import edu.vt.middleware.ldap.control.Control;

/**
 * Handles provider specific request controls.
 *
 * @param  <T>  type of provider specific control
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface RequestControlHandler<T> extends ControlHandler<T>
{


  /**
   * Converts the supplied control to a provider specific request control.
   *
   * @param  requestControl  to convert
   * @return  provider specific controls
   */
  T processRequest(Control requestControl);
}
