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
package edu.vt.middleware.ldap.provider.jndi;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.provider.ConnectionException;

/**
 * Creates connections using the JNDI {@link InitialLdapContext} class with the
 * start TLS extended operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JndiTlsConnectionFactory extends AbstractJndiConnectionFactory
{


  /**
   * Creates a new jndi tls connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  env  jndi context environment
   */
  public JndiTlsConnectionFactory(
    final String url, final Map<String, Object> env)
  {
    if (url == null) {
      throw new IllegalArgumentException("LDAP URL cannot be null");
    }
    ldapUrl = url;
    environment = env;
  }


  /** {@inheritDoc} */
  @Override
  protected JndiTlsConnection createInternal(final String url)
    throws LdapException
  {
    final Hashtable<String, Object> env = new Hashtable<String, Object>(
      environment);
    env.put(PROVIDER_URL, url);
    if (config.getTracePackets() != null) {
      env.put(TRACE, config.getTracePackets());
    }

    JndiTlsConnection conn = null;
    env.put(VERSION, "3");
    boolean closeConn = false;
    try {
      conn = new JndiTlsConnection(new InitialLdapContext(env,null));
      conn.setStartTlsResponse(startTls(conn.getLdapContext()));
      conn.getLdapContext().reconnect(null);
      conn.setRemoveDnUrls(config.getRemoveDnUrls());
      conn.setOperationRetryExceptions(
        NamingExceptionUtil.getNamingExceptions(
          config.getOperationRetryResultCodes()));
    } catch (NamingException e) {
      closeConn = true;
      throw new ConnectionException(
        e, NamingExceptionUtil.getResultCode(e.getClass()));
    } catch (IOException e) {
      closeConn = true;
      throw new ConnectionException(e);
    } catch (RuntimeException e) {
      closeConn = true;
      throw e;
    } finally {
      if (closeConn) {
        try {
          if (conn != null) {
            conn.close();
          }
        } catch (LdapException e) {
          logger.debug("Problem tearing down connection", e);
        }
      }
    }
    return conn;
  }


  /**
   * This will attempt the StartTLS extended operation on the supplied ldap
   * context.
   *
   * @param  ctx  ldap context
   *
   * @return  start tls response
   *
   * @throws  NamingException  if an error occurs while requesting an extended
   * operation
   * @throws  IOException  if an error occurs while negotiating TLS
   */
  public StartTlsResponse startTls(final LdapContext ctx)
    throws NamingException, IOException
  {
    final StartTlsResponse tls = (StartTlsResponse) ctx.extendedOperation(
      new StartTlsRequest());
    if (config.getHostnameVerifier() != null) {
      logger.trace("TLS hostnameVerifier = {}", config.getHostnameVerifier());
      tls.setHostnameVerifier(config.getHostnameVerifier());
    }
    if (config.getSslSocketFactory() != null) {
      logger.trace("TLS sslSocketFactory = {}", config.getSslSocketFactory());
      tls.negotiate(config.getSslSocketFactory());
    } else {
      tls.negotiate();
    }
    return tls;
  }
}
