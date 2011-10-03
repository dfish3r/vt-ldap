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

import java.util.Map;
import edu.vt.middleware.ldap.auth.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.AuthenticationRequest;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Provides an authentication factory implementation that uses a spring context.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SpringAuthenticatorFactory implements AuthenticatorFactory
{


  /** Application context. */
  protected static final ClassPathXmlApplicationContext CONTEXT =
    new ClassPathXmlApplicationContext(new String[] {
      "/spring-jaas-context.xml",
    });

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());


  /** {@inheritDoc} */
  @Override
  public Authenticator createAuthenticator(final Map<String, ?> jaasOptions)
  {
    return (Authenticator) CONTEXT.getBean("authenticator");
  }


  /** {@inheritDoc} */
  @Override
  public AuthenticationRequest createAuthenticationRequest(
    final Map<String, ?> jaasOptions)
  {
    return (AuthenticationRequest) CONTEXT.getBean("authenticationRequest");
  }


  /**
   * Closes the authenticator dn resolver if it is a managed dn resolver.
   */
  public static void close()
  {
    final Authenticator a = (Authenticator) CONTEXT.getBean("authenticator");
    if (a.getDnResolver() instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager cfm =
        (PooledConnectionFactoryManager) a.getDnResolver();
      cfm.getConnectionFactory().close();
    }
    final AuthenticationHandler ah = a.getAuthenticationHandler();
    if (ah instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager cfm =
        (PooledConnectionFactoryManager) ah;
      cfm.getConnectionFactory().close();
    }
  }
}
