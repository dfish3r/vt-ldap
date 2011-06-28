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

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.auth.AuthenticationException;
import edu.vt.middleware.ldap.provider.BindRequest;
import edu.vt.middleware.ldap.provider.ConnectionException;

/**
 * Creates ldap connections using the JNDI {@link InitialLdapContext} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JndiConnectionFactory extends AbstractJndiConnectionFactory
{


  /**
   * Creates a new jndi connection factory.
   *
   * @param  url  of the ldap to connect to
   */
  public JndiConnectionFactory(final String url)
  {
    if (url == null) {
      throw new IllegalArgumentException("LDAP URL cannot be null");
    }
    ldapUrl = url;
  }


  /** {@inheritDoc} */
  @Override
  protected JndiConnection createInternal(
    final String url, final BindRequest request)
    throws LdapException
  {
    final Hashtable<String, Object> env = new Hashtable<String, Object>(
      environment);
    env.put(PROVIDER_URL, url);
    if (tracePackets != null) {
      env.put(TRACE, tracePackets);
    }

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

      env.put(AUTHENTICATION, authenticationType);
      if (username != null) {
        env.put(PRINCIPAL, username);
        if (credential != null) {
          env.put(CREDENTIALS, credential.getBytes());
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
        env.put(PRINCIPAL, dn);
        if (credential != null) {
          env.put(CREDENTIALS, credential.getBytes());
        }
      }
    }

    JndiConnection conn = null;
    try {
      conn = new JndiConnection(new InitialLdapContext(env, null));
      conn.setRemoveDnUrls(removeDnUrls);
      conn.setOperationRetryExceptions(
        NamingExceptionUtil.getNamingExceptions(
          operationRetryResultCodes));
    } catch (javax.naming.AuthenticationException e) {
      throw new AuthenticationException(e, ResultCode.INVALID_CREDENTIALS);
    } catch (NamingException e) {
      throw new ConnectionException(
        e, NamingExceptionUtil.getResultCode(e.getClass()));
    }
    return conn;
  }
}
