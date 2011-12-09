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
package edu.vt.middleware.ldap.control;

/**
 * Marker interface for ldap response controls.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ResponseControl extends Control
{


  /**
   * Initializes this response control with the supplied BER encoded data.
   *
   * @param  encoded  BER encoded response control
   */
  void decode(byte[] encoded);
}
