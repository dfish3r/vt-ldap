/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
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
import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import edu.vt.middleware.ldap.auth.AuthorizationException;

/**
 * <code>TestAuthenticationResultHandler</code>.
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
  public void process(final AuthenticationCriteria ac, final LdapContext ctx)
    throws NamingException
  {
    if (!succeed) {
      throw new AuthorizationException("Succeed is false");
    }
    this.results.add(ac.getDn());
  }


  /**
   * Returns the authentication results.
   *
   * @return  authentication results
   */
  public List<String> getResults()
  {
    return this.results;
  }


  /**
   * Sets whether process will succeed.
   *
   * @param  b  <code>boolean</code>
   */
  public void setSucceed(final boolean b)
  {
    succeed = b;
  }
}
