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

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.Response;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.control.ResponseControl;

/**
 * Response object for authentication handlers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticationHandlerResponse extends Response<Boolean>
{

  /** Connection that authentication occurred on. */
  private final Connection connection;


  /**
   * Creates a new authentication response.
   *
   * @param  success  authentication result
   * @param  rc  result code from the underlying ldap operation
   * @param  conn  connection the authentication occurred on
   */
  public AuthenticationHandlerResponse(
    final Boolean success, final ResultCode rc, final Connection conn)
  {
    super(success, rc);
    connection = conn;
  }


  /**
   * Creates a new ldap response.
   *
   * @param  success  authentication result
   * @param  rc  result code from the underlying ldap operation
   * @param  conn  connection the authentication occurred on
   * @param  controls  response controls from the underlying ldap operation
   */
  public AuthenticationHandlerResponse(
    final Boolean success,
    final ResultCode rc,
    final Connection conn,
    final ResponseControl[] controls)
  {
    super(success, rc, controls);
    connection = conn;
  }


  /**
   * Returns the connection that the ldap operation occurred on.
   *
   * @return  connection
   */
  public Connection getConnection()
  {
    return connection;
  }
}
