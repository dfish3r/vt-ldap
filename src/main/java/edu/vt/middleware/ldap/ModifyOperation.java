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
package edu.vt.middleware.ldap;

/**
 * Executes an ldap modify operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class ModifyOperation extends AbstractOperation<ModifyRequest, Void>
{


  /**
   * Creates a new modify operation.
   *
   * @param  conn  connection
   */
  public ModifyOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Void> invoke(final ModifyRequest request)
    throws LdapException
  {
    return getConnection().getProviderConnection().modify(request);
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final ModifyRequest request,
    final ConnectionConfig cc) {}
}
