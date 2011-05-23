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

import edu.vt.middleware.ldap.Connection;

/**
 * Provides an interface for activating ldap connections when they enter the
 * pool.
 *
 * @param  <T>  type of ldap connection
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface Activator<T extends Connection>
{


  /**
   * Activate the supplied ldap connection.
   *
   * @param  t  ldap connection
   *
   * @return  whether activation was successful
   */
  boolean activate(T t);
}
