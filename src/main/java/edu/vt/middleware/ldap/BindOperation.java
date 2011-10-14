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
 * Executes an ldap bind operation. If the bind request contains a null dn, then
 * an anonymous bind will be executed.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BindOperation extends AbstractOperation<BindRequest, Void>
{


  /**
   * Creates a new add operation.
   *
   * @param  c  connection
   */
  public BindOperation(final Connection c)
  {
    connection = c;
    initialize(c.getConnectionConfig());
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Void> invoke(final BindRequest request)
    throws LdapException
  {
    if (request.getDn() == null) {
      connection.getProviderConnection().bind();
    } else {
      connection.getProviderConnection().bind(request);
    }
    return new Response<Void>();
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final BindRequest request, final ConnectionConfig cc) {}
}
