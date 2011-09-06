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
package edu.vt.middleware.ldap.jaas;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.ConnectionFactoryManager;
import edu.vt.middleware.ldap.LdapException;

/**
 * Looks up a user's roles using an LDAP search.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchRoleResolver extends AbstractSearchRoleResolver
  implements ConnectionFactoryManager
{

  /** Connection factory. */
  protected ConnectionFactory factory;


  /** Default constructor. */
  public SearchRoleResolver() {}


  /**
   * Creates a new role resolver.
   *
   * @param  cf  connection factory
   */
  public SearchRoleResolver(final ConnectionFactory cf)
  {
    setConnectionFactory(cf);
  }


  /**
   * Returns the connection factory.
   *
   * @return  connection factory
   */
  public ConnectionFactory getConnectionFactory()
  {
    return factory;
  }


  /**
   * Sets the connection factory.
   *
   * @param  cf  connection factory
   */
  public void setConnectionFactory(final ConnectionFactory cf)
  {
    factory = cf;
  }


  /**
   * Retrieve a connection that is ready for use.
   *
   * @return  connection
   *
   * @throws LdapException  if an error occurs opening the connection
   */
  protected Connection getConnection()
    throws LdapException
  {
    final Connection conn = factory.getConnection();
    conn.open();
    return conn;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::factory=%s]",
        getClass().getName(),
        hashCode(),
        factory);
  }
}
