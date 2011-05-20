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
 * Executes an ldap compare operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class CompareOperation
  extends AbstractOperation<CompareRequest, Boolean>
{


  /**
   * Creates a new compare operation.
   *
   * @param  lc  ldap connection
   */
  public CompareOperation(final Connection lc)
  {
    connection = lc;
    initialize(lc.getConnectionConfig());
  }


  /** {@inheritDoc} */
  @Override
  protected Response<Boolean> invoke(final CompareRequest request)
    throws LdapException
  {
    return new Response<Boolean>(
      connection.getProviderConnection().compare(request));
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final CompareRequest request, final ConnectionConfig config) {}
}
