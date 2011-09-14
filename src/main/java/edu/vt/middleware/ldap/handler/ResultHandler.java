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

import edu.vt.middleware.ldap.LdapException;

/**
 * Provides post search processing of ldap results.
 *
 * @param  <R>  type of result
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ResultHandler<R>
{


  /**
   * Process the results from an ldap search.
   *
   * @param  criteria  search criteria used to perform the search
   * @param  result  search results
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  void process(SearchCriteria criteria, R result)
    throws LdapException;
}
