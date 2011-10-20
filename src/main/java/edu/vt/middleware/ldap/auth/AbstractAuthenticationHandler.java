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

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.control.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for an LDAP authentication implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractAuthenticationHandler
  implements AuthenticationHandler
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** controls used by this handler. */
  private Control[] authenticationControls;


  /**
   * Returns the controls for this authentication handler.
   *
   * @return  controls
   */
  public Control[] getAuthenticationControls()
  {
    return authenticationControls;
  }


  /**
   * Sets the controls for this authentication handler.
   *
   * @param  c  controls to set
   */
  public void setAuthenticationControls(final Control... c)
  {
    authenticationControls = c;
  }


  /** {@inheritDoc} */
  @Override
  public Connection authenticate(final AuthenticationCriteria ac)
    throws LdapException
  {
    final Connection conn = getConnection();
    boolean closeConn = false;
    try {
      authenticateInternal(conn, ac);
    } catch (LdapException e) {
      closeConn = true;
      throw e;
    } finally {
      if (closeConn) {
        conn.close();
      }
    }
    return conn;
  }


  /**
   * Returns a connection that the authentication operation should be performed
   * on.
   *
   * @return  connection
   *
   * @throws  LdapException  if an error occurs provisioning the connection
   */
  protected abstract Connection getConnection() throws LdapException;


  /**
   * Authenticate on the supplied connection using the supplied criteria.
   *
   * @param  c  to authenticate on
   * @param  criteria  criteria to authenticate with
   *
   * @throws  LdapException  if the authentication fails
   */
  protected abstract void authenticateInternal(
    final Connection c, final AuthenticationCriteria criteria)
    throws LdapException;
}
