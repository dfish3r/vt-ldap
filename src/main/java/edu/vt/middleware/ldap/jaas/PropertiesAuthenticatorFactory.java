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

import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.ldap.auth.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.AuthenticationRequest;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;
import edu.vt.middleware.ldap.props.AuthenticationRequestPropertySource;
import edu.vt.middleware.ldap.props.AuthenticatorPropertySource;

/**
 * Provides a module authenticator factory implementation that uses the
 * properties package in this library.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PropertiesAuthenticatorFactory extends AbstractPropertiesFactory
  implements AuthenticatorFactory
{

  /** Object cache. */
  private static Map<String, Authenticator> cache =
    new HashMap<String, Authenticator>();


  /** {@inheritDoc} */
  @Override
  public Authenticator createAuthenticator(final Map<String, ?> jaasOptions)
  {
    Authenticator a = null;
    if (jaasOptions.containsKey(CACHE_ID)) {
      final String cacheId = (String) jaasOptions.get(CACHE_ID);
      synchronized (cache) {
        if (!cache.containsKey(cacheId)) {
          a = createAuthenticatorInternal(jaasOptions);
          logger.trace("Created authenticator: {}", a);
          cache.put(cacheId, a);
        } else {
          a = cache.get(cacheId);
          logger.trace("Retrieved authenticator from cache: {}", a);
        }
      }
    } else {
      a = createAuthenticatorInternal(jaasOptions);
      logger.trace("Created authenticator {} from {}", a, jaasOptions);
    }
    return a;
  }


  /**
   * Initializes an authenticator using an authenticator property source.
   *
   * @param  options  to initialize authenticator
   *
   * @return  authenticator
   */
  protected Authenticator createAuthenticatorInternal(
    final Map<String, ?> options)
  {
    final Authenticator a = new Authenticator();
    final AuthenticatorPropertySource source = new AuthenticatorPropertySource(
      a, createProperties(options));
    source.initialize();
    return a;
  }


  /** {@inheritDoc} */
  @Override
  public AuthenticationRequest createAuthenticationRequest(
    final Map<String, ?> jaasOptions)
  {
    final AuthenticationRequest ar = new AuthenticationRequest();
    final AuthenticationRequestPropertySource source =
      new AuthenticationRequestPropertySource(
        ar, createProperties(jaasOptions));
    source.initialize();
    logger.trace("Created authentication request {} from {}", ar, jaasOptions);
    return ar;
  }


  /**
   * Iterates over the cache and closes any managed dn resolvers and managed
   * authentication handlers.
   */
  public static void close()
  {
    for (Map.Entry<String, Authenticator> e : cache.entrySet()) {
      final Authenticator a = e.getValue();
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
}
