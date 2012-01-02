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
package edu.vt.middleware.ldap.sasl;

/**
 * Contains all the configuration data for SASL Digest-MD5 authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DigestMd5Config extends SaslConfig
{

  /** sasl realm. */
  private String saslRealm;


  /** Default constructor. */
  public DigestMd5Config()
  {
    setMechanism(Mechanism.DIGEST_MD5);
  }


  /**
   * Returns the sasl realm.
   *
   * @return  realm
   */
  public String getRealm()
  {
    return saslRealm;
  }


  /**
   * Sets the sasl realm.
   *
   * @param  realm  to set
   */
  public void setRealm(final String realm)
  {
    checkImmutable();
    logger.trace("setting realm: {}", realm);
    saslRealm = realm;
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
        getMechanism(),
        getAuthorizationId(),
        getMutualAuthentication(),
        getQualityOfProtection(),
        getSecurityStrength(),
        saslRealm);
  }
}
