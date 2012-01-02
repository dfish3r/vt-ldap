/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.provider.Provider;
import edu.vt.middleware.ldap.provider.jndi.JndiProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates connections for performing ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DefaultConnectionFactory implements ConnectionFactory
{

  /** Ldap provider class name. */
  public static final String PROVIDER = "edu.vt.middleware.ldap.provider";

  /** Static reference to the default ldap provider. */
  private static final Provider<?> DEFAULT_PROVIDER = getDefaultProvider();

  /** Provider used by this factory. */
  private Provider<?> provider = DEFAULT_PROVIDER.newInstance();

  /** Connection configuration used by this factory. */
  private ConnectionConfig config;


  /** Default constructor. */
  public DefaultConnectionFactory() {}


  /**
   * Creates a new connection factory.
   *
   * @param  ldapUrl  to connect to
   */
  public DefaultConnectionFactory(final String ldapUrl)
  {
    setConnectionConfig(new ConnectionConfig(ldapUrl));
  }


  /**
   * Creates a new connection factory.
   *
   * @param  cc  connection configuration
   */
  public DefaultConnectionFactory(final ConnectionConfig cc)
  {
    setConnectionConfig(cc);
  }


  /**
   * Creates a new connection factory.
   *
   * @param  cc  connection configuration
   * @param  p  provider
   */
  public DefaultConnectionFactory(
    final ConnectionConfig cc,
    final Provider<?> p)
  {
    setConnectionConfig(cc);
    setProvider(p);
  }


  /**
   * Returns the connection config.
   *
   * @return  connection config
   */
  public ConnectionConfig getConnectionConfig()
  {
    return config;
  }


  /**
   * Sets the connection config. Once invoked the supplied connection config is
   * made immutable. See {@link ConnectionConfig#makeImmutable()}.
   *
   * @param  cc  connection config
   */
  public void setConnectionConfig(final ConnectionConfig cc)
  {
    config = cc;
    config.makeImmutable();
  }


  /**
   * Returns the ldap provider.
   *
   * @return  ldap provider
   */
  public Provider<?> getProvider()
  {
    return provider;
  }


  /**
   * Sets the ldap provider.
   *
   * @param  p  ldap provider to set
   */
  public void setProvider(final Provider<?> p)
  {
    provider = p;
  }


  /**
   * Creates a new connection. Connections returned from this method must be
   * opened before they can perform ldap operations.
   *
   * @return  connection
   */
  public Connection getConnection()
  {
    return new DefaultConnection(config, provider.getConnectionFactory(config));
  }


  /**
   * Creates a new connection. Connections returned from this method must be
   * opened before they can be used.
   *
   * @param  ldapUrl  to connect to
   *
   * @return  connection
   */
  public static Connection getConnection(final String ldapUrl)
  {
    final Provider<?> p = DEFAULT_PROVIDER.newInstance();
    final ConnectionConfig cc = new ConnectionConfig(ldapUrl);
    cc.makeImmutable();
    return new DefaultConnection(cc, p.getConnectionFactory(cc));
  }


  /**
   * Creates a new connection. Connections returned from this method must be
   * opened before they can be used.
   *
   * @param  cc  connection configuration
   *
   * @return  connection
   */
  public static Connection getConnection(final ConnectionConfig cc)
  {
    final Provider<?> p = DEFAULT_PROVIDER.newInstance();
    cc.makeImmutable();
    return new DefaultConnection(cc, p.getConnectionFactory(cc));
  }


  /**
   * The {@link #PROVIDER} property is checked and that class is loaded if
   * provided. Otherwise the JNDI provider is returned.
   *
   * @return  default provider
   */
  public static Provider<?> getDefaultProvider()
  {
    Provider<?> p = null;
    final String providerClass = System.getProperty(PROVIDER);
    if (providerClass != null) {
      final Logger l = LoggerFactory.getLogger(DefaultConnectionFactory.class);
      try {
        if (l.isInfoEnabled()) {
          l.info("Setting ldap provider to {}", providerClass);
        }
        p = (Provider<?>) Class.forName(providerClass).newInstance();
      } catch (Exception e) {
        if (l.isErrorEnabled()) {
          l.error("Error instantiating {}", providerClass, e);
        }
        throw new IllegalStateException(e);
      }
    } else {
      // set the default ldap provider to JNDI
      p = new JndiProvider();
    }
    return p;
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
        "[%s@%d::provider=%s, config=%s]",
        getClass().getName(),
        hashCode(),
        provider,
        config);
  }


  /**
   * Default implementation for managing a connection to an LDAP.
   *
   * @author  Middleware Services
   * @version  $Revision$ $Date$
   */
  protected static class DefaultConnection implements Connection
  {

    /** Logger for this class. */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Connection configuration. */
    private ConnectionConfig config;

    /** Connection factory. */
    private edu.vt.middleware.ldap.provider.ConnectionFactory<?>
    providerConnectionFactory;

    /** Provider connection. */
    private edu.vt.middleware.ldap.provider.Connection providerConnection;


    /**
     * Creates a new connection.
     *
     * @param  cc  connection configuration
     * @param  cf  provider connection factory
     */
    public DefaultConnection(
      final ConnectionConfig cc,
      final edu.vt.middleware.ldap.provider.ConnectionFactory<?> cf)
    {
      config = cc;
      providerConnectionFactory = cf;
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
     * Returns the provider specific connection. Must be called after a
     * successful call to {@link #open()}.
     *
     * @return  provider connection
     *
     * @throws  IllegalStateException  if the connection is not open
     */
    public edu.vt.middleware.ldap.provider.Connection getProviderConnection()
    {
      if (providerConnection == null) {
        throw new IllegalStateException("Connection is not open");
      }
      return providerConnection;
    }


    /**
     * This will establish a connection if one does not already exist by binding
     * to the LDAP using parameters given by {@link
     * ConnectionConfig#getBindDn()}, {@link
     * ConnectionConfig#getBindCredential()}, {@link
     * ConnectionConfig#getBindSaslConfig()}, and {@link
     * ConnectionConfig#getBindControls()}. If these parameters have not been
     * set then an anonymous bind will be attempted. This connection should be
     * closed using {@link #close()}.
     *
     * @return  response associated with the bind operation
     *
     * @throws  LdapException  if the LDAP cannot be reached
     */
    public synchronized Response<Void> open()
      throws LdapException
    {
      final BindRequest request = new BindRequest();
      request.setDn(config.getBindDn());
      request.setCredential(config.getBindCredential());
      request.setSaslConfig(config.getBindSaslConfig());
      request.setControls(config.getBindControls());
      return open(request);
    }


    /**
     * This will establish a connection if one does not already exist by binding
     * to the LDAP using the supplied bind request. This connection should be
     * closed using {@link #close()}.
     *
     * @param  request  bind request
     *
     * @return  response associated with the bind operation
     *
     * @throws  IllegalStateException  if the connection is already open
     * @throws  LdapException  if the LDAP cannot be reached
     */
    public synchronized Response<Void> open(final BindRequest request)
      throws LdapException
    {
      if (providerConnection != null) {
        throw new IllegalStateException("Connection already open");
      }
      providerConnection = providerConnectionFactory.create();
      return providerConnection.bind(request);
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
}
