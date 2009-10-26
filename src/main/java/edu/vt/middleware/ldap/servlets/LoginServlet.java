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
package edu.vt.middleware.ldap.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import edu.vt.middleware.ldap.Authenticator;
import edu.vt.middleware.ldap.props.LdapProperties;

/**
 * <code>LoginServet</code> attempts to authenticate a user against an LDAP. The
 * following init params can be set for this servlet:
 * edu.vt.middleware.ldap.servlets.propertiesFile - to load authenticator
 * properties from edu.vt.middleware.ldap.servlets.sessionId - to set the user
 * identifier in the session edu.vt.middleware.ldap.servlets.loginUrl - to set
 * the URL of your login page edu.vt.middleware.ldap.servlets.errorMsg - to
 * display if authentication fails
 * edu.vt.middleware.ldap.servlets.sessionManager - optional class to perform
 * session management after login and logout (must extend
 * edu.vt.middleware.ldap.servlets.session.SessionManager)
 *
 * <p>The following http params can be sent to this servlet: user - user
 * identifier to authenticate credential - user credential to authenticate with
 * url - to redirect client to after successful authentication</p>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class LoginServlet extends CommonServlet
{

  /** serial version uid. */
  private static final long serialVersionUID = -3482852409544351134L;

  /** URL of the page that does collects user credentials. */
  private String loginUrl;

  /** Message to display if authentication fails. */
  private String errorMsg;

  /** Used to authenticate against an LDAP. */
  private Authenticator auth;


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
    this.loginUrl = getInitParameter(ServletConstants.LOGIN_URL);
    if (this.loginUrl == null) {
      this.loginUrl = ServletConstants.DEFAULT_LOGIN_URL;
    }
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(ServletConstants.LOGIN_URL + " = " + this.loginUrl);
    }
    this.errorMsg = getInitParameter(ServletConstants.ERROR_MSG);
    if (this.errorMsg == null) {
      this.errorMsg = ServletConstants.DEFAULT_ERROR_MSG;
    }
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(ServletConstants.ERROR_MSG + " = " + this.errorMsg);
    }

    String propertiesFile = getInitParameter(ServletConstants.PROPERTIES_FILE);
    if (propertiesFile == null) {
      propertiesFile = LdapProperties.PROPERTIES_FILE;
    }
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(
        ServletConstants.PROPERTIES_FILE + " = " + propertiesFile);
    }
    this.auth = new Authenticator();
    this.auth.loadFromProperties(
      LoginServlet.class.getResourceAsStream(propertiesFile));
  }


  /**
   * Handle all requests sent to this servlet.
   *
   * @param  request  <code>HttpServletRequest</code>
   * @param  response  <code>HttpServletResponse</code>
   *
   * @throws  ServletException  if this request cannot be serviced
   * @throws  IOException  if a response cannot be sent
   */
  public void service(
    final HttpServletRequest request,
    final HttpServletResponse response)
    throws ServletException, IOException
  {
    boolean validCredentials = false;
    String user = request.getParameter(ServletConstants.USER_PARAM);
    if (user != null) {
      user = user.trim().toLowerCase();
    }
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Received user param = " + user);
    }

    final String credential = request.getParameter(
      ServletConstants.CREDENTIAL_PARAM);
    String url = request.getParameter(ServletConstants.URL_PARAM);
    if (url == null) {
      url = "";
    }
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Received url param = " + url);
    }

    final StringBuffer error = new StringBuffer(this.errorMsg);

    try {
      if (this.auth.authenticate(user, credential)) {
        validCredentials = true;
      }
    } catch (Exception e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Error authenticating user " + user, e);
      }
      if (
        e.getCause() != null &&
          e.getCause().getMessage() != null &&
          !e.getCause().getMessage().equals("null")) {
        error.append(": ").append(e.getCause().getMessage());
      } else if (e.getMessage() != null && !e.getMessage().equals("null")) {
        error.append(": ").append(e.getMessage());
      }
    }

    if (validCredentials) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Authentication succeeded for user " + user);
      }
      try {
        // invalidate existing session
        HttpSession session = request.getSession(false);
        if (session != null) {
          session.invalidate();
        }
        session = request.getSession(true);
        this.sessionManager.login(session, user);
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Initialized session for user " + user);
        }
        response.sendRedirect(url);
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Redirected user to " + url);
        }
        return;
      } catch (Exception e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("Error authorizing user " + user, e);
        }
        if (
          e.getCause() != null &&
            e.getCause().getMessage() != null &&
            !e.getCause().getMessage().equals("null")) {
          error.append(": ").append(e.getCause().getMessage());
        } else if (e.getMessage() != null && !e.getMessage().equals("null")) {
          error.append(": ").append(e.getMessage());
        }
      }
    }

    final StringBuffer errorUrl = new StringBuffer(this.loginUrl);
    if (error != null) {
      errorUrl.append("?error=").append(
        URLEncoder.encode(error.toString(), "UTF-8"));
    }
    if (user != null) {
      errorUrl.append("&user=").append(URLEncoder.encode(user, "UTF-8"));
    }
    if (url != null) {
      errorUrl.append("&url=").append(URLEncoder.encode(url, "UTF-8"));
    }
    response.sendRedirect(errorUrl.toString());
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Redirected user to " + errorUrl.toString());
    }
  }


  /**
   * Called by the servlet container to indicate to a servlet that the servlet
   * is being taken out of service.
   */
  public void destroy()
  {
    super.destroy();
  }
}
