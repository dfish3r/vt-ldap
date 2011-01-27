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
package edu.vt.middleware.ldap.auth.handler;

import edu.vt.middleware.ldap.Credential;

/**
 * Contains the attributes used to perform authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticationCriteria
{

  /** dn. */
  protected String dn;

  /** credential. */
  protected Credential credential;


  /** Default constructor. */
  public AuthenticationCriteria() {}


  /**
   * Creates a new authentication criteria.
   *
   * @param  s  to set dn
   */
  public AuthenticationCriteria(final String s)
  {
    this.dn = s;
  }


  /**
   * Returns the dn.
   *
   * @return  dn  to authenticate
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
   * Returns the credential.
   *
   * @return  credential  to authenticate dn
   */
  public Credential getCredential()
  {
    return this.credential;
  }


  /**
   * Sets the credential.
   *
   * @param  c  to set credential
   */
  public void setCredential(final Credential c)
  {
    this.credential = c;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "%s@%d: dn=%s, credential=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.dn,
        this.credential);
  }
}
