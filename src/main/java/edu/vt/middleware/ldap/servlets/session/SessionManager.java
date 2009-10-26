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
package edu.vt.middleware.ldap.servlets.session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>SessionManager</code> provides a parent class for initializing a <code>
 * HttpSession</code> after a successful authentication and destroying a <code>
 * HttpSession</code> after logout.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public abstract class SessionManager
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Identifier to set in the session after valid authentication. */
  protected String sessionId;

  /** Whether to invalidate session on logout. */
  protected boolean invalidateSession = true;


  /**
   * This sets a session id that can be used in {@link #login} or {@link
   * #logout}.
   *
   * @param  id  <code>String</code>
   */
  public void setSessionId(final String id)
  {
    this.sessionId = id;
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Set session attribute to " + this.sessionId);
    }
  }


  /**
   * This sets whether to invalidate a session on logout. Default value is true.
   *
   * @param  invalidate  <code>boolean</code>
   */
  public void setInvalidateSession(final boolean invalidate)
  {
    this.invalidateSession = invalidate;
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Set invalidateSession to " + this.invalidateSession);
    }
  }


  /**
   * This performs any actions necessary to login the suppled user.
   *
   * @param  session  <code>HttpSession</code>
   * @param  user  <code>String</code>
   *
   * @throws  ServletException  if an error occurs initializing the session
   */
  public abstract void login(HttpSession session, String user)
    throws ServletException;


  /**
   * This performs any actions necessary to logout the suppled session.
   *
   * @param  session  <code>HttpSession</code>
   *
   * @throws  ServletException  if an error occurs cleaning up the session
   */
  public abstract void logout(HttpSession session)
    throws ServletException;
}
