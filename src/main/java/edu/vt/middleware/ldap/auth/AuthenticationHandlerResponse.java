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

import java.util.Arrays;
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

  /** Authentication message. */
  private final String message;


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
    message = null;
  }


  /**
   * Creates a new authentication response.
   *
   * @param  success  authentication result
   * @param  rc  result code from the underlying ldap operation
   * @param  conn  connection the authentication occurred on
   * @param  msg  authentication message
   */
  public AuthenticationHandlerResponse(
    final Boolean success,
    final ResultCode rc,
    final Connection conn,
    final String msg)
  {
    super(success, rc);
    connection = conn;
    message = msg;
  }


  /**
   * Creates a new ldap response.
   *
   * @param  success  authentication result
   * @param  rc  result code from the underlying ldap operation
   * @param  conn  connection the authentication occurred on
   * @param  msg  authentication message
   * @param  controls  response controls from the underlying ldap operation
   */
  public AuthenticationHandlerResponse(
    final Boolean success,
    final ResultCode rc,
    final Connection conn,
    final String msg,
    final ResponseControl[] controls)
  {
    super(success, rc, controls);
    connection = conn;
    message = msg;
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


  /**
   * Returns the authentication message.
   *
   * @return  message
   */
  public String getMessage()
  {
    return message;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::connection=%s, message=%s, result=%s, resultCode=%s, "+
        "controls=%s]",
        getClass().getName(),
        hashCode(),
        connection,
        message,
        getResult(),
        getResultCode(),
        Arrays.toString(getControls()));
  }
}
