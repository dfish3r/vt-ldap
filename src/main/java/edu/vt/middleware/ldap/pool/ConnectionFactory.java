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
 * Provides an interface for creating, activating, validating, and destroying
 * ldap connections.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ConnectionFactory<T extends Connection>
{


  /**
   * Create a new ldap connection.
   *
   * @return  ldap connection
   */
  T create();


  /**
   * Destroy an ldap connection.
   *
   * @param  t  ldap connection
   */
  void destroy(T t);


  /**
   * Prepare the supplied connection for placement in the pool.
   *
   * @param  t  ldap connection
   *
   * @return  whether the supplied connection successfully activated
   */
  boolean activate(T t);


  /**
   * Prepare the supplied connection for removal from the pool.
   *
   * @param  t  ldap connection
   *
   * @return  whether the supplied connection successfully passivated
   */
  boolean passivate(T t);


  /**
   * Verify an ldap connection is still viable for use in the pool.
   *
   * @param  t  ldap connection
   *
   * @return  whether the supplied connection is viable
   */
  boolean validate(T t);
}
