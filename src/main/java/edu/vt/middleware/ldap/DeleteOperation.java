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
 * Executes an ldap delete operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class DeleteOperation extends AbstractLdapOperation<DeleteRequest, Void>
{


  /**
   * Creates a new delete operation.
   *
   * @param  lc  ldap connection
   */
  public DeleteOperation(final LdapConnection lc)
  {
    ldapConnection = lc;
    initialize(lc.getLdapConnectionConfig());
  }


  /** {@inheritDoc} */
  @Override
  protected LdapResponse<Void> invoke(final DeleteRequest request)
    throws LdapException
  {
    ldapConnection.getProviderConnection().delete(request);
    return new LdapResponse<Void>();
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final DeleteRequest request, final LdapConnectionConfig config) {}
}
