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
package edu.vt.middleware.ldap.bean;

/**
 * <code>LdapBeanFactory</code> provides an interface for ldap bean type
 * factories.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapBeanFactory
{


  /**
   * Create a new instance of <code>LdapResult</code>.
   *
   * @return  <code>LdapResult</code>
   */
  LdapResult newLdapResult();


  /**
   * Create a new instance of <code>LdapEntry</code>.
   *
   * @return  <code>LdapEntry</code>
   */
  LdapEntry newLdapEntry();


  /**
   * Create a new instance of <code>LdapAttributes</code>.
   *
   * @return  <code>LdapAttributes</code>
   */
  LdapAttributes newLdapAttributes();


  /**
   * Create a new instance of <code>LdapAttribute</code>.
   *
   * @return  <code>LdapAttribute</code>
   */
  LdapAttribute newLdapAttribute();
}
