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
package edu.vt.middleware.ldap.props;

/**
 * Interface for property driven object initialization.
 *
 * @param  <T>  type of object to invoke properties on
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface PropertySource<T>
{


  /**
   * Returns whether the supplied property exists for this object.
   *
   * @param  name  of the property to check
   *
   * @return  whether the supplied property name exists
   */
  boolean hasProperty(String name);


  /**
   * Returns the object initialized with properties.
   *
   * @return  initialized object
   */
  T get();
}
