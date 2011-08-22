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

import java.io.Serializable;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;

/**
 * Returns an LDAP entry that contains only the DN that was supplied to it.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NoopEntryResolver implements EntryResolver, Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -1296734590602210039L;

  /** Default constructor. */
  public NoopEntryResolver() {}


  /**
   * Returns an ldap entry that contains the supplied dn.
   *
   * @param  connection  that authentication occurred on
   * @param  dn  that authenticated
   *
   * @return  ldap entry
   *
   * @throws  LdapException  never
   */
  public LdapEntry resolve(final Connection connection, final String dn)
    throws LdapException
  {
    return new LdapEntry(dn);
  }
}
