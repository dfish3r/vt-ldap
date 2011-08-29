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

import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.provider.Provider;
import edu.vt.middleware.ldap.provider.ProviderConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exposes a connection factory for creating ldap connections with JNDI.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiProvider implements Provider<JndiProviderConfig>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Provider configuration. */
  private JndiProviderConfig config = new JndiProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ProviderConnectionFactory<JndiProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    JndiProviderConnectionFactory cf = null;
    if (cc.isTlsEnabled()) {
      cf = new JndiTlsConnectionFactory(cc.getLdapUrl());
    } else {
      cf = new JndiConnectionFactory(cc.getLdapUrl());
    }
    config.setSslSocketFactory(cc.getSslSocketFactory());
    config.setHostnameVerifier(cc.getHostnameVerifier());
    cf.setProviderConfig(config);
    cf.initialize(cc);
    return cf;
  }


  /** {@inheritDoc} */
  @Override
  public JndiProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final JndiProviderConfig jpc)
  {
    config = jpc;
  }


  /** {@inheritDoc} */
  @Override
  public JndiProvider newInstance()
  {
    return new JndiProvider();
  }
}
