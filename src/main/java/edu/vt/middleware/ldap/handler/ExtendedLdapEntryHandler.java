/*
  $Id: ExtendedLdapResultHandler.java 2097 2011-08-29 15:58:30Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 2097 $
  Updated: $Date: 2011-08-29 11:58:30 -0400 (Mon, 29 Aug 2011) $
*/
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.Connection;

/**
 * Provides an interface for entry handlers that require the use of the
 * connection that was used to perform the original search.
 *
 * @author  Middleware Services
 * @version  $Revision: 2097 $
 */
public interface ExtendedLdapEntryHandler extends LdapEntryHandler
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
