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

import edu.vt.middleware.ldap.AbstractConfig;

/**
 * Contains all the configuration data for SASL authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SaslConfig extends AbstractConfig
{

  /** sasl mechanism. */
  protected Mechanism mechanism;

  /** sasl authorization id. */
  protected String authorizationId;

  /** perform mutual authentication. */
  protected Boolean mutualAuthentication;

  /** sasl quality of protection. */
  protected QualityOfProtection qualityOfProtection;

  /** sasl security strength. */
  protected SecurityStrength securityStrength;


  /**
   * Returns the sasl mechanism.
   *
   * @return  mechanism
   */
  public Mechanism getMechanism()
  {
    return mechanism;
  }


  /**
   * Sets the sasl mechanism.
   *
   * @param  m  mechanism
   */
  public void setMechanism(final Mechanism m)
  {
    checkImmutable();
    logger.trace("setting mechanism: {}", m);
    mechanism = m;
  }


  /**
   * Returns the sasl authorization id.
   *
   * @return  authorization id
   */
  public String getAuthorizationId()
  {
    return authorizationId;
  }


  /**
   * Sets the sasl authorization id.
   *
   * @param  id  authorization id
   */
  public void setAuthorizationId(final String id)
  {
    checkImmutable();
    logger.trace("setting authorizationId: {}", id);
    authorizationId = id;
  }


  /**
   * Returns whether mutual authentication should occur.
   *
   * @return  whether mutual authentication should occur
   */
  public Boolean getMutualAuthentication()
  {
    return mutualAuthentication;
  }


  /**
   * Sets whether mutual authentication should occur.
   *
   * @param  b  whether mutual authentication should occur
   */
  public void setMutualAuthentication(final Boolean b)
  {
    checkImmutable();
    logger.trace("setting mutualAuthentication: {}", b);
    mutualAuthentication = b;
  }


  /**
   * Returns the sasl quality of protection.
   *
   * @return  quality of protection
   */
  public QualityOfProtection getQualityOfProtection()
  {
    return qualityOfProtection;
  }


  /**
   * Sets the sasl quality of protection.
   *
   * @param  qop  quality of protection
   */
  public void setQualityOfProtection(final QualityOfProtection qop)
  {
    checkImmutable();
    logger.trace("setting qualityOfProtection: {}", qop);
    qualityOfProtection = qop;
  }


  /**
   * Returns the sasl security strength.
   *
   * @return  security strength
   */
  public SecurityStrength getSecurityStrength()
  {
    return securityStrength;
  }


  /**
   * Sets the sasl security strength.
   *
   * @param  ss  security strength
   */
  public void setSecurityStrength(final SecurityStrength ss)
  {
    checkImmutable();
    logger.trace("setting securityStrength: {}", ss);
    securityStrength = ss;
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
        "qualityOfProtection=%s, securityStrength=%s]",
        getClass().getName(),
        hashCode(),
        mechanism,
        authorizationId,
        mutualAuthentication,
        qualityOfProtection,
        securityStrength);
  }
}
