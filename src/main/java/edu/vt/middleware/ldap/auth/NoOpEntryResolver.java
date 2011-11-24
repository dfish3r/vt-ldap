/*
  $Id: NoopEntryResolver.java 2115 2011-10-03 14:43:46Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 2115 $
  Updated: $Date: 2011-10-03 10:43:46 -0400 (Mon, 03 Oct 2011) $
*/
package edu.vt.middleware.ldap.auth;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;

/**
 * Returns an LDAP entry that contains only the DN that was supplied to it.
 *
 * @author  Middleware Services
 * @version  $Revision: 2115 $ $Date: 2011-10-03 10:43:46 -0400 (Mon, 03 Oct 2011) $
 */
public class NoOpEntryResolver implements EntryResolver
{


  /** Default constructor. */
  public NoOpEntryResolver() {}


  /** {@inheritDoc} */
  @Override
  public LdapEntry resolve(
    final Connection conn, final AuthenticationCriteria ac)
    throws LdapException
  {
    return new LdapEntry(ac.getDn());
  }
}
