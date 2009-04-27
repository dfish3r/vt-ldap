/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
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
 * <code>LdapPasivator</code> provides an interface for passivating ldap objects
 * when they are checked back into the pool.
 *
 * @param  <T>  type of ldap object
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapPassivator<T extends BaseLdap>
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
