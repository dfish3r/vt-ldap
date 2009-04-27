/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
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
 * <code>DefaultSessionManager</code> provides a base class for session
 * management. After a successful authentication, this class sets the session id
 * that is set for the login servlet to the user name. After logout the session
 * id attribute is removed from the session. This class is used by default if no
 * custom session manager has been set.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class DefaultSessionManager extends SessionManager
{

  /** Log for this class */
  private static final Log LOG = LogFactory.getLog(DefaultSessionManager.class);


  /**
   * This performs any actions necessary to login the suppled user.
   *
   * @param  session  <code>HttpSession</code>
   * @param  user  <code>String</code>
   *
   * @throws  ServletException  if an error occurs initializing the session
   */
  public void login(final HttpSession session, final String user)
    throws ServletException
  {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Begin login method");
    }
    if (this.sessionId != null) {
      session.setAttribute(this.sessionId, user);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Set session attribute " + this.sessionId + " to " + user);
      }
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Could not set session attribute, value is null");
      }
    }
  }


  /**
   * This performs any actions necessary to logout the suppled session.
   *
   * @param  session  <code>HttpSession</code>
   *
   * @throws  ServletException  if an error occurs cleaning up the session
   */
  public void logout(final HttpSession session)
    throws ServletException
  {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Begin logout method");
    }
    if (this.sessionId != null) {
      final String user = (String) session.getAttribute(this.sessionId);
      session.removeAttribute(this.sessionId);
      if (LOG.isDebugEnabled()) {
        LOG.debug(
          "Removed session attribute " + this.sessionId + " for " + user);
      }
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Could not remove session attribute, value is null");
      }
    }
    if (this.invalidateSession) {
      session.invalidate();
      if (LOG.isDebugEnabled()) {
        LOG.debug("Session invalidated");
      }
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Session was not invalidated");
      }
    }
  }
}
