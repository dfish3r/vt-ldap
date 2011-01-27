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

  /** {@inheritDoc} */
  public void activateObject(final Object obj)
  {
    this.activate((LdapConnection) obj);
  }


  /** {@inheritDoc} */
  public void destroyObject(final Object obj)
  {
    this.destroy((LdapConnection) obj);
  }


  /** {@inheritDoc} */
  public Object makeObject()
  {
    return this.create();
  }


  /** {@inheritDoc} */
  public void passivateObject(final Object obj)
  {
    this.passivate((LdapConnection) obj);
  }


  /** {@inheritDoc} */
  public boolean validateObject(final Object obj)
  {
    return this.validate((LdapConnection) obj);
  }
}
