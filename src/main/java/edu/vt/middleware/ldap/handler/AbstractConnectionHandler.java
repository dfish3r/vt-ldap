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

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import edu.vt.middleware.ldap.LdapConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AbstractConnectionHandler</code> provides a basic implementation for
 * other connection handlers to inherit.

 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractConnectionHandler implements ConnectionHandler
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Ldap configuration. */
  protected LdapConfig config;

  /** Ldap context. */
  protected LdapContext context;


  /** {@inheritDoc} */
  public LdapContext getLdapContext()
  {
    return this.context;
  }


  /** {@inheritDoc} */
  public void setLdapConfig(final LdapConfig lc)
  {
    this.config = lc;
  }


  /** {@inheritDoc} */
  public abstract void connect(final String dn, final Object credential)
    throws NamingException;


  /** {@inheritDoc} */
  public boolean isConnected()
  {
    return this.context != null;
  }


  /** {@inheritDoc} */
  public void close()
    throws NamingException
  {
    try {
      if (this.context != null) {
        this.context.close();
      }
    } finally {
      this.context = null;
    }
  }


  /** {@inheritDoc} */
  public abstract ConnectionHandler newInstance();
}
