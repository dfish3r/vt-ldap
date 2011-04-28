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
package edu.vt.middleware.ldap.cache;

import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchRequest;
import net.sf.ehcache.Element;

/**
 * Ehcache implementation.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class Ehcache<Q extends SearchRequest> implements Cache<Q>
{

  /** Underlying ehcache. */
  protected net.sf.ehcache.Cache cache;


  /**
   * Creates a new ehcache.
   *
   * @param  c  backing ehcache
   */
  public Ehcache(final net.sf.ehcache.Cache c)
  {
    this.cache = c;
  }


  /**
   * Removes all data from this cache.
   */
  public void clear()
  {
    this.cache.removeAll();
  }


  /** {@inheritDoc} */
  public LdapResult get(final Q request)
  {
    final Element e = this.cache.get(request);
    if (e == null) {
      return null;
    }
    return (LdapResult) e.getObjectValue();
  }


  /** {@inheritDoc} */
  public void put(final Q request, final LdapResult lr)
  {
    this.cache.put(new Element(request, lr));
  }


  /**
   * Returns the number of items in this cache.
   *
   * @return  size of this cache
   */
  public int size()
  {
    return this.cache.getKeysWithExpiryCheck().size();
  }
}
