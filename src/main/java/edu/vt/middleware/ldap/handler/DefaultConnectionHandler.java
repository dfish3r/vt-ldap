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
package edu.vt.middleware.ldap.handler;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.LdapConstants;
import edu.vt.middleware.ldap.ssl.DefaultHostnameVerifier;

/**
 * <code>DefaultConnectionHandler</code> creates a new <code>LdapContext</code>
 * using environment properties obtained from {@link
 * LdapConfig#getEnvironment()}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DefaultConnectionHandler extends AbstractConnectionHandler
{


  /** Default constructor. */
  public DefaultConnectionHandler() {}


  /**
   * Creates a new <code>DefaultConnectionHandler</code> with the supplied ldap
   * config.
   *
   * @param  lc  ldap config
   */
  public DefaultConnectionHandler(final LdapConfig lc)
  {
    this.setLdapConfig(lc);
  }


  /**
   * Copy constructor for <code>DefaultConnectionHandler</code>.
   *
   * @param  ch  to copy properties from
   */
  public DefaultConnectionHandler(final DefaultConnectionHandler ch)
  {
    this.setLdapConfig(ch.getLdapConfig());
    this.setConnectionStrategy(ch.getConnectionStrategy());
    this.setConnectionRetryExceptions(ch.getConnectionRetryExceptions());
    this.setConnectionCount(ch.getConnectionCount());
  }


  /** {@inheritDoc} */
  protected void connectInternal(
    final String authtype,
    final String dn,
    final Object credential,
    final Hashtable<String, Object> env)
    throws NamingException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Bind with the following parameters:");
      this.logger.debug("  authtype = " + authtype);
      this.logger.debug("  dn = " + dn);
      if (this.config.getLogCredentials()) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("  credential = " + credential);
        }
      } else {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("  credential = <suppressed>");
        }
      }
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("  env = " + env);
      }
    }

    // note that when using simple authentication (the default),
    // if the credential is null the provider will automatically revert the
    // authentication to none
    env.put(LdapConstants.AUTHENTICATION, authtype);
    if (dn != null) {
      env.put(LdapConstants.PRINCIPAL, dn);
      if (credential != null) {
        env.put(LdapConstants.CREDENTIALS, credential);
      }
    }

    // JNDI does not perform hostname validation for LDAPS
    // set a socket factory that will
    if (LdapConstants.SSL_PROTOCOL.equals(env.get(LdapConstants.PROTOCOL)) ||
        ((String) env.get(LdapConstants.PROVIDER_URL)).toLowerCase().contains(
          "ldaps://")) {
      if (env.get(LdapConstants.SOCKET_FACTORY) == null) {
        env.put(
          LdapConstants.SOCKET_FACTORY,
          DefaultHostnameVerifier.SSLSocketFactory.class.getName());
      }
    }

    try {
      this.context = new InitialLdapContext(env, null);
    } catch (NamingException e) {
      if (this.context != null) {
        try {
          this.context.close();
        } finally {
          this.context = null;
        }
      }
      throw e;
    }
  }


  /** {@inheritDoc} */
  public DefaultConnectionHandler newInstance()
  {
    return new DefaultConnectionHandler(this);
  }
}
