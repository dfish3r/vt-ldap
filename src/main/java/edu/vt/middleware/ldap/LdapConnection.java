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

  /** LDAP connection configuration. */
  protected LdapConnectionConfig config;

  /** LDAP connection handler. */
  protected ConnectionFactory connectionFactory;

  /** LDAP connection. */
  protected Connection providerConnection;


  /** Default constructor. */
  public LdapConnection() {}


  /**
   * Creates a new ldap connection.
   *
   * @param  ldapUrl  to connect to
   */
  public LdapConnection(final String ldapUrl)
  {
    this.setLdapConnectionConfig(new LdapConnectionConfig(ldapUrl));
  }


  /**
   * Creates a new ldap connection.
   *
   * @param  lcc  ldap connection configuration
   */
  public LdapConnection(final LdapConnectionConfig lcc)
  {
    this.setLdapConnectionConfig(lcc);
  }


  /**
   * Returns the ldap connection configuration.
   *
   * @return  ldap connection configuration
   */
  public LdapConnectionConfig getLdapConnectionConfig()
  {
    return this.config;
  }


  /**
   * Sets the ldap connection configuration.
   *
   * @param  lcc  ldap connection configuration
   */
  public void setLdapConnectionConfig(final LdapConnectionConfig lcc)
  {
    this.config = lcc;
  }


  /**
   * This will establish a connection if one does not already exist by binding
   * to the LDAP using parameters given by
   * {@link LdapConnectionConfig#getBindDn()} and
   * {@link LdapConnectionConfig#getBindCredential()}. If these parameters
   * have not been set then an anonymous bind will be attempted. This connection
   * should be closed using {@link #close()}.
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
    if (this.providerConnection == null) {
      throw new IllegalStateException("Connection not open");
    }
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
   * Convenience method for performing an ldap add operation.
   *
   * @param  dn  to add
   * @param  attrs  to add
   * @throws  LdapException  if an error occurs
   */
  public void add(final String dn, final LdapAttributes attrs)
    throws LdapException
  {
    final AddOperation op = new AddOperation(this);
    op.execute(new AddRequest(dn, attrs));
  }


  /**
   * Convenience method for performing an ldap compare operation.
   *
   * @param  dn  to compare
   * @param  attr  to compare
   * @return  whether compare succeeded
   * @throws  LdapException  if an error occurs
   */
  public boolean compare(final String dn, final LdapAttribute attr)
    throws LdapException
  {
    final CompareOperation op = new CompareOperation(this);
    return op.execute(new CompareRequest(dn, attr)).getResult();
  }


  /**
   * Convenience method for performing an ldap delete operation.
   *
   * @param  dn  to delete
   * @throws  LdapException  if an error occurs
   */
  public void delete(final String dn)
    throws LdapException
  {
    final DeleteOperation op = new DeleteOperation(this);
    op.execute(new DeleteRequest(dn));
  }


  /**
   * Convenience method for performing an ldap modify operation.
   *
   * @param  dn  to modify
   * @param  mods  to modify
   * @throws  LdapException  if an error occurs
   */
  public void modify(final String dn, final AttributeModification[] mods)
    throws LdapException
  {
    final ModifyOperation op = new ModifyOperation(this);
    op.execute(new ModifyRequest(dn, mods));
  }


  /**
   * Convenience method for performing an ldap rename operation.
   *
   * @param  oldDn  to rename
   * @param  newDn  to rename
   * @throws  LdapException  if an error occurs
   */
  public void rename(final String oldDn, final String newDn)
    throws LdapException
  {
    final RenameOperation op = new RenameOperation(this);
    op.execute(new RenameRequest(oldDn, newDn));
  }


  /**
   * Convenience method for performing an ldap search operation.
   *
   * @param  dn  to search on
   * @param  filter  to apply to search
   * @param  retAttrs  attribute names to return
   * @return  ldap result
   * @throws  LdapException  if an error occurs
   */
  public LdapResult search(
    final String dn,
    final SearchFilter filter,
    final String[] retAttrs)
    throws LdapException
  {
    final SearchOperation op = new SearchOperation(this);
    return op.execute(new SearchRequest(dn, filter, retAttrs)).getResult();
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
