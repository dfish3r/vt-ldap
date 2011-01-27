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

import java.util.HashMap;
import java.util.Map;

/**
 * Class for testing that authentication result handlers are firing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class TestAuthenticationResultHandler
  implements AuthenticationResultHandler
{

  /** results. */
  private Map<String, Boolean> results = new HashMap<String, Boolean>();


  /** {@inheritDoc} */
  public void process(final AuthenticationCriteria ac, final boolean success)
  {
    this.results.put(ac.getDn(), Boolean.valueOf(success));
  }


  /**
   * Returns the authentication results.
   *
   * @return  authentication results
   */
  public Map<String, Boolean> getResults()
  {
    return this.results;
  }
}
