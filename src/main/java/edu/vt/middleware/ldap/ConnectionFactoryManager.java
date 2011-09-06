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
package edu.vt.middleware.ldap;

/**
 * Interface for objects that manage an instance of connection factory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface ConnectionFactoryManager
{


  /**
   * Returns the connection factory.
   *
   * @return  connection factory
   */
  ConnectionFactory getConnectionFactory();


  /**
   * Sets the connection factory.
   *
   * @param  cf  connection factory
   */
  void setConnectionFactory(ConnectionFactory cf);
}
