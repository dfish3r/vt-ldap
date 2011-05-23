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

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.pool.DefaultConnectionFactory;
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * Provides a implementation of a commons pooling poolable object factory for
 * testing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultLdapPoolableObjectFactory extends DefaultConnectionFactory
  implements PoolableObjectFactory
{


  /**
   * Creates a new default ldap poolable object factory.
   *
   * @param  lcc  ldap connection config
   */
  public DefaultLdapPoolableObjectFactory(final ConnectionConfig lcc)
  {
    super(lcc);
  }


  /** {@inheritDoc} */
  @Override
  public void activateObject(final Object obj)
  {
    activate((Connection) obj);
  }


  /** {@inheritDoc} */
  @Override
  public void destroyObject(final Object obj)
  {
    destroy((Connection) obj);
  }


  /** {@inheritDoc} */
  @Override
  public Object makeObject()
  {
    return create();
  }


  /** {@inheritDoc} */
  @Override
  public void passivateObject(final Object obj)
  {
    passivate((Connection) obj);
  }


  /** {@inheritDoc} */
  @Override
  public boolean validateObject(final Object obj)
  {
    return validate((Connection) obj);
  }
}
