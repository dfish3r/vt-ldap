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

import java.util.Collection;
import edu.vt.middleware.ldap.provider.ProviderConnection;
import edu.vt.middleware.ldap.provider.ProviderConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for managing an LDAP connection.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class Connection
{
  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** LDAP connection configuration. */
  protected ConnectionConfig config;

  /** LDAP connection factory. */
  protected ProviderConnectionFactory<?> providerConnectionFactory;

  /** LDAP connection. */
  protected ProviderConnection providerConnection;


  /** Default constructor. */
  public Connection() {}


  /**
   * Creates a new ldap connection.
   *
   * @param  ldapUrl  to connect to
   */
  public Connection(final String ldapUrl)
  {
    this(new ConnectionConfig(ldapUrl));
  }


  /**
   * Creates a new ldap connection.
   *
   * @param  cc  connection configuration
   */
  public Connection(final ConnectionConfig cc)
  {
    setConnectionConfig(cc);
  }


  /**
   * Returns the connection configuration.
   *
   * @return  connection configuration
   */
  public ConnectionConfig getConnectionConfig()
  {
    return config;
  }


  /**
   * Sets the connection configuration.
   *
   * @param  cc  connection configuration
   *
   * @throws  IllegalStateException  if this connection has already been
   * initialized
   */
  public void setConnectionConfig(final ConnectionConfig cc)
  {
    if (providerConnectionFactory != null) {
      throw new IllegalStateException(
        "Cannot set configuration after connection has been initialized");
    }
    config = cc;
  }


  /**
   * Prepares this ldap connection for use. This method should only be invoked
   * if provider connection factory needs to be modified before the connection
   * is opened.
   */
  public synchronized void initialize()
  {
    if (providerConnectionFactory == null) {
      providerConnectionFactory =
        config.getProvider().getConnectionFactory(config);
    }
  }


  /**
   * This will establish a connection if one does not already exist by binding
   * to the LDAP using parameters given by
   * {@link ConnectionConfig#getBindDn()} and
   * {@link ConnectionConfig#getBindCredential()}. If these parameters
   * have not been set then an anonymous bind will be attempted. This connection
   * should be closed using {@link #close()}.
   *
   * @throws  LdapException  if the LDAP cannot be reached
   */
  public synchronized void open()
    throws LdapException
  {
    open(config.getBindDn(), config.getBindCredential());
  }


  /**
   * This will establish a connection if one does not already exist by binding
   * to the LDAP using the supplied dn and credential. This connection should be
   * closed using {@link #close()}. See {@link #initialize()}.
   *
   * @param  bindDn  to bind to the LDAP as
   * @param  bindCredential  to bind to the LDAP with
   *
   * @throws  IllegalStateExcepiton  if the connection is already open
   * @throws  LdapException  if the LDAP cannot be reached
   */
  public synchronized void open(
    final String bindDn, final Credential bindCredential)
    throws LdapException
  {
    if (providerConnection != null) {
      throw new IllegalStateException("Connection already open");
    }
    initialize();
    providerConnection = providerConnectionFactory.create(
      new BindRequest(bindDn, bindCredential, config.getSaslConfig()));
  }


  /** This will close the connection to the LDAP. */
  public synchronized void close()
  {
    try {
      if (providerConnection != null) {
        providerConnection.close();
      }
    } catch (LdapException e) {
      logger.warn("Error closing connection with the LDAP", e);
    } finally {
      providerConnection = null;
    }
  }


  /**
   * Returns the provider specific connection. Must be called after a successful
   * call to {@link #open()}.
   *
   * @return  provider connection
   *
   * @throws  IllegalStateException  if the connection is not open
   */
  public ProviderConnection getProviderConnection()
  {
    if (providerConnection == null) {
      throw new IllegalStateException("Connection is not open");
    }
    return providerConnection;
  }


  /**
   * Returns the provider specific connection factory. Must be called after a
   * successful call to {@link #initialize()}.
   *
   * @return  provider connection
   */
  public ProviderConnectionFactory<?> getProviderConnectionFactory()
  {
    if (providerConnectionFactory == null) {
      throw new IllegalStateException("Connection is not initialized");
    }
    return providerConnectionFactory;
  }


  /**
   * Convenience method for performing an ldap anonymous bind operation.
   *
   * @throws  LdapException  if an error occurs
   */
  public void bind()
    throws LdapException
  {
    final BindOperation op = new BindOperation(this);
    op.execute(new BindRequest());
  }


  /**
   * Convenience method for performing an ldap bind operation.
   *
   * @param  dn  to bind as
   * @param  c  credential to bind with
   *
   * @throws  LdapException  if an error occurs
   */
  public void bind(final String dn, final Credential c)
    throws LdapException
  {
    final BindOperation op = new BindOperation(this);
    op.execute(new BindRequest(dn, c));
  }


  /**
   * Convenience method for performing an ldap add operation.
   *
   * @param  dn  to add
   * @param  attrs  to add
   * @throws  LdapException  if an error occurs
   */
  public void add(final String dn, final Collection<LdapAttribute> attrs)
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
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::config=%s, providerConnectionFactory=%s, " +
        "providerConnection=%s]",
        getClass().getName(),
        hashCode(),
        config,
        providerConnectionFactory,
        providerConnection);
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
      close();
    } finally {
      super.finalize();
    }
  }
}
