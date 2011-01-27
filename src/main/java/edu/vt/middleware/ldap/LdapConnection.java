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
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.provider.Connection;
import edu.vt.middleware.ldap.provider.ConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdapConnection
{
  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Ldap config. */
  protected LdapConfig config;

  /** LDAP connection handler. */
  protected ConnectionFactory connectionFactory;

  /** LDAP connection. */
  protected Connection providerConnection;


  /** Default constructor. */
  public LdapConnection() {}


  /**
   * Creates a new ldap connection.
   *
   * @param  lc  ldap configuration
   */
  public LdapConnection(final LdapConfig lc)
  {
    this.setLdapConfig(lc);
  }


  /**
   * Returns the ldap configuration.
   *
   * @return  ldap configuration
   */
  public LdapConfig getLdapConfig()
  {
    return this.config;
  }


  /**
   * Sets the ldap configuration.
   *
   * @param  lc  ldap configuration
   */
  public void setLdapConfig(final LdapConfig lc)
  {
    this.config = lc;
  }


  /**
   * This will establish a connection if one does not already exist by binding
   * to the LDAP using parameters given by {@link LdapConfig#getBindDn()} and
   * {@link LdapConfig#getBindCredential()}. If these parameters have not been
   * set then an anonymous bind will be attempted. This connection must be
   * closed using {@link #close()}.
   *
   * @throws  LdapException  if the LDAP cannot be reached
   */
  public synchronized void open()
    throws LdapException
  {
    if (this.providerConnection != null) {
      throw new IllegalStateException("Connection already open");
    }
    this.connectionFactory = this.config.getConnectionFactory();
    this.providerConnection = this.connectionFactory.create(
      this.config.getBindDn(), this.config.getBindCredential());
  }


  /** This will close the connection to the LDAP. */
  public synchronized void close()
  {
    try {
      this.connectionFactory.destroy(this.providerConnection);
    } catch (LdapException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Error closing connection with the LDAP", e);
      }
    } finally {
      this.providerConnection = null;
      this.connectionFactory = null;
    }
  }


  /**
   * Returns the provider specific connection. Must be called after a successful
   * call to {@link #open()}.
   *
   * @return  provider connection
   */
  public Connection getProviderConnection()
  {
    if (this.providerConnection == null) {
      throw new IllegalStateException("Connection is not open");
    }
    return this.providerConnection;
  }


  /**
   * Closes this connection if it is garbage collected.
   *
   * @throws  Throwable  if an exception is thrown by this method
   */
  protected void finalize()
    throws Throwable
  {
    try {
      this.close();
    } finally {
      super.finalize();
    }
  }
}
