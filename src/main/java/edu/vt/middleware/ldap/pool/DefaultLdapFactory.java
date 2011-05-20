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
 * <code>DefaultLdapFactory</code> provides a simple implementation of an ldap
 * factory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultLdapFactory extends AbstractLdapFactory<Connection>
{

  /** Ldap connection configuration to create ldap objects with. */
  private ConnectionConfig config;

  /** Whether to connect to the ldap on object creation. */
  private boolean connectOnCreate = true;


  /**
   * This creates a new <code>DefaultLdapFactory</code> with the supplied ldap
   * configuration. The ldap configuration will be marked as immutable by this
   * factory.
   *
   * @param  lcc  ldap connection config
   */
  public DefaultLdapFactory(final ConnectionConfig lcc)
  {
    config = lcc;
    config.makeImmutable();
  }


  /**
   * Returns whether ldap objects will attempt to connect after creation.
   * Default is true.
   *
   * @return  <code>boolean</code>
   */
  public boolean getConnectOnCreate()
  {
    return connectOnCreate;
  }


  /**
   * This sets whether newly created ldap objects will attempt to connect.
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
