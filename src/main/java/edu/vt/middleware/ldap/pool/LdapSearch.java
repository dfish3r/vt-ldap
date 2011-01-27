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
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Convenience class which leverages an ldap pool for searching.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapSearch
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Pool of ldap connections. */
  protected LdapPool<LdapConnection> pool;


  /**
   * Creates a new ldap search.
   *
   * @param  lp  ldap pool
   */
  public LdapSearch(final LdapPool<LdapConnection> lp)
  {
    this.pool = lp;
  }


  /**
   * This will perform an LDAP search operation.
   *
   * @param  query  to search for
   * @param  attrs  to return
   *
   * @return  ldap result
   *
   * @throws  LdapException  if an error occurs while searching
   */
  public LdapResult search(final String query, final String[] attrs)
    throws LdapException
  {
    LdapResult result = null;
    if (query != null) {
      try {
        LdapConnection conn = null;
        try {
          conn = this.pool.checkOut();
          final SearchOperation search = new SearchOperation(conn);
          result = search.execute(
            new SearchRequest(new SearchFilter(query), attrs)).getResult();
        } finally {
          this.pool.checkIn(conn);
        }
      } catch (LdapPoolException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("Error using LDAP pool", e);
        }
      }
    }
    return result;
  }


  /**
   * Empties the underlying ldap pool, closing all connections. See {@link
   * LdapPool#close()}.
   */
  public void close()
  {
    this.pool.close();
  }


  /**
   * Closes the underlying ldap pool if the object is garbage collected.
   *
   * @throws  Throwable  if an exception is thrown by this method
   */
  protected void finalize()
    throws Throwable
  {
    try {
      this.close();
    } finally {
      super.finalize();
    }
  }
}
