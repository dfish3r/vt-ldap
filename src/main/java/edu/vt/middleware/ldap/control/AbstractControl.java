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
 * Base class for ldap controls.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractControl implements Control
{

  /** is control critical. */
  private boolean criticality;


  /** {@inheritDoc} */
  @Override
  public boolean getCriticality()
  {
    return criticality;
  }


  /** {@inheritDoc} */
  @Override
  public void setCriticality(final boolean b)
  {
    criticality = b;
  }
}
