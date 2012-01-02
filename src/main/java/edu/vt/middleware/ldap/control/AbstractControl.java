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
package edu.vt.middleware.ldap.control;

/**
 * Base class for ldap controls.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractControl implements Control
{

  /** control oid. */
  private final String oid;

  /** is control critical. */
  private final boolean criticality;


  /**
   * Creates a new abstract control.
   *
   * @param  id  OID of this control
   */
  public AbstractControl(final String id)
  {
    oid = id;
    criticality = false;
  }


  /**
   * Creates a new abstract control.
   *
   * @param  id  OID of this control
   * @param  b  whether this control is critical
   */
  public AbstractControl(final String id, final boolean b)
  {
    oid = id;
    criticality = b;
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return oid;
  }


  /** {@inheritDoc} */
  @Override
  public boolean getCriticality()
  {
    return criticality;
  }


  /**
   * Returns whether the supplied object contains the same data as this control.
   * Delegates to {@link #hashCode()} implementation.
   *
   * @param  o  to compare for equality
   *
   * @return  equality result
   */
  @Override
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this || (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /**
   * Returns the hash code for this object.
   *
   * @return  hash code
   */
  @Override
  public abstract int hashCode();
}
