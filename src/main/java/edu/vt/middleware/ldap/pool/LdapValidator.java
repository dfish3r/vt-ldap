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
 * <code>LdapValidator</code> provides an interface for validating ldap objects
 * when they are in the pool.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapValidator<T extends LdapConnection>
{


  /**
   * Validate the supplied ldap object.
   *
   * @param  t  ldap object
   *
   * @return  whether validation was successful
   */
  boolean validate(T t);
}
