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

/**
 * Wrapper class for all operation responses.
 *
 * @param  <T>  type of ldap result contained in this response
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdapResponse<T>
{
  /** Operation response. */
  private T result;


  /** Default constructor. */
  public LdapResponse() {}


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   */
  public LdapResponse(final T t)
  {
    this.result = t;
  }


  /**
   * Returns the result of the ldap operation.
   *
   * @return  operation result
   */
  public T getResult()
  {
    return this.result;
  }
}
