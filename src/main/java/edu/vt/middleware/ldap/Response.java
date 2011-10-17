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
  private T result;

  /** request controls. */
  private Control[] controls;


  /** Default constructor. */
  public Response() {}


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   */
  public Response(final T t)
  {
    result = t;
  }


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   * @param  c  response controls
   */
  public Response(final T t, final Control[] c)
  {
    result = t;
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


  /** {@inheritDoc} */
  @Override
  public Control[] getControls()
  {
    return controls;
  }
}
