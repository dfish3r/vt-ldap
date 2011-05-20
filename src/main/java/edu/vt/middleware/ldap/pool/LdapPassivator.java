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
 * <code>LdapPasivator</code> provides an interface for passivating ldap objects
 * when they are checked back into the pool.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapPassivator<T extends Connection>
{


  /**
   * Passivate the supplied ldap object.
   *
   * @param  t  ldap object
   *
   * @return  whether passivation was successful
   */
  boolean passivate(T t);
}
