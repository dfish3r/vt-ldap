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
  extends AbstractLdapOperation<CompareRequest, Boolean>
{


  /**
   * Creates a new compare operation.
   *
   * @param  lc  ldap connection
   */
  public CompareOperation(final LdapConnection lc)
  {
    ldapConnection = lc;
    initialize(lc.getLdapConnectionConfig());
  }


  /** {@inheritDoc} */
  @Override
  protected LdapResponse<Boolean> invoke(final CompareRequest request)
    throws LdapException
  {
    return new LdapResponse<Boolean>(
      ldapConnection.getProviderConnection().compare(request));
  }


  /** {@inheritDoc} */
  @Override
  protected void initializeRequest(
    final CompareRequest request, final LdapConnectionConfig config) {}
}
