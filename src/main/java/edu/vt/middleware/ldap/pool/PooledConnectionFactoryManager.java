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
 * Interface for objects that manage an instance of pooled connection factory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface PooledConnectionFactoryManager
{


  /**
   * Returns the connection factory.
   *
   * @return  connection factory
   */
  PooledConnectionFactory getConnectionFactory();


  /**
   * Sets the connection factory.
   *
   * @param  cf  connection factory
   */
  void setConnectionFactory(PooledConnectionFactory cf);
}
