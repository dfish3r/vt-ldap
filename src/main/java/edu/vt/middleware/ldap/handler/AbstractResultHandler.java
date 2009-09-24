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

import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AbstractResultHandler</code> implements common handler functionality.
 *
 * @param  <R>  type of result
 * @param  <O>  type of output
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractResultHandler<R, O> implements ResultHandler<R, O>
{
  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());


  /**
   * This will enumerate through the supplied <code>NamingEnumeration</code> and
   * return a List of those results. The results are unaltered and the dn is
   * ignored.
   *
   * @param  sc  <code>SearchCriteria</code> used to find enumeration
   * @param  en  <code>NamingEnumeration</code> LDAP results
   *
   * @return  <code>List</code> - LDAP results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public List<O> process(
    final SearchCriteria sc,
    final NamingEnumeration<? extends R> en)
    throws NamingException
  {
    return this.process(sc, en, null);
  }


  /**
   * This will enumerate through the supplied <code>NamingEnumeration</code> and
   * return a List of those results. The results are unaltered and the dn is
   * ignored. Any exceptions passed into this method will be ignored and
   * results will be returned as if no exception occurred.
   *
   * @param  sc  <code>SearchCriteria</code> used to find enumeration
   * @param  en  <code>NamingEnumeration</code> LDAP results
   * @param  ignore  <code>NamingException[]</code> to ignore
   *
   * @return  <code>List</code> - LDAP results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public List<O> process(
    final SearchCriteria sc,
    final NamingEnumeration<? extends R> en,
    final NamingException[] ignore)
    throws NamingException
  {
    final List<O> results = new ArrayList<O>();
    if (en != null) {
      try {
        while (en.hasMore()) {
          final O o = processResult(sc, en.next());
          if (o != null) {
            results.add(o);
          }
        }
      } catch (NamingException e) {
        boolean ignoreException = false;
        if (ignore != null && ignore.length > 0) {
          for (NamingException ne : ignore) {
            if (ne.getClass().isInstance(e)) {
              if (this.logger.isDebugEnabled()) {
                this.logger.debug("Ignoring naming exception", e);
              }
              ignoreException = true;
              break;
            }
          }
        }
        if (!ignoreException) {
          throw e;
        }
      }
    }
    return results;
  }


  /**
   * This will enumerate through the supplied <code>List</code> and return a
   * List of those results. The results are unaltered and the dn is ignored.
   *
   * @param  sc  <code>SearchCriteria</code> used to find enumeration
   * @param  l  <code>List</code> LDAP results
   *
   * @return  <code>List</code> - LDAP results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public List<O> process(final SearchCriteria sc, final List<? extends R> l)
    throws NamingException
  {
    final List<O> results = new ArrayList<O>();
    if (l != null) {
      for (R r : l) {
        final O o = processResult(sc, r);
        if (o != null) {
          results.add(o);
        }
      }
    }
    return results;
  }


  /**
   * Processes the supplied result.
   *
   * @param  sc  <code>SearchCriteria</code> used to retrieve the result
   * @param  r  <code>R</code> result to process
   *
   * @return  <code>O</code> processed result
   *
   * @throws  NamingException  if the supplied result cannot be read
   */
  protected abstract O processResult(final SearchCriteria sc, final R r)
    throws NamingException;
}
