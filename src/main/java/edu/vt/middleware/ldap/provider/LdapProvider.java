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
package edu.vt.middleware.ldap.provider;

import edu.vt.middleware.ldap.LdapConfig;

/**
 * Provides access to a provider specific connection factory.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public interface LdapProvider
{


  /**
   * Returns the connection factory for this provider.
   *
   * @param  lc  ldap configuration
   * @return  connection factory
   */
  ConnectionFactory getConnectionFactory(LdapConfig lc);
}
