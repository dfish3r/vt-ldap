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
package edu.vt.middleware.ldap.servlets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import edu.vt.middleware.ldap.servlets.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>CommonServlet</code> contains common code that each servlet uses to
 * initialize itself.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CommonServlet extends HttpServlet
{

  /** serial version uid. */
  private static final long serialVersionUID = -9145103488979474729L;

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Used to manage a session after login and logout. */
  protected SessionManager sessionManager;


  /**
   * Initialize this servlet.
   *
   * @param  config  <code>ServletConfig</code>
   *
   * @throws  ServletException  if an error occurs
   */
  public void init(final ServletConfig config)
    throws ServletException
  {
    super.init(config);

    String sessionManagerClass = getInitParameter(
      ServletConstants.SESSION_MANAGER);
    if (sessionManagerClass == null) {
      sessionManagerClass = ServletConstants.DEFAULT_SESSION_MANAGER;
    }
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(ServletConstants.SESSION_MANAGER + " = " +
                        sessionManagerClass);
    }
    try {
      this.sessionManager = (SessionManager) Class.forName(sessionManagerClass)
          .newInstance();
    } catch (ClassNotFoundException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Could not find class " + sessionManagerClass, e);
      }
      throw new ServletException(e);
    } catch (InstantiationException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error(
          "Could not instantiate class " + sessionManagerClass, e);
      }
      throw new ServletException(e);
    } catch (IllegalAccessException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Could not access class " + sessionManagerClass, e);
      }
      throw new ServletException(e);
    }

    String sessionId = getInitParameter(ServletConstants.SESSION_ID);
    if (sessionId == null) {
      sessionId = ServletConstants.DEFAULT_SESSION_ID;
    }
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(ServletConstants.SESSION_ID + " = " + sessionId);
    }
    this.sessionManager.setSessionId(sessionId);


    String invalidateSession = getInitParameter(
      ServletConstants.INVALIDATE_SESSION);
    if (invalidateSession == null) {
      invalidateSession = ServletConstants.DEFAULT_INVALIDATE_SESSION;
    }
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(
        ServletConstants.INVALIDATE_SESSION + " = " + invalidateSession);
    }
    this.sessionManager.setInvalidateSession(
      Boolean.valueOf(invalidateSession).booleanValue());
  }
}
