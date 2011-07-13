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
 * Request control for ManageDsaIT. See RFC 3296.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ManageDsaITControl extends AbstractControl
{


  /**
   * Default constructor.
   */
  public ManageDsaITControl() {}


  /**
   * Creates a new ManageDsaIT control.
   *
   * @param  b  whether this control is critical
   */
  public ManageDsaITControl(final boolean b)
  {
    setCriticality(b);
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::criticality=%s]",
        getClass().getName(),
        hashCode(),
        criticality);
  }
}
