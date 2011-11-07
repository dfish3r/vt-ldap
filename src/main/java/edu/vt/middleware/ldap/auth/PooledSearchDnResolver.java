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
package edu.vt.middleware.ldap.auth;

import java.util.Arrays;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;

/**
 * Looks up a user's DN using a pool of connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PooledSearchDnResolver extends AbstractSearchDnResolver
  implements PooledConnectionFactoryManager
{

  /** Connection factory. */
  protected PooledConnectionFactory factory;


  /** Default constructor. */
  public PooledSearchDnResolver() {}


  /**
   * Creates a new pooled search dn resolver.
   *
   * @param  cf  connection factory
   */
  public PooledSearchDnResolver(final PooledConnectionFactory cf)
  {
    setConnectionFactory(cf);
  }


  /**
   * Returns the connection factory.
   *
   * @return  connection factory
   */
  public PooledConnectionFactory getConnectionFactory()
  {
    return factory;
  }


  /**
   * Sets the connection factory.
   *
   * @param  cf  connection factory
   */
  public void setConnectionFactory(final PooledConnectionFactory cf)
  {
    factory = cf;
  }


  /** {@inheritDoc} */
  @Override
  protected Connection getConnection()
    throws LdapException
  {
    return factory.getConnection();
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
        "[%s@%d::factory=%s, baseDn=%s, userFilter=%s, userFilterArgs=%s, " +
        "allowMultipleDns=%s, subtreeSearch=%s, derefAliases=%s, " +
        "referralBehavior=%s]",
        getClass().getName(),
        hashCode(),
        factory,
        baseDn,
        userFilter,
        userFilterArgs != null ? Arrays.asList(userFilterArgs) : null,
        allowMultipleDns,
        subtreeSearch,
        derefAliases,
        referralBehavior);
  }
}
