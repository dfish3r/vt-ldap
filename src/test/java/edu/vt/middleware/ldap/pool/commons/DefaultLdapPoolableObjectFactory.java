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

import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.pool.DefaultLdapFactory;
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * Provides a implementation of a commons pooling poolable object factory for
 * testing.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultLdapPoolableObjectFactory extends DefaultLdapFactory
  implements PoolableObjectFactory
{


  /**
   * Creates a new default ldap poolable object factory.
   *
   * @param  lcc  ldap connection config
   */
  public DefaultLdapPoolableObjectFactory(final LdapConnectionConfig lcc)
  {
    super(lcc);
  }


  /** {@inheritDoc} */
  @Override
  public void activateObject(final Object obj)
  {
    activate((LdapConnection) obj);
  }


  /** {@inheritDoc} */
  @Override
  public void destroyObject(final Object obj)
  {
    destroy((LdapConnection) obj);
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
    passivate((LdapConnection) obj);
  }


  /** {@inheritDoc} */
  @Override
  public boolean validateObject(final Object obj)
  {
    return validate((LdapConnection) obj);
  }
}
