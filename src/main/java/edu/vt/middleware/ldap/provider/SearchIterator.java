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
package edu.vt.middleware.ldap.provider;

import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;

/**
 * Search results iterator.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface SearchIterator
{


  /**
   * Returns true if the iteration has more elements.
   *
   * @return  true if the iterator has more elements
   *
   * @throws  LdapException  if an error occurs
   */
  boolean hasNext() throws LdapException;


  /**
   * Returns the next element in the iteration.
   *
   * @return  the next element in the iteration
   *
   * @throws  LdapException  if an error occurs
   */
  LdapEntry next() throws LdapException;


  /**
   * Close any resources associated with this iterator.
   *
   * @throws  LdapException  if an error occurs
   */
  void close() throws LdapException;
}
