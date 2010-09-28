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
package edu.vt.middleware.ldap.auth;

import java.io.Serializable;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>ConstructDnResolver</code> creates an LDAP DN using known information
 * about the LDAP. Specifically it concatenates the first user field with the
 * base DN.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ConstructDnResolver implements DnResolver, Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -6508789359608064771L;

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Authentication configuration. */
  protected AuthenticatorConfig config;


  /** Default constructor. */
  public ConstructDnResolver() {}


  /**
   * This will create a new <code>ConstructDnResolver</code> with the supplied
   * <code>AuthenticatorConfig</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public ConstructDnResolver(final AuthenticatorConfig authConfig)
  {
    this.setAuthenticatorConfig(authConfig);
  }


  /**
   * This will set the config parameters of this <code>Authenticator</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public void setAuthenticatorConfig(final AuthenticatorConfig authConfig)
  {
    this.config = authConfig;
  }


  /**
   * This returns the <code>AuthenticatorConfig</code> of the <code>
   * Authenticator</code>.
   *
   * @return  <code>AuthenticatorConfig</code>
   */
  public AuthenticatorConfig getAuthenticatorConfig()
  {
    return this.config;
  }


  /**
   * Creates a LDAP DN by combining the userField and the base dn.
   *
   * @param  user  <code>String</code> to find dn for
   *
   * @return  <code>String</code> - user's dn
   *
   * @throws  NamingException  if the LDAP search fails
   */
  public String resolve(final String user)
    throws NamingException
  {
    String dn = null;
    if (user != null && !"".equals(user)) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Constructing DN from first userfield and base");
      }
      dn = String.format(
        "%s=%s,%s",
        this.config.getUserField()[0],
        user,
        this.config.getBaseDn());
    } else {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("User input was empty or null");
      }
    }
    return dn;
  }


  /** {@inheritDoc} */
  public void close() {}
}
