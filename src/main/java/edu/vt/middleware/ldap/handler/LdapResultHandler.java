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

import edu.vt.middleware.ldap.LdapResult;

/**
 * Provides post search processing of ldap search results.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface LdapResultHandler extends ResultHandler<LdapResult>
{


  /**
   * Gets the attribute handlers.
   *
   * @return  attribute handlers
   */
  LdapAttributeHandler[] getAttributeHandler();


  /**
   * Sets the attribute handlers.
   *
   * @param  handlers  attribute handlers
   */
  void setAttributeHandler(final LdapAttributeHandler[] handlers);
}
