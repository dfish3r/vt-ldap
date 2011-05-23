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
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapException;

/**
 * Provides a simple implementation of a connection factory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultConnectionFactory
  extends AbstractConnectionFactory<Connection>
{

  /** Connection configuration to create ldap connections with. */
  private ConnectionConfig config;

  /** Whether to connect to the ldap on connection creation. */
  private boolean connectOnCreate = true;


  /**
   * Creates a new default connection factory. The connection configuration will
   * be marked as immutable by this factory.
   *
   * @param  cc  connection config
   */
  public DefaultConnectionFactory(final ConnectionConfig cc)
  {
    config = cc;
    config.makeImmutable();
  }


  /**
   * Returns whether ldap connections will attempt to connect after creation.
   * Default is true.
   *
   * @return   whether ldap connections will attempt to connect after creation
   */
  public boolean getConnectOnCreate()
  {
    return connectOnCreate;
  }


  /**
   * Sets whether newly created ldap connections will attempt to connect.
   * Default is true.
   *
   * @param  b  connect on create
   */
  public void setConnectOnCreate(final boolean b)
  {
    connectOnCreate = b;
  }


  /** {@inheritDoc} */
  @Override
  public Connection create()
  {
    Connection conn = new Connection(config);
    if (connectOnCreate) {
      try {
        conn.open();
      } catch (LdapException e) {
        logger.error("unabled to connect to the ldap", e);
        conn = null;
      }
    }
    return conn;
  }


  /** {@inheritDoc} */
  @Override
  public void destroy(final Connection lc)
  {
    lc.close();
    logger.trace("destroyed ldap connection: {}", lc);
  }
}
