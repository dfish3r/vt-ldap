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

import edu.vt.middleware.ldap.LdapConnection;

/**
 * <code>LdapActivator</code> provides an interface for activating ldap objects
 * when they enter the pool.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapActivator<T extends LdapConnection>
{


  /**
   * Activate the supplied ldap object.
   *
   * @param  t  ldap object
   *
   * @return  whether activation was successful
   */
  boolean activate(T t);
}
