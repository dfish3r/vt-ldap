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
public class RenameOperation extends AbstractOperation<RenameRequest, Void>
{


  /**
   * Creates a new rename operation.
   *
   * @param  c  connection
   */
  public RenameOperation(final Connection c)
  {
    connection = c;
    initialize(c.getConnectionConfig());
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Void> invoke(final RenameRequest request)
    throws LdapException
  {
    connection.getProviderConnection().rename(request);
    return new Response<Void>();
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final RenameRequest request, final ConnectionConfig config) {}
}
