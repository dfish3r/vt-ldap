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
package edu.vt.middleware.ldap.provider;

import edu.vt.middleware.ldap.AddRequest;
import edu.vt.middleware.ldap.BindRequest;
import edu.vt.middleware.ldap.CompareRequest;
import edu.vt.middleware.ldap.DeleteRequest;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ModifyRequest;
import edu.vt.middleware.ldap.RenameRequest;
import edu.vt.middleware.ldap.Response;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Interface for a provider specific implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public interface Connection
{


  /**
   * Bind to the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the bind operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> bind(BindRequest request)
    throws LdapException;


  /**
   * Add an entry to an ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the add operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> add(AddRequest request)
    throws LdapException;


  /**
   * Compare an entry in the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the compare operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Boolean> compare(CompareRequest request)
    throws LdapException;


  /**
   * Delete an entry in the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the delete operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> delete(DeleteRequest request)
    throws LdapException;


  /**
   * Modify an entry in the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the modify operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> modify(ModifyRequest request)
    throws LdapException;


  /**
   * Rename an entry in the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  response associated with the rename operation
   *
   * @throws  LdapException  if an error occurs
   */
  Response<Void> rename(RenameRequest request)
    throws LdapException;


  /**
   * Search the ldap.
   *
   * @param  request  containing the data necessary to perform the operation
   *
   * @return  search iterator
   *
   * @throws  LdapException  if an error occurs
   */
  SearchIterator search(SearchRequest request)
    throws LdapException;


  /**
   * Tear down this connection to an LDAP.
   *
   * @throws  LdapException  if an LDAP error occurs
   */
  void close()
    throws LdapException;
}
