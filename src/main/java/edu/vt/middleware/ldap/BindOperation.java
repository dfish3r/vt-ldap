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
 * Executes an ldap bind operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BindOperation extends AbstractOperation<BindRequest, Void>
{


  /**
   * Creates a new bind operation.
   *
   * @param  conn  connection
   */
  public BindOperation(final Connection conn)
  {
    super(conn);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Void> invoke(final BindRequest request)
    throws LdapException
  {
    return getConnection().getProviderConnection().bind(request);
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final BindRequest request,
    final ConnectionConfig cc) {}
}
