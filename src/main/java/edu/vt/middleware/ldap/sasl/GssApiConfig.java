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
package edu.vt.middleware.ldap.sasl;

/**
 * Contains all the configuration data for SASL GSSAPI authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class GssApiConfig extends SaslConfig
{
  /** sasl realm. */
  protected String realm;


  /**
   * Default constructor.
   */
  public GssApiConfig()
  {
    mechanism = Mechanism.GSSAPI;
  }


  /**
   * Returns the sasl realm.
   *
   * @return  realm
   */
  public String getRealm()
  {
    return realm;
  }


  /**
   * Sets the sasl realm.
   *
   * @param  s  realm
   */
  public void setRealm(final String s)
  {
    checkImmutable();
    logger.trace("setting realm: {}", s);
    realm = s;
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
        "[%s@%d::mechanism=%s, authorizationId=%s, mutualAuthentication=%s, " +
        "qualityOfProtection=%s, securityStrength=%s, realm=%s]",
        getClass().getName(),
        hashCode(),
        mechanism,
        authorizationId,
        mutualAuthentication,
        qualityOfProtection,
        securityStrength,
        realm);
  }
}
