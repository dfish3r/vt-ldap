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
package edu.vt.middleware.ldap.pool.commons;

import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.pool.DefaultLdapFactory;
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * <code>DefaultLdapPoolableObjectFactory</code> provides a implementation of a
 * commons pooling <code>PoolableObjectFactory</code> for testing.
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
    this.activate((Ldap) obj);
  }


  /** {@inheritDoc} */
  public void destroyObject(final Object obj)
  {
    this.destroy((Ldap) obj);
  }


  /** {@inheritDoc} */
  public Object makeObject()
  {
    return this.create();
  }


  /** {@inheritDoc} */
  public void passivateObject(final Object obj)
  {
    this.passivate((Ldap) obj);
  }


  /** {@inheritDoc} */
  public boolean validateObject(final Object obj)
  {
    return this.validate((Ldap) obj);
  }
}
