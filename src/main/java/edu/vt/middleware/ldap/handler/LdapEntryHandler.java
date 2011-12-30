/*
  $Id: LdapResultHandler.java 2193 2011-12-15 22:01:04Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 2193 $
  Updated: $Date: 2011-12-15 17:01:04 -0500 (Thu, 15 Dec 2011) $
*/
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;

/**
 * Provides post search processing of an ldap entry.
 *
 * @author  Middleware Services
 * @version  $Revision: 2193 $
 */
public interface LdapEntryHandler
{


  /**
   * Process an entry from an ldap search.
   *
   * @param  criteria  search criteria used to perform the search
   * @param  entry  search result
   *
   * @return  handler result
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  HandlerResult process(SearchCriteria criteria, LdapEntry entry)
    throws LdapException;
}
