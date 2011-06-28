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
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.auth.AuthenticationException;
import edu.vt.middleware.ldap.provider.BindRequest;
import edu.vt.middleware.ldap.provider.ConnectionException;

/**
 * Creates ldap connections using the JNDI {@link InitialLdapContext} class with
 * the start tls extended operation.
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
   */
  public JndiTlsConnectionFactory(final String url)
  {
    if (url == null) {
      throw new IllegalArgumentException("LDAP URL cannot be null");
    }
    ldapUrl = url;
  }


  /** {@inheritDoc} */
  @Override
  protected JndiTlsConnection createInternal(
    final String url, final BindRequest request)
    throws LdapException
  {
    final Hashtable<String, Object> env = new Hashtable<String, Object>(
      environment);
    env.put(PROVIDER_URL, url);
    if (tracePackets != null) {
      env.put(TRACE, tracePackets);
    }

    JndiTlsConnection conn = null;
    env.put(VERSION, "3");
    boolean closeConn = false;
    try {
      conn = new JndiTlsConnection(new InitialLdapContext(env, null));
      conn.setStartTlsResponse(startTls(conn.getLdapContext()));
      if (request.isSaslRequest()) {
        final String authenticationType = getAuthenticationType(
          request.getSaslConfig().getMechanism());
        final String username = request.getBindDn();
        final Credential credential = request.getBindCredential();
        logger.debug(
          "Bind with the following parameters: url = {}, " +
          "authenticationType = {}, username = {}, credential = {}, env = {}",
          new Object[] {
            url,
            authenticationType,
            username,
            logCredentials || credential == null ? credential : "<suppressed>",
            environment, });

        conn.getLdapContext().addToEnvironment(
          AUTHENTICATION, authenticationType);
        if (username != null) {
          conn.getLdapContext().addToEnvironment(PRINCIPAL, username);
          if (credential != null) {
            conn.getLdapContext().addToEnvironment(
              CREDENTIALS, credential.getBytes());
          }
        }
      } else {
        final String dn = request.getBindDn();
        final Credential credential = request.getBindCredential();
        logger.debug(
          "Bind with the following parameters: url = {}, dn = {}, " +
          "credential = {}, env = {}",
          new Object[] {
            url,
            dn,
            logCredentials || credential == null ? credential : "<suppressed>",
            environment, });
        // note that when using simple authentication (the default),
        // if the credential is null the provider will automatically revert the
        // authentication to none
        if (dn != null) {
          conn.getLdapContext().addToEnvironment(PRINCIPAL, dn);
          if (credential != null) {
            conn.getLdapContext().addToEnvironment(
              CREDENTIALS, credential.getBytes());
          }
        }
      }
      conn.getLdapContext().reconnect(null);
      conn.setRemoveDnUrls(removeDnUrls);
      conn.setOperationRetryExceptions(
        NamingExceptionUtil.getNamingExceptions(
          operationRetryResultCodes));
    } catch (javax.naming.AuthenticationException e) {
      closeConn = true;
      throw new AuthenticationException(e, ResultCode.INVALID_CREDENTIALS);
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
    if (hostnameVerifier != null) {
      logger.trace("TLS hostnameVerifier = {}", hostnameVerifier);
      tls.setHostnameVerifier(hostnameVerifier);
    }
    if (sslSocketFactory != null) {
      logger.trace("TLS sslSocketFactory = {}", sslSocketFactory);
      tls.negotiate(sslSocketFactory);
    } else {
      tls.negotiate();
    }
    return tls;
  }
}
