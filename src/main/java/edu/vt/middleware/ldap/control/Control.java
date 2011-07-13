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
 * Marker interface for ldap controls.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Control
{


  /**
   * Returns whether the control is critical.
   *
   * @return  whether the control is critical
   */
  boolean getCriticality();


  /**
   * Sets whether the control is critical.
   *
   * @param  b  whether the control is critical
   */
  void setCriticality(boolean b);
}
