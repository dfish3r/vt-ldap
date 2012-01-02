/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.LdapEntry;

/**
 * Handler result data.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class HandlerResult
{

  /** Ldap entry produced by a handler. */
  private final LdapEntry ldapEntry;

  /** Whether the search operation should be aborted. */
  private final boolean abortSearch;


  /**
   * Creates a new handler result.
   *
   * @param  entry  produced by a handler
   */
  public HandlerResult(final LdapEntry entry)
  {
    ldapEntry = entry;
    abortSearch = false;
  }


  /**
   * Creates a new handler result.
   *
   * @param  entry  produced by a handler
   * @param  abort  whether the search operation should be aborted
   */
  public HandlerResult(final LdapEntry entry, final boolean abort)
  {
    ldapEntry = entry;
    abortSearch = abort;
  }


  /**
   * Returns the ldap entry produced by a handler.
   *
   * @return  ldap entry
   */
  public LdapEntry getLdapEntry()
  {
    return ldapEntry;
  }


  /**
   * Returns whether the search operation should be aborted.
   *
   * @return  whether the search operation should be aborted
   */
  public boolean getAbortSearch()
  {
    return abortSearch;
  }
}
