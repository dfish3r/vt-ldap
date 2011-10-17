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
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.control.Control;

/**
 * Interface for ldap messages.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Message
{


  /**
   * Returns the controls for this message.
   *
   * @return  controls
   */
  Control[] getControls();
}
