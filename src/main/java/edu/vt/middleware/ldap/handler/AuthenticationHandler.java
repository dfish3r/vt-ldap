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

/**
 * AuthenticationHandler provides post processing of authentication results.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface AuthenticationHandler
{


  /**
   * Process the results from a ldap authentication.
   *
   * @param  ac  <code>AuthenticationCriteria</code> used to perform
   * the authentication
   * @param  success  <code>boolean</code> whether the authentication succeeded
   */
  void process(AuthenticationCriteria ac, boolean success);
}
