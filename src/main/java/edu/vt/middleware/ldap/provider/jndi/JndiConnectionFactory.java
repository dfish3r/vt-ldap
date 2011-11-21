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
package edu.vt.middleware.ldap.provider.jndi;

import java.util.Hashtable;
import java.util.Map;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.provider.AbstractConnectionFactory;
import edu.vt.middleware.ldap.provider.ConnectionException;

/**
 * Creates connections using the JNDI {@link InitialLdapContext} class.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class JndiConnectionFactory extends
  AbstractConnectionFactory<JndiProviderConfig>
{

  /** Environment properties. */
  private Map<String, Object> environment;


  /**
   * Creates a new jndi connection factory.
   *
   * @param  url  of the ldap to connect to
   * @param  env  jndi context environment
   */
  public JndiConnectionFactory(final String url, final Map<String, Object> env)
  {
    if (url == null) {
      throw new IllegalArgumentException("LDAP URL cannot be null");
    }
    ldapUrl = url;
    environment = env;
  }


  /** {@inheritDoc} */
  @Override
  protected JndiConnection createInternal(final String url)
    throws LdapException
  {
    final Hashtable<String, Object> env = new Hashtable<String, Object>(
      environment);
    env.put(JndiProvider.PROVIDER_URL, url);
    if (config.getTracePackets() != null) {
      env.put(JndiProvider.TRACE, config.getTracePackets());
    }

    JndiConnection conn = null;
    try {
      conn = new JndiConnection(new InitialLdapContext(env, null));
      conn.setRemoveDnUrls(config.getRemoveDnUrls());
      conn.setOperationRetryResultCodes(config.getOperationRetryResultCodes());
      conn.setSearchIgnoreResultCodes(config.getSearchIgnoreResultCodes());
      conn.setControlProcessor(config.getControlProcessor());
    } catch (NamingException e) {
      throw new ConnectionException(
        e, NamingExceptionUtil.getResultCode(e.getClass()));
    }
    return conn;
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
        "[%s@%d::config=%s]",
        getClass().getName(),
        hashCode(),
        config);
  }
}
