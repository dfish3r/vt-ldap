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
package edu.vt.middleware.ldap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.bean.LdapResult;
import edu.vt.middleware.ldap.pool.LdapPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>LdapSearch</code> queries an LDAP and returns the result. Each instance
 * of <code>LdapSearch</code> maintains it's own pool of LDAP connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class LdapSearch
{

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(LdapSearch.class);

  /** Ldap object to use for searching. */
  protected LdapPool<Ldap> pool;


  /**
   * This creates a new <code>LdapSearch</code> with the supplied pool.
   *
   * @param  pool  <code>LdapPool</code>
   */
  public LdapSearch(final LdapPool<Ldap> pool)
  {
    this.pool = pool;
  }


  /**
   * This will perform a LDAP search with the supplied query and return
   * attributes.
   *
   * @param  query  <code>String</code> to search for
   * @param  attrs  <code>String[]</code> to return
   *
   * @return  <code>Iterator</code> of search results
   *
   * @throws  NamingException  if an error occurs while searching
   */
  public Iterator<SearchResult> search(final String query, final String[] attrs)
    throws NamingException
  {
    Iterator<SearchResult> queryResults = null;
    if (query != null) {
      try {
        Ldap ldap = null;
        try {
          ldap = this.pool.checkOut();
          queryResults = ldap.search(new SearchFilter(query), attrs);
        } catch (NamingException e) {
          if (LOG.isErrorEnabled()) {
            LOG.error("Error attempting LDAP search", e);
          }
          throw e;
        } finally {
          this.pool.checkIn(ldap);
        }
      } catch (Exception e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error using LDAP pool", e);
        }
      }
    }
    return queryResults;
  }


  /**
   * This will perform a LDAP search with the supplied query and return
   * attributes. The results will be written to the supplied <code>
   * OutputStream</code>.
   *
   * @param  query  <code>String</code> to search for
   * @param  attrs  <code>String[]</code> to return
   * @param  out  <code>OutputStream</code> to write to
   *
   * @throws  NamingException  if an error occurs while searching
   * @throws  IOException  if an error occurs while writing search results
   */
  public void searchToStream(
    final String query,
    final String[] attrs,
    final OutputStream out)
    throws NamingException, IOException
  {
    final LdapResult lr = new LdapResult(this.search(query, attrs));
    out.write(lr.toString().getBytes());
  }


  /**
   * This will perform a LDAP search with the supplied query and return
   * attributes.
   *
   * @param  query  <code>String</code> to search for
   * @param  attrs  <code>String[]</code> to return
   *
   * @return  <code>String</code> of formatted results
   *
   * @throws  NamingException  if an error occurs while searching
   * @throws  IOException  if an error occurs while writing search results
   */
  public String searchToString(final String query, final String[] attrs)
    throws NamingException, IOException
  {
    final LdapResult lr = new LdapResult(this.search(query, attrs));
    return lr.toString();
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
   * Called by the garbage collector on an object when garbage collection
   * determines that there are no more references to the object.
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
