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
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.control.Control;

/**
 * Interface for ldap messages.
 *
 * @param  <T>  type of control
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Message<T extends Control>
{


  /**
   * Returns the controls for this message.
   *
   * @return  controls
   */
  T[] getControls();
}
