/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.auth;

import edu.vt.middleware.ldap.Credential;

/**
 * Contains the properties used to perform authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticationCriteria
{

  /** dn. */
  private String authenticationDn;

  /** credential. */
  private Credential credential;


  /** Default constructor. */
  public AuthenticationCriteria() {}


  /**
   * Creates a new authentication criteria.
   *
   * @param  dn  to set
   */
  public AuthenticationCriteria(final String dn)
  {
    authenticationDn = dn;
  }


  /**
   * Returns the dn.
   *
   * @return  dn to authenticate
   */
  public String getDn()
  {
    return authenticationDn;
  }


  /**
   * Sets the dn.
   *
   * @param  dn  to set dn
   */
  public void setDn(final String dn)
  {
    authenticationDn = dn;
  }


  /**
   * Returns the credential.
   *
   * @return  credential to authenticate dn
   */
  public Credential getCredential()
  {
    return credential;
  }


  /**
   * Sets the credential.
   *
   * @param  c  to set credential
   */
  public void setCredential(final Credential c)
  {
    credential = c;
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
        "[%s@%d::dn=%s]",
        getClass().getName(),
        hashCode(),
        authenticationDn);
  }
}
