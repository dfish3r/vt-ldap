/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.BaseLdap;

/**
 * <code>LdapFactory</code> provides an interface for creating, activating,
 * validating, and destroying ldap objects.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapFactory<T extends BaseLdap>
{


  /**
   * Create a new ldap object.
   *
   * @return  ldap object
   */
  T create();


  /**
   * Destroy an ldap object.
   *
   * @param  t  ldap object
   */
  void destroy(T t);


  /**
   * Prepare the supplied object for placement in the pool.
   *
   * @param  t  ldap object
   *
   * @return  whether the supplied object successfully activated
   */
  boolean activate(T t);


  /**
   * Prepare the supplied object for removal from the pool.
   *
   * @param  t  ldap object
   *
   * @return  whether the supplied object successfully passivated
   */
  boolean passivate(T t);


  /**
   * Verify an ldap object is still viable for use in the pool.
   *
   * @param  t  ldap object
   *
   * @return  whether the supplied object is ready for use
   */
  boolean validate(T t);
}
