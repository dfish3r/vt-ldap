/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

/**
 * Interface for ldap operations.
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public interface Operation<Q extends Request, S>
{


  /**
   * Execute this ldap operation.
   *
   * @param  request  containing the data required by this operation
   *
   * @return  response for this operation
   *
   * @throws  LdapException  if the operation fails
   */
  Response<S> execute(Q request)
    throws LdapException;
}
