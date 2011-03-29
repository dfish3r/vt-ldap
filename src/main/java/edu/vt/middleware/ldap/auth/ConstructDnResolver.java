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

  /** Directory user field. */
  protected String userField = "uid";

  /** Base DN to append to all DNs. */
  protected String baseDn = "";


  /** Default constructor. */
  public ConstructDnResolver() {}


  /**
   * Creates a new construct DN resolver.
   *
   * @param  dn  base DN to append
   */
  public ConstructDnResolver(final String dn)
  {
    this.setBaseDn(dn);
  }


  /**
   * Creates a new construct DN resolver.
   *
   * @param  dn  base DN to append
   * @param  s  user field
   */
  public ConstructDnResolver(final String dn, final String s)
  {
    this.setBaseDn(dn);
    this.setUserField(s);
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
   * Returns the base DN.
   *
   * @return  base DN
   */
  public String getBaseDn()
  {
    return this.baseDn;
  }


  /**
   * Sets the base DN.
   *
   * @param  dn base DN
   */
  public void setBaseDn(final String dn)
  {
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting baseDn: " + dn);
    }
    this.baseDn = dn;
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
      dn = String.format("%s=%s,%s", this.userField, user, this.baseDn);
    } else {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("User input was empty or null");
      }
    }
    return dn;
  }
}
