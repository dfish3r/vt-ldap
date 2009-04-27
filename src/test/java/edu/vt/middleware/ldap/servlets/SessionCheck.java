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
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <code>SessionCheck</code> prints sessions variables for testing purposes.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SessionCheck extends HttpServlet
{


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
    final PrintWriter out = response.getWriter();
    final HttpSession session = request.getSession();
    final Enumeration e = session.getAttributeNames();
    while (e.hasMoreElements()) {
      final String k = (String) e.nextElement();
      out.println(k + ":" + session.getAttribute(k));
    }
  }
}
