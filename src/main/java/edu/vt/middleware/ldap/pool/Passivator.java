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
 * Provides an interface for passivating objects when they are checked back into
 * the pool.
 *
 * @param  <T>  type of object being pooled
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Passivator<T>
{


  /**
   * Passivate the supplied object.
   *
   * @param  t  object
   *
   * @return  whether passivation was successful
   */
  boolean passivate(T t);
}
