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
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.ConnectionFactoryManager;
import edu.vt.middleware.ldap.LdapException;

/**
 * Looks up a user's DN using an LDAP search.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchDnResolver extends AbstractSearchDnResolver
  implements ConnectionFactoryManager
{

  /** Connection factory. */
  private ConnectionFactory factory;


  /** Default constructor. */
  public SearchDnResolver() {}


  /**
   * Creates a new search dn resolver.
   *
   * @param  cf  connection factory
   */
  public SearchDnResolver(final ConnectionFactory cf)
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


  /** {@inheritDoc} */
  @Override
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
        "[%s@%d::factory=%s, baseDn=%s, userFilter=%s, userFilterArgs=%s, " +
        "allowMultipleDns=%s, subtreeSearch=%s, derefAliases=%s, " +
        "referralBehavior=%s]",
        getClass().getName(),
        hashCode(),
        factory,
        getBaseDn(),
        getUserFilter(),
        Arrays.toString(getUserFilterArgs()),
        getAllowMultipleDns(),
        getSubtreeSearch(),
        getDerefAliases(),
        getReferralBehavior());
  }
}
