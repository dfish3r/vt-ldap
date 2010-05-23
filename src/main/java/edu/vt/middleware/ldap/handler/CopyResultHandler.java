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
package edu.vt.middleware.ldap.handler;

import javax.naming.NamingException;

/**
 * <code>CopyResultHandler</code> converts a NamingEnumeration into a List of
 * ldap results.
 *
 * @param  <T>  type of result
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CopyResultHandler<T> extends AbstractResultHandler<T, T>
{


  /**
   * Returns the supplied result unaltered.
   *
   * @param  sc  <code>SearchCriteria</code> used to retrieve the result
   * @param  r  <code>T</code> to process
   *
   * @return  <code>T</code> result that was supplied
   *
   * @throws  NamingException  if the supplied result cannot be read
   */
  protected T processResult(final SearchCriteria sc, final T r)
    throws NamingException
  {
    return r;
  }
}
