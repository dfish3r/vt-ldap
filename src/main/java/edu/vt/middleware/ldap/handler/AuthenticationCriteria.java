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
package edu.vt.middleware.ldap.handler;

/**
 * <code>AuthenticationCriteria</code> contains the attributes used to perform
 * authentications.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class AuthenticationCriteria
{

  /** dn. */
  private String dn;

  /** credential. */
  private Object credential;


  /** Default constructor. */
  public AuthenticationCriteria() {}


  /**
   * Creates a new authentication criteria with the supplied dn.
   *
   * @param  s  to set dn
   */
  public AuthenticationCriteria(final String s)
  {
    this.dn = s;
  }


  /**
   * Gets the dn.
   *
   * @return  dn
   */
  public String getDn()
  {
    return this.dn;
  }


  /**
   * Sets the dn.
   *
   * @param  s  to set dn
   */
  public void setDn(final String s)
  {
    this.dn = s;
  }


  /**
   * Gets the credential.
   *
   * @return  credential
   */
  public Object getCredential()
  {
    return this.credential;
  }


  /**
   * Sets the credential.
   *
   * @param  o  to set credential
   */
  public void setCredential(final Object o)
  {
    this.credential = o;
  }


  /**
   * This returns a string representation of this search criteria.
   *
   * @return  <code>String</code>
   */
  @Override
  public String toString()
  {
    return String.format("dn=%s,credential=%s", this.dn, this.credential);
  }
}
