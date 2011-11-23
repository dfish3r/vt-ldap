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
 * Provides an LDAP authentication implementation that leverages a compare
 * operation against the userPassword attribute. The default password scheme
 * used is 'SHA'.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CompareAuthenticationHandler
  extends AbstractCompareAuthenticationHandler
  implements ConnectionFactoryManager
{

  /** Connection factory. */
  private ConnectionFactory factory;


  /** Default constructor. */
  public CompareAuthenticationHandler() {}


  /**
   * Creates a new compare authentication handler.
   *
   * @param  cf  connection factory
   */
  public CompareAuthenticationHandler(final ConnectionFactory cf)
  {
    setConnectionFactory(cf);
  }


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory getConnectionFactory()
  {
    return factory;
  }


  /** {@inheritDoc} */
  @Override
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
        "[%s@%d::factory=%s, passwordScheme=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        factory,
        getPasswordScheme(),
        getAuthenticationControls() != null ?
          Arrays.asList(getAuthenticationControls()) : null);
  }
}
