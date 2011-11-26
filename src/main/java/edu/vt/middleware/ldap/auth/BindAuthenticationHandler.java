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
import edu.vt.middleware.ldap.BindRequest;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.ConnectionFactoryManager;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.Response;
import edu.vt.middleware.ldap.ResultCode;

/**
 * Provides an LDAP authentication implementation that leverages the LDAP bind
 * operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class BindAuthenticationHandler
  extends AbstractBindAuthenticationHandler
  implements ConnectionFactoryManager
{

  /** Connection factory. */
  private ConnectionFactory factory;


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
  protected AuthenticationHandlerResponse authenticateInternal(
    final Connection c, final AuthenticationCriteria criteria)
    throws LdapException
  {
    AuthenticationHandlerResponse response = null;
    final BindRequest request = new BindRequest(
      criteria.getDn(), criteria.getCredential());
    request.setSaslConfig(getAuthenticationSaslConfig());
    request.setControls(getAuthenticationControls());
    try {
      final Response<Void> connResponse = c.open(request);
      response = new AuthenticationHandlerResponse(
        ResultCode.SUCCESS == connResponse.getResultCode(),
        connResponse.getResultCode(),
        c,
        null,
        connResponse.getControls());
    } catch (LdapException e) {
      if (ResultCode.INVALID_CREDENTIALS == e.getResultCode()) {
        response = new AuthenticationHandlerResponse(
          false,
          e.getResultCode(),
          c,
          e.getMessage(),
          e.getControls());
      } else {
        throw e;
      }
    }
    return response;
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
