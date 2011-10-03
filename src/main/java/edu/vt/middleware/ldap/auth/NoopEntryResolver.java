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
package edu.vt.middleware.ldap.auth;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;

/**
 * Returns an LDAP entry that contains only the DN that was supplied to it.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NoopEntryResolver implements EntryResolver
{


  /** Default constructor. */
  public NoopEntryResolver() {}


  /** {@inheritDoc} */
  @Override
  public LdapEntry resolve(
    final Connection conn, final AuthenticationCriteria ac)
    throws LdapException
  {
    return new LdapEntry(ac.getDn());
  }
}
