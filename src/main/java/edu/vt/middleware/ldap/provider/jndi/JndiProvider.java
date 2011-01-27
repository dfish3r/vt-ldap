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

import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.provider.ConnectionFactory;
import edu.vt.middleware.ldap.provider.LdapProvider;

/**
 * Exposes a connection factory for creating ldap connections with JNDI.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiProvider implements LdapProvider
{


  /** {@inheritDoc} */
  public ConnectionFactory getConnectionFactory(final LdapConfig lc)
  {
    ConnectionFactory cf = null;
    if (lc.isTlsEnabled()) {
      cf = JndiTlsConnectionFactory.newInstance(lc);
    } else {
      cf = JndiConnectionFactory.newInstance(lc);
    }
    return cf;
  }
}
