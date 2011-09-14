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
package edu.vt.middleware.ldap.auth.handler;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapException;
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
   * Returns a connection that the bind operation should be performed on.
   *
   * @return  connection
   *
   * @throws  LdapException  if an error occurs provisioning the connection
   */
  protected abstract Connection getConnection() throws LdapException;


  /**
   * Performs a bind on the supplied connection using the supplied criteria.
   *
   * @param  c  to bind on
   * @param  criteria  criteria to bind with
   *
   * @throws  LdapException  if the bind fails
   */
  protected abstract void authenticateInternal(
    final Connection c, final AuthenticationCriteria criteria)
    throws LdapException;
}
