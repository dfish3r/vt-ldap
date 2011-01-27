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
package edu.vt.middleware.ldap.pool.commons;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Provides a implementation of a commons pooling generic object pool for
 * testing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CommonsLdapPool extends GenericObjectPool
{


  /** Creates a new ldap pool using {@link DefaultLdapPoolableObjectFactory}. */
  public CommonsLdapPool()
  {
    this(new DefaultLdapPoolableObjectFactory());
  }


  /**
   * Creates a new ldap pool using the supplied poolable object factory.
   *
   * @param  poolableObjectFactory  to create Ldap objects with
   */
  public CommonsLdapPool(final PoolableObjectFactory poolableObjectFactory)
  {
    super(poolableObjectFactory);
  }
}
