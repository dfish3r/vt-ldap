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
import edu.vt.middleware.ldap.LdapException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Returns a DN with the user field concatenated with the base DN.
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

  /** Directory user field. */
  protected String userField = "uid";


  /** Default constructor. */
  public ConstructDnResolver() {}


  /**
   * Creates a new construct DN resolver.
   *
   * @param  ac  authenticator config
   */
  public ConstructDnResolver(final AuthenticatorConfig ac)
  {
    this.setAuthenticatorConfig(ac);
  }


  /**
   * Creates a new construct DN resolver.
   *
   * @param  ac  authenticator config
   * @param  s  user field
   */
  public ConstructDnResolver(final AuthenticatorConfig ac, final String s)
  {
    this.setAuthenticatorConfig(ac);
    this.setUserField(s);
  }


  /**
   * Returns the authenticator config.
   *
   * @return  authenticator config
   */
  public AuthenticatorConfig getAuthenticatorConfig()
  {
    return this.config;
  }


  /**
   * Sets the authenticator config.
   *
   * @param  ac  of the authenticator
   */
  public void setAuthenticatorConfig(final AuthenticatorConfig ac)
  {
    this.config = ac;
  }


  /**
   * Returns the user field used to construct the entry DN.
   *
   * @return  user field
   */
  public String getUserField()
  {
    return this.userField;
  }


  /**
   * Sets the user field used to construct the entry DN.
   *
   * @param  s  user field to construct DN with
   */
  public void setUserField(final String s)
  {
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting userField: " + s);
    }
    this.userField = s;
  }


  /**
   * Creates an ldap entry where the DN is the user field and the base DN.
   *
   * @param  user  to construct dn for
   *
   * @return  ldap entry
   *
   * @throws  LdapException  if the LDAP search fails
   */
  public String resolve(final String user)
    throws LdapException
  {
    String dn = null;
    if (user != null && !"".equals(user)) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Constructing DN from userFilter and base");
      }
      dn = String.format(
        "%s=%s,%s", this.userField, user, this.config.getBaseDn());
    } else {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("User input was empty or null");
      }
    }
    return dn;
  }
}
