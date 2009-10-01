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

import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * ResultHandler provides post search processing of ldap results.
 *
 * @param  <R>  type of result
 * @param  <O>  type of output
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ResultHandler<R, O>
{


  /**
   * Process the results from a ldap search.
   *
   * @param  sc  <code>SearchCriteria</code> used to perform the search
   * @param  en  <code>NamingEnumeration</code> of search results
   *
   * @return  <code>List</code> of result objects
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  List<O> process(SearchCriteria sc, NamingEnumeration<? extends R> en)
    throws NamingException;


  /**
   * Process the results from a ldap search.
   *
   * @param  sc  <code>SearchCriteria</code> used to perform the search
   * @param  en  <code>NamingEnumeration</code> of search results
   * @param  ignore  <code>Class[]</code> of exception types to ignore
   * results
   *
   * @return  <code>List</code> of result objects
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  List<O> process(
    SearchCriteria sc,
    NamingEnumeration<? extends R> en,
    Class[] ignore)
    throws NamingException;


  /**
   * Process the results from a ldap search.
   *
   * @param  sc  <code>SearchCriteria</code> used to perform the search
   * @param  l  <code>List</code> of search results
   *
   * @return  <code>List</code> of result objects
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  List<O> process(SearchCriteria sc, List<? extends R> l)
    throws NamingException;
}
