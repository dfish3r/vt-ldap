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
package edu.vt.middleware.ldap.handler;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.LdapConstants;

/**
 * <code>DefaultConnectionHandler</code> creates a new <code>LdapContext</code>
 * using environment properties obtained from {@link
 * LdapConfig#getEnvironment()}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DefaultConnectionHandler extends AbstractConnectionHandler
{


  /** Default constructor. */
  public DefaultConnectionHandler() {}


  /**
   * Creates a new <code>DefaultConnectionHandler</code> with the supplied ldap
   * config.
   *
   * @param  lc  ldap config
   */
  public DefaultConnectionHandler(final LdapConfig lc)
  {
    this.setLdapConfig(lc);
  }


  /** {@inheritDoc} */
  public void connect(final String dn, final Object credential)
    throws NamingException
  {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Bind with the following parameters:");
      this.logger.debug("  dn = " + dn);
      if (this.config.getLogCredentials()) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("  credential = " + credential);
        }
      } else {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("  credential = <suppressed>");
        }
      }
      if (this.logger.isTraceEnabled()) {
        this.logger.trace("  config = " + this.config.getEnvironment());
      }
    }

    final Hashtable<String, Object> environment = new Hashtable<String, Object>(
      this.config.getEnvironment());
    // note that when using simple authentication (the default),
    // if the credential is null the provider will automatically revert the
    // authentication to none
    environment.put(LdapConstants.AUTHENTICATION, this.config.getAuthtype());
    if (dn != null) {
      environment.put(LdapConstants.PRINCIPAL, dn);
      if (credential != null) {
        environment.put(LdapConstants.CREDENTIALS, credential);
      }
    }

    try {
      this.context = new InitialLdapContext(environment, null);
    } catch (NamingException e) {
      if (this.context != null) {
        try {
          this.context.close();
        } finally {
          this.context = null;
        }
      }
      throw e;
    }
  }


  /** {@inheritDoc} */
  public DefaultConnectionHandler newInstance()
  {
    return new DefaultConnectionHandler(this.config);
  }
}
