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
package edu.vt.middleware.ldap.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AbstractLdapBean</code> provides common implementations to other bean
 * objects.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractLdapBean
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Factory for creating ldap beans. */
  protected final LdapBeanFactory beanFactory;


  /**
   * Creates a new <code>AbstractLdapBean</code> with the supplied ldap bean
   * factory.
   *
   * @param  lbf  <code>LdapBeanFactory</code>
   */
  public AbstractLdapBean(final LdapBeanFactory lbf)
  {
    this.beanFactory = lbf;
  }


  /**
   * Returns whether the supplied <code>Object</code> contains the same data as
   * this bean.
   *
   * @param  o  <code>Object</code>
   *
   * @return  <code>boolean</code>
   */
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this ||
        (this.getClass() == o.getClass() && o.hashCode() == this.hashCode());
  }


  /**
   * This returns the hash code for this object.
   *
   * @return  <code>int</code>
   */
  public abstract int hashCode();
}
