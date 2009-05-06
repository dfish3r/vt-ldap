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

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>LogoutServet</code> removes the session id attribute set by the <code>
 * LoginServlet</code>. The following init params can be set for this servlet:
 * edu.vt.middleware.ldap.servlets.sessionId - to remove from the session
 *
 * <p>The following http params can be sent to this servlet: url - to redirect
 * client to after logout</p>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class LogoutServlet extends CommonServlet
{

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(LogoutServlet.class);


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
    String url = request.getParameter(ServletConstants.URL_PARAM);
    if (url == null) {
      url = "";
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Received url param = " + url);
    }

    this.sessionManager.logout(request.getSession(true));
    response.sendRedirect(url);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Redirected user to " + url);
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
