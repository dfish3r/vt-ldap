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
package edu.vt.middleware.ldap;

/**
 * Executes an ldap add operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class AddOperation extends AbstractLdapOperation<AddRequest, Void>
{


  /**
   * Creates a new add operation.
   *
   * @param  lc  ldap connection
   */
  public AddOperation(final LdapConnection lc)
  {
    this.ldapConnection = lc;
    this.initialize(lc.getLdapConnectionConfig());
  }


  /** {@inheritDoc} */
  protected LdapResponse<Void> invoke(final AddRequest request)
    throws LdapException
  {
    this.ldapConnection.getProviderConnection().add(request);
    return new LdapResponse<Void>();
  }


  /** {@inheritDoc} */
  protected void initializeRequest(
    final AddRequest request, final LdapConnectionConfig config) {}
}
