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
 * Executes an ldap rename operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class RenameOperation extends AbstractLdapOperation<RenameRequest, Void>
{


  /**
   * Creates a new rename operation.
   *
   * @param  lc  ldap connection
   */
  public RenameOperation(final LdapConnection lc)
  {
    ldapConnection = lc;
    initialize(lc.getLdapConnectionConfig());
  }


  /** {@inheritDoc} */
  @Override
  protected LdapResponse<Void> invoke(final RenameRequest request)
    throws LdapException
  {
    ldapConnection.getProviderConnection().rename(request);
    return new LdapResponse<Void>();
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final RenameRequest request, final LdapConnectionConfig config) {}
}
