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

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import edu.vt.middleware.ldap.LdapConfig;

/**
 * ConnectionHandler provides an interface for creating and closing LDAP
 * connections.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ConnectionHandler
{

  /**
   * Enum to define the type of connection strategy.
   */
  public enum ConnectionStrategy {

    /** default strategy. */
    DEFAULT,

    /** active-passive strategy. */
    ACTIVE_PASSIVE,

    /** round robin strategy. */
    ROUND_ROBIN,

    /** random strategy. */
    RANDOM,
  }


  /**
   * Returns the connection strategy.
   *
   * @return  strategy for making connections
   */
  ConnectionStrategy getConnectionStrategy();


  /**
   * Sets the connection strategy.
   *
   * @param  strategy  for making connections
   */
  void setConnectionStrategy(ConnectionStrategy strategy);


  /**
   * This returns the exception types to retry connections on.
   *
   * @return  <code>Class[]</code>
   */
  Class<?>[] getConnectionRetryExceptions();


  /**
   * This sets the exception types to retry connections on.
   *
   * @param  exceptions  <code>Class[]</code>
   */
  void setConnectionRetryExceptions(Class<?>[] exceptions);


  /**
   * Returns the ldap configuration.
   *
   * @return  ldap config
   */
  LdapConfig getLdapConfig();


  /**
   * Sets the ldap configuration.
   *
   * @param  lc  ldap config
   */
  void setLdapConfig(LdapConfig lc);


  /**
   * Open a connection to an LDAP.
   *
   * @param  dn  to attempt bind with
   * @param  credential  to attempt bind with
   *
   * @throws  NamingException  if an LDAP error occurs
   */
  void connect(String dn, Object credential)
    throws NamingException;


  /**
   * Returns whether the underlying context has been established.
   *
   * @return  whether a connection has been made
   */
  boolean isConnected();


  /**
   * Returns an ldap context to use for ldap operations. {@link #connect(String,
   * Object)} must be called prior to invoking this.
   *
   * @return  ldap context
   *
   * @throws  NamingException  if an LDAP error occurs
   */
  LdapContext getLdapContext()
    throws NamingException;


  /**
   * Close a connection to an LDAP.
   *
   * @throws  NamingException  if an LDAP error occurs
   */
  void close()
    throws NamingException;


  /**
   * Returns a separate instance of this connection handler with the same
   * underlying ldap configuration.
   *
   * @return  connection handler
   */
  ConnectionHandler newInstance();
}
