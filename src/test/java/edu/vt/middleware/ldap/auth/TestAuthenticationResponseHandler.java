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
package edu.vt.middleware.ldap.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for testing that authentication response handlers are firing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class TestAuthenticationResponseHandler
  implements AuthenticationResponseHandler
{

  /** results. */
  private Map<String, Boolean> results = new HashMap<String, Boolean>();


  /** {@inheritDoc} */
  @Override
  public void process(final AuthenticationResponse response)
  {
    results.put(response.getLdapEntry().getDn(), response.getResult());
  }


  /**
   * Returns the authentication results.
   *
   * @return  authentication results
   */
  public Map<String, Boolean> getResults()
  {
    return results;
  }
}
