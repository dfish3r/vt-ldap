/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>TestAuthenticationHandler</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class TestAuthenticationHandler implements AuthenticationHandler
{
  /** results. */
  private Map<String, Boolean> results = new HashMap<String, Boolean>();


  /** {@inheritDoc}. */
  public void process(final AuthenticationCriteria ac, final boolean success)
  {
    this.results.put(ac.getDn(), Boolean.valueOf(success));
  }


  /**
   * Returns the authentication results.
   *
   * @return authentication results
   */
  public Map<String, Boolean> getResults()
  {
    return this.results;
  }
}
