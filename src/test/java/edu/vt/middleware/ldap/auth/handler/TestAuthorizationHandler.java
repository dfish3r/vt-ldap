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
package edu.vt.middleware.ldap.auth.handler;

import java.util.ArrayList;
import java.util.List;
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.auth.AuthorizationException;

/**
 * Class for testing that authorization handlers are firing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class TestAuthorizationHandler implements AuthorizationHandler
{

  /** results. */
  private List<String> results = new ArrayList<String>();

  /** whether process should succeed. */
  private boolean succeed;


  /** {@inheritDoc} */
  @Override
  public void process(
    final AuthenticationCriteria ac, final LdapConnection conn)
    throws LdapException
  {
    if (!succeed) {
      throw new AuthorizationException("Succeed is false");
    }
    results.add(ac.getDn());
  }


  /**
   * Returns the authentication results.
   *
   * @return  authentication results
   */
  public List<String> getResults()
  {
    return results;
  }


  /**
   * Sets whether process will succeed.
   *
   * @param  b  whether process succeed
   */
  public void setSucceed(final boolean b)
  {
    succeed = b;
  }
}
