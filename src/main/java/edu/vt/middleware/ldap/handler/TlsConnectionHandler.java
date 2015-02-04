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

import java.io.IOException;
import java.util.Hashtable;
import javax.naming.CommunicationException;
import javax.naming.ConfigurationException;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.LdapConstants;

/**
 * <code>TlsConnectionHandler</code> creates a new <code>LdapContext</code>
 * using environment properties obtained from {@link
 * LdapConfig#getEnvironment()} and then invokes the startTLS extended operation
 * on the context. <code>SSLSocketFactory</code> and <code>
 * HostnameVerifier</code> properties are used from the <code>
 * LdapContext</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class TlsConnectionHandler extends DefaultConnectionHandler
{

  /** Start TLS response. */
  private StartTlsResponse startTlsResponse;

  /**
   * Whether to call {@link StartTlsResponse#close()} when {@link #close()} is
   * called.
   */
  private boolean stopTlsOnClose;


  /** Default constructor. */
  public TlsConnectionHandler() {}


  /**
   * Creates a new <code>TlsConnectionHandler</code> with the supplied ldap
   * config.
   *
   * @param  lc  ldap config
   */
  public TlsConnectionHandler(final LdapConfig lc)
  {
    super(lc);
  }


  /**
   * Copy constructor for <code>TlsConnectionHandler</code>.
   *
   * @param  ch  to copy properties from
   */
  public TlsConnectionHandler(final TlsConnectionHandler ch)
  {
    this.setLdapConfig(ch.getLdapConfig());
    this.setConnectionStrategy(ch.getConnectionStrategy());
    this.setConnectionRetryExceptions(ch.getConnectionRetryExceptions());
    this.setConnectionCount(ch.getConnectionCount());
    this.setStopTlsOnClose(ch.getStopTlsOnClose());
  }


  /**
   * Returns whether to call {@link StartTlsResponse#close()} when {@link
   * #close()} is called.
   *
   * @return  stop TLS on close
   */
  public boolean getStopTlsOnClose()
  {
    return this.stopTlsOnClose;
  }


  /**
   * Sets whether to call {@link StartTlsResponse#close()} when {@link #close()}
   * is called.
   *
   * @param  b  stop TLS on close
   */
  public void setStopTlsOnClose(final boolean b)
  {
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting stopTlsOnClose: " + b);
    }
    this.stopTlsOnClose = b;
  }


  /**
   * This returns the startTLS response created by a call to {@link
   * #connect(String, Object)}.
   *
   * @return  start tls response
   */
  public StartTlsResponse getStartTlsResponse()
  {
    return this.startTlsResponse;
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

    env.put(LdapConstants.VERSION, LdapConstants.VERSION_THREE);
    try {
      this.context = new InitialLdapContext(env, null);
      this.startTlsResponse = this.startTls(this.context);
      // note that when using simple authentication (the default),
      // if the credential is null the provider will automatically revert the
      // authentication to none
      this.context.addToEnvironment(LdapConstants.AUTHENTICATION, authtype);
      if (dn != null) {
        this.context.addToEnvironment(LdapConstants.PRINCIPAL, dn);
        if (credential != null) {
          // JNDI does not startTLS when chasing referrals
          if (this.context.getEnvironment().containsKey(
                LdapConstants.REFERRAL) &&
              "follow".equals(
                this.context.getEnvironment().get(LdapConstants.REFERRAL))) {
            throw new ConfigurationException(
              "Referral following with credentials is not supported with " +
              "startTLS, use LDAPS instead.");
          }
          this.context.addToEnvironment(LdapConstants.CREDENTIALS, credential);
        }
      }
      this.context.reconnect(null);
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
  public void close()
    throws NamingException
  {
    try {
      if (this.stopTlsOnClose) {
        this.stopTls(this.startTlsResponse);
      }
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Error stopping TLS", e);
      }
    } finally {
      this.startTlsResponse = null;
      super.close();
    }
  }


  /**
   * This will attempt to StartTLS with the supplied <code>LdapContext</code>.
   *
   * @param  ctx  <code>LdapContext</code>
   *
   * @return  <code>StartTlsResponse</code>
   *
   * @throws  NamingException  if an error occurs while requesting an extended
   * operation
   */
  public StartTlsResponse startTls(final LdapContext ctx)
    throws NamingException
  {
    StartTlsResponse tls = null;
    try {
      tls = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
      if (this.config.useHostnameVerifier()) {
        if (this.logger.isTraceEnabled()) {
          this.logger.trace(
            "TLS hostnameVerifier = " + this.config.getHostnameVerifier());
        }
        tls.setHostnameVerifier(this.config.getHostnameVerifier());
      }
      if (this.config.useSslSocketFactory()) {
        if (this.logger.isTraceEnabled()) {
          this.logger.trace(
            "TLS sslSocketFactory = " + this.config.getSslSocketFactory());
        }
        tls.negotiate(this.config.getSslSocketFactory());
      } else {
        tls.negotiate();
      }
    } catch (IOException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Could not negotiate TLS connection", e);
      }
      throw new CommunicationException(e.getMessage());
    }
    return tls;
  }


  /**
   * This will attempt to StopTLS with the supplied <code>
   * StartTlsResponse</code>.
   *
   * @param  tls  <code>StartTlsResponse</code>
   *
   * @throws  NamingException  if an error occurs while closing the TLS
   * connection
   */
  public void stopTls(final StartTlsResponse tls)
    throws NamingException
  {
    if (tls != null) {
      try {
        tls.close();
      } catch (IOException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("Could not close TLS connection", e);
        }
        throw new CommunicationException(e.getMessage());
      }
    }
  }


  /** {@inheritDoc} */
  public TlsConnectionHandler newInstance()
  {
    return new TlsConnectionHandler(this);
  }
}
