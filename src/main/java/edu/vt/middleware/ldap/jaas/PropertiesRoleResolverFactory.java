/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
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
import edu.vt.middleware.ldap.ConnectionFactoryManager;
import edu.vt.middleware.ldap.DefaultConnectionFactory;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.pool.PooledConnectionFactory;
import edu.vt.middleware.ldap.pool.PooledConnectionFactoryManager;
import edu.vt.middleware.ldap.props.DefaultConnectionFactoryPropertySource;
import edu.vt.middleware.ldap.props.PooledConnectionFactoryPropertySource;
import edu.vt.middleware.ldap.props.PropertySource.PropertyDomain;
import edu.vt.middleware.ldap.props.SearchRequestPropertySource;

/**
 * Provides a module role resolver factory implementation that uses the
 * properties package in this library.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PropertiesRoleResolverFactory extends AbstractPropertiesFactory
  implements RoleResolverFactory
{

  /** Object cache. */
  private static Map<String, RoleResolver> cache =
    new HashMap<String, RoleResolver>();


  /** {@inheritDoc} */
  @Override
  public RoleResolver createRoleResolver(final Map<String, ?> jaasOptions)
  {
    RoleResolver rr = null;
    if (jaasOptions.containsKey(CACHE_ID)) {
      final String cacheId = (String) jaasOptions.get(CACHE_ID);
      synchronized (cache) {
        if (!cache.containsKey(cacheId)) {
          rr = createRoleResolverInternal(jaasOptions);
          logger.trace("Created role resolver: {}", rr);
          cache.put(cacheId, rr);
        } else {
          rr = cache.get(cacheId);
          logger.trace("Retrieved role resolver from cache: {}", rr);
        }
      }
    } else {
      rr = createRoleResolverInternal(jaasOptions);
      logger.trace("Created role resolver {} from {}", rr, jaasOptions);
    }
    return rr;
  }


  /**
   * Initializes a role resolver using a role resolver property source.
   *
   * @param  options  to initialize role resolver
   *
   * @return  role resolver
   */
  protected RoleResolver createRoleResolverInternal(
    final Map<String, ?> options)
  {
    RoleResolver rr = null;
    if (options.containsKey("roleResolver")) {
      try {
        final String className = (String) options.get("roleResolver");
        rr = (RoleResolver) Class.forName(className).newInstance();
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(e);
      } catch (InstantiationException e) {
        throw new IllegalArgumentException(e);
      } catch (IllegalAccessException e) {
        throw new IllegalArgumentException(e);
      }
    } else {
      rr = new SearchRoleResolver();
    }
    if (rr instanceof PooledConnectionFactoryManager) {
      final PooledConnectionFactoryManager cfm =
        (PooledConnectionFactoryManager) rr;
      final PooledConnectionFactory cf = new PooledConnectionFactory();
      final PooledConnectionFactoryPropertySource source =
        new PooledConnectionFactoryPropertySource(
          cf,
          PropertyDomain.AUTH,
          createProperties(options));
      source.initialize();
      cfm.setConnectionFactory(cf);
    }
    if (rr instanceof ConnectionFactoryManager) {
      final ConnectionFactoryManager cfm = (ConnectionFactoryManager) rr;
      final DefaultConnectionFactory cf = new DefaultConnectionFactory();
      final DefaultConnectionFactoryPropertySource source =
        new DefaultConnectionFactoryPropertySource(
          cf,
          PropertyDomain.AUTH,
          createProperties(options));
      source.initialize();
      cfm.setConnectionFactory(cf);
    }
    return rr;
  }


  /** {@inheritDoc} */
  @Override
  public SearchRequest createSearchRequest(final Map<String, ?> jaasOptions)
  {
    final SearchRequest sr = new SearchRequest();
    final SearchRequestPropertySource source = new SearchRequestPropertySource(
      sr,
      PropertyDomain.AUTH,
      createProperties(jaasOptions));
    source.initialize();
    logger.trace("Created search request {} from {}", sr, jaasOptions);
    return sr;
  }


  /** Iterates over the cache and closes all role resolvers. */
  public static void close()
  {
    for (RoleResolver rr : cache.values()) {
      if (rr instanceof PooledConnectionFactoryManager) {
        final PooledConnectionFactoryManager cfm =
          (PooledConnectionFactoryManager) rr;
        cfm.getConnectionFactory().getConnectionPool().close();
      }
    }
  }
}
