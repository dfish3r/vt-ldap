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
import edu.vt.middleware.ldap.provider.ProviderConnectionFactory;

/**
 * Provides an interface for creating JNDI specific provider connections.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface JndiProviderConnectionFactory
  extends ProviderConnectionFactory<JndiProviderConfig>
{


  /**
   * Prepares this connection factory for use by inspecting the connection
   * configuration properties.
   *
   * @param  cc  connection config
   */
  void initialize(ConnectionConfig cc);
}
