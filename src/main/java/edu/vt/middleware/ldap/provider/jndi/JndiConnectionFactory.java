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
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.auth.AuthenticationException;
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
  protected JndiConnectionFactory(final String url)
  {
    if (url == null) {
      throw new IllegalArgumentException("LDAP URL cannot be null");
    }
    this.ldapUrl = url;
  }


  /** {@inheritDoc} */
  protected JndiConnection createInternal(
    final String url, final String dn, final Credential credential)
    throws LdapException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Bind with the following parameters:");
      this.logger.debug("  url = " + url);
      this.logger.debug("  authentication = " + this.authentication);
      this.logger.debug("  dn = " + dn);
      if (this.logCredentials) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("  credential = " + credential);
        }
      } else {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("  credential = <suppressed>");
        }
      }
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("  env = " + this.environment);
      }
    }

    final Hashtable<String, Object> env = new Hashtable<String, Object>(
      this.environment);
    env.put(PROVIDER_URL, url);
    if (this.tracePackets != null) {
      env.put(TRACE, this.tracePackets);
    }


    // note that when using simple authentication (the default),
    // if the credential is null the provider will automatically revert the
    // authentication to none
    env.put(AUTHENTICATION, this.authentication);
    if (dn != null) {
      env.put(PRINCIPAL, dn);
      if (credential != null) {
        env.put(CREDENTIALS, credential.getBytes());
      }
    }

    JndiConnection conn = null;
    try {
      conn = new JndiConnection(new InitialLdapContext(env, null));
      conn.setRemoveDnUrls(this.removeDnUrls);
      conn.setOperationRetryExceptions(
        NamingExceptionUtil.getNamingExceptions(
          this.operationRetryResultCodes));
    } catch (javax.naming.AuthenticationException e) {
      throw new AuthenticationException(e, ResultCode.INVALID_CREDENTIALS);
    } catch (NamingException e) {
      throw new ConnectionException(
        e, NamingExceptionUtil.getResultCode(e.getClass()));
    }
    return conn;
  }


  /**
   * Creates a new instance of this connection factory.
   *
   * @param  lc  ldap configuration to read connection properties from
   * @return  jndi connection factory
   */
  public static JndiConnectionFactory newInstance(final LdapConfig lc)
  {
    final JndiConnectionFactory cf = new JndiConnectionFactory(
      lc.getLdapUrl());
    cf.setAuthentication(lc.getAuthtype());
    cf.setEnvironment(createEnvironment(lc));
    cf.setLogCredentials(lc.getLogCredentials());
    if (lc.getConnectionStrategy() != null) {
      cf.setConnectionStrategy(lc.getConnectionStrategy());
    }
    return cf;
  }
}
