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
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.Connection;

/**
 * Provides an interface for search result handlers that require the use of the
 * connection that was used to perform the original search.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ExtendedLdapResultHandler extends LdapResultHandler
{


  /**
   * Gets the connection used by the search operation invoking this handler.
   *
   * @return  connection
   */
  Connection getResultConnection();


  /**
   * Sets the connection used by the search operation invoking this handler.
   *
   * @param  c  connection
   */
  void setResultConnection(final Connection c);
}
