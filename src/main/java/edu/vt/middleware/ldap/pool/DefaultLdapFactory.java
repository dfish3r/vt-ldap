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

import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapException;

/**
 * <code>DefaultLdapFactory</code> provides a simple implementation of an ldap
 * factory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultLdapFactory extends AbstractLdapFactory<LdapConnection>
{

  /** Ldap connection configuration to create ldap objects with. */
  private LdapConnectionConfig config;

  /** Whether to connect to the ldap on object creation. */
  private boolean connectOnCreate = true;


  /**
   * This creates a new <code>DefaultLdapFactory</code> with the supplied ldap
   * configuration. The ldap configuration will be marked as immutable by this
   * factory.
   *
   * @param  lcc  ldap connection config
   */
  public DefaultLdapFactory(final LdapConnectionConfig lcc)
  {
    this.config = lcc;
    this.config.makeImmutable();
  }


  /**
   * Returns whether ldap objects will attempt to connect after creation.
   * Default is true.
   *
   * @return  <code>boolean</code>
   */
  public boolean getConnectOnCreate()
  {
    return this.connectOnCreate;
  }


  /**
   * This sets whether newly created ldap objects will attempt to connect.
   * Default is true.
   *
   * @param  b  connect on create
   */
  public void setConnectOnCreate(final boolean b)
  {
    this.connectOnCreate = b;
  }


  /** {@inheritDoc} */
  public LdapConnection create()
  {
    LdapConnection conn = new LdapConnection(this.config);
    if (this.connectOnCreate) {
      try {
        conn.open();
      } catch (LdapException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("unabled to connect to the ldap", e);
        }
        conn = null;
      }
    }
    return conn;
  }


  /** {@inheritDoc} */
  public void destroy(final LdapConnection lc)
  {
    lc.close();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("destroyed ldap connection: " + lc);
    }
  }
}
