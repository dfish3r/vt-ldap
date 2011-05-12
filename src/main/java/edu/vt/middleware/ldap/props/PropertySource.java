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
package edu.vt.middleware.ldap.props;

/**
 * Interface for property driven object initialization.
 *
 * @param  <T>  type of object to invoke properties on
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface PropertySource<T>
{

  /**
   * Enum to define the domain for properties.
   */
  public enum PropertyDomain
  {

    /** ldap property domain. */
    LDAP("edu.vt.middleware.ldap."),

    /** auth property domain. */
    AUTH("edu.vt.middleware.ldap.auth."),

    /** pool property domain. */
    POOL("edu.vt.middleware.ldap.pool.");

    /** properties domain. */
    private String domain;


    /**
     * Creates a new property domain.
     *
     * @param  s  properties domain
     */
    PropertyDomain(final String s)
    {
      domain = s;
    }


    /**
     * Returns the properties domain value.
     *
     * @return  properties domain
     */
    public String value()
    {
      return domain;
    }
  }


  /**
   * Returns the object initialized with properties.
   *
   * @return  initialized object
   */
  T get();
}
