/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.bean.LdapBeanFactory;
import edu.vt.middleware.ldap.bean.LdapBeanProvider;
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
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Ldap object to use for searching. */
  protected LdapPool<Ldap> pool;

  /** Ldap bean factory. */
  protected LdapBeanFactory beanFactory = LdapBeanProvider.getLdapBeanFactory();


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
   * Returns the factory for creating ldap beans.
   *
   * @return  <code>LdapBeanFactory</code>
   */
  public LdapBeanFactory getLdapBeanFactory()
  {
    return this.beanFactory;
  }


  /**
   * Sets the factory for creating ldap beans.
   *
   * @param  lbf  <code>LdapBeanFactory</code>
   */
  public void setLdapBeanFactory(final LdapBeanFactory lbf)
  {
    if (lbf != null) {
      this.beanFactory = lbf;
    }
  }


  /**
   * This will perform an LDAP search with the supplied query and return
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
          if (this.logger.isErrorEnabled()) {
            this.logger.error("Error attempting LDAP search", e);
          }
          throw e;
        } finally {
          this.pool.checkIn(ldap);
        }
      } catch (Exception e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("Error using LDAP pool", e);
        }
      }
    }
    return queryResults;
  }


  /**
   * This will perform an LDAP search with the supplied query and return
   * attributes. The results will be written to the supplied <code>
   * Writer</code>.
   *
   * @param  query  <code>String</code> to search for
   * @param  attrs  <code>String[]</code> to return
   * @param  writer  <code>Writer</code> to write to
   *
   * @throws  NamingException  if an error occurs while searching
   * @throws  IOException  if an error occurs while writing search results
   */
  public void search(
    final String query,
    final String[] attrs,
    final Writer writer)
    throws NamingException, IOException
  {
    final LdapResult lr = this.beanFactory.newLdapResult();
    lr.addEntries(this.search(query, attrs));
    writer.write(lr.toString());
    writer.flush();
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
