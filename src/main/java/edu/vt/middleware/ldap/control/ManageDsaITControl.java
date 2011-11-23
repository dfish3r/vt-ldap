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
                                implements RequestControl
{

  /** OID of this control. */
  public static final String OID = "2.16.840.1.113730.3.4.2";


  /**
   * Default constructor.
   */
  public ManageDsaITControl() {}


  /**
   * Creates a new ManageDsaIT control.
   *
   * @param  critical  whether this control is critical
   */
  public ManageDsaITControl(final boolean critical)
  {
    setCriticality(critical);
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return OID;
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
        getCriticality());
  }
}
