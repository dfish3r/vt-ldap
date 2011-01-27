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

import edu.vt.middleware.ldap.LdapConnection;

/**
 * Provides an interface for attribute handlers that require the use of the
 * connection that was used to perform the original search.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ExtendedLdapAttributeHandler extends LdapAttributeHandler
{


  /**
   * Gets the connection used by the search operation invoking this handler.
   *
   * @return  ldap connection
   */
  LdapConnection getResultLdapConnection();


  /**
   * Sets the connection used by the search operation invoking this handler.
   *
   * @param  lc  ldap connection
   */
  void setResultLdapConnection(final LdapConnection lc);
}
