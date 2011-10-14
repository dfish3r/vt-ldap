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
import edu.vt.middleware.ldap.BindOperation;
import edu.vt.middleware.ldap.BindRequest;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;

/**
 * Provides an LDAP authentication implementation that leverages a pool of LDAP
 * connections to perform the LDAP bind operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PooledBindAuthenticationHandler
  extends AbstractBindAuthenticationHandler
  implements PooledConnectionFactoryManager
{

  /** Connection factory. */
  protected PooledConnectionFactory factory;


  /** Default constructor. */
  public PooledBindAuthenticationHandler() {}


  /**
   * Creates a new pooled bind authentication handler.
   *
   * @param  cf  connection factory
   */
  public PooledBindAuthenticationHandler(final PooledConnectionFactory cf)
  {
    setConnectionFactory(cf);
  }


  /** {@inheritDoc} */
  @Override
  public PooledConnectionFactory getConnectionFactory()
  {
    return factory;
  }


  /** {@inheritDoc} */
  @Override
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


  /** {@inheritDoc} */
  @Override
  protected void authenticateInternal(
    final Connection c, final AuthenticationCriteria criteria)
    throws LdapException
  {
    final BindRequest request = new BindRequest(
      criteria.getDn(), criteria.getCredential());
    request.setSaslConfig(getAuthenticationSaslConfig());
    request.setControls(getAuthenticationControls());
    final BindOperation op = new BindOperation(c);
    op.execute(request);
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
        "[%s@%d::factory=%s, saslConfig=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        factory,
        getAuthenticationSaslConfig(),
        getAuthenticationControls() != null ?
          Arrays.asList(getAuthenticationControls()) : null);
  }
}
