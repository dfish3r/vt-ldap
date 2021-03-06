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
import edu.vt.middleware.ldap.provider.AbstractConnectionFactory;
import edu.vt.middleware.ldap.provider.ConnectionException;

/**
 * Creates connections using the JNDI {@link InitialLdapContext} class with the
 * start TLS extended operation.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JndiTlsConnectionFactory
  extends AbstractConnectionFactory<JndiProviderConfig>
{

  /** Environment properties. */
  private Map<String, Object> environment;


  /**
   * Creates a new jndi tls connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  env  jndi context environment
   */
  public JndiTlsConnectionFactory(
    final String url,
    final Map<String, Object> env)
  {
    super(url);
    environment = env;
  }


  /** {@inheritDoc} */
  @Override
  protected JndiTlsConnection createInternal(final String url)
    throws LdapException
  {
    // CheckStyle:IllegalType OFF
    // the JNDI API requires the Hashtable type
    final Hashtable<String, Object> env = new Hashtable<String, Object>(
      environment);
    // CheckStyle:IllegalType ON
    env.put(JndiProvider.PROVIDER_URL, url);
    if (getProviderConfig().getTracePackets() != null) {
      env.put(JndiProvider.TRACE, getProviderConfig().getTracePackets());
    }

    JndiTlsConnection conn = null;
    boolean closeConn = false;
    try {
      conn = new JndiTlsConnection(new InitialLdapContext(env, null));
      conn.setStartTlsResponse(startTls(conn.getLdapContext()));
      conn.setRemoveDnUrls(getProviderConfig().getRemoveDnUrls());
      conn.setOperationRetryResultCodes(
        getProviderConfig().getOperationRetryResultCodes());
      conn.setSearchIgnoreResultCodes(
        getProviderConfig().getSearchIgnoreResultCodes());
      conn.setControlProcessor(getProviderConfig().getControlProcessor());
    } catch (NamingException e) {
      closeConn = true;
      throw new ConnectionException(
        e,
        NamingExceptionUtil.getResultCode(e.getClass()));
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
  protected StartTlsResponse startTls(final LdapContext ctx)
    throws NamingException, IOException
  {
    final StartTlsResponse tls = (StartTlsResponse) ctx.extendedOperation(
      new StartTlsRequest());
    if (getProviderConfig().getHostnameVerifier() != null) {
      logger.trace(
        "TLS hostnameVerifier = {}",
        getProviderConfig().getHostnameVerifier());
      tls.setHostnameVerifier(getProviderConfig().getHostnameVerifier());
    }
    if (getProviderConfig().getSslSocketFactory() != null) {
      logger.trace(
        "TLS sslSocketFactory = {}",
        getProviderConfig().getSslSocketFactory());
      tls.negotiate(getProviderConfig().getSslSocketFactory());
    } else {
      tls.negotiate();
    }
    return tls;
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
        "[%s@%d::config=%s]",
        getClass().getName(),
        hashCode(),
        getProviderConfig());
  }
}
