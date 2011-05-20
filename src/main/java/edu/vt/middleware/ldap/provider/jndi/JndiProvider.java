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

/**
 * Exposes a connection factory for creating ldap connections with JNDI.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiProvider implements Provider
{


  /** {@inheritDoc} */
  @Override
  public ProviderConnectionFactory getConnectionFactory(
    final ConnectionConfig lcc)
  {
    ProviderConnectionFactory cf = null;
    if (lcc.isTlsEnabled()) {
      cf = JndiTlsConnectionFactory.newInstance(lcc);
    } else {
      cf = JndiConnectionFactory.newInstance(lcc);
    }
    return cf;
  }
}
