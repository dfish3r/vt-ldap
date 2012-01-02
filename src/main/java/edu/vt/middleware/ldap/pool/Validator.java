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
package edu.vt.middleware.ldap.pool;

/**
 * Provides an interface for validating objects when they are in the pool.
 *
 * @param  <T>  type of object being pooled
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Validator<T>
{


  /**
   * Validate the supplied object.
   *
   * @param  t  object
   *
   * @return  whether validation was successful
   */
  boolean validate(T t);
}
