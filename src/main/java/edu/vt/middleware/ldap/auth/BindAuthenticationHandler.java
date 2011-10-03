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

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.ConnectionFactoryManager;
import edu.vt.middleware.ldap.LdapException;

/**
 * Provides an LDAP authentication implementation that leverages the LDAP bind
 * operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class BindAuthenticationHandler
  extends AbstractAuthenticationHandler
  implements ConnectionFactoryManager
{

  /** Connection factory. */
  protected ConnectionFactory factory;


  /** Default constructor. */
  public BindAuthenticationHandler() {}


  /**
   * Creates a new bind authentication handler.
   *
   * @param  cf  connection factory
   */
  public BindAuthenticationHandler(final ConnectionFactory cf)
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
    return factory.getConnection();
  }


  /** {@inheritDoc} */
  @Override
  protected void authenticateInternal(
    final Connection c, final AuthenticationCriteria criteria)
    throws LdapException
  {
    c.open(criteria.getDn(), criteria.getCredential());
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
