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

import java.util.Arrays;
import edu.vt.middleware.ldap.control.Control;

/**
 * Wrapper class for all operation responses.
 *
 * @param  <T>  type of ldap result contained in this response
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class Response<T> implements Message
{

  /** Operation response. */
  private final T result;

  /** Operation result code. */
  private final ResultCode code;

  /** request controls. */
  private final Control[] controls;


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   * @param  rc  result code
   */
  public Response(final T t, final ResultCode rc)
  {
    result = t;
    code = rc;
    controls = null;
  }


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   * @param  rc  result code
   * @param  c  response controls
   */
  public Response(final T t, final ResultCode rc, final Control[] c)
  {
    result = t;
    code = rc;
    controls = c;
  }


  /**
   * Returns the result of the ldap operation.
   *
   * @return  operation result
   */
  public T getResult()
  {
    return result;
  }


  /**
   * Returns the result code of the ldap operation.
   *
   * @return  operation result code
   */
  public ResultCode getResultCode()
  {
    return code;
  }


  /** {@inheritDoc} */
  @Override
  public Control[] getControls()
  {
    return controls;
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
        "[%s@%d::result=%s, resultCode=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        result,
        code,
        controls != null ? Arrays.asList(controls) : null);
  }
}
