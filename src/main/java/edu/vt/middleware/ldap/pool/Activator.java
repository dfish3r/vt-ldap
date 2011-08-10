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
package edu.vt.middleware.ldap.pool;

/**
 * Provides an interface for activating objects when they enter the pool.
 *
 * @param  <T>  type of object being pooled
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Activator<T>
{


  /**
   * Activate the supplied object.
   *
   * @param  t  object
   *
   * @return  whether activation was successful
   */
  boolean activate(T t);
}
