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
package edu.vt.middleware.ldap.servlets;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;

/**
 * Queries an LDAP and returns the value of a single attribute. Example:
 * http://www.server.com/Attribute?query=uid=dfisher&attr=givenName If you need
 * to pass complex queries, such as (&(cn=daniel*)(surname=fisher)), then the
 * query must be form encoded. The content returned by the servlet is of type
 * text/plain, if you want to receive the content as application/octet-stream
 * that can be specified by passing the content-type=octet param. The following
 * init params can be set for this servlet:
 * <ul>
 *   <li>edu.vt.middleware.ldap.servlets.propertiesFile</li>
 *   <li>edu.vt.middleware.ldap.servlets.poolPropertiesFile</li>
 *   <li>edu.vt.middleware.ldap.servlets.poolType</li>
 * </ul>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class AttributeServlet extends AbstractServlet
{

  /** serial version uid. */
  private static final long serialVersionUID = -5918353780927139315L;


  /**
   * Initialize this servlet.
   *
   * @param  config  servlet configuration
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
   * @param  request  http servlet reqeust
   * @param  response  http servlet response
   *
   * @throws  ServletException  if an error occurs
   * @throws  IOException  if an error occurs
   */
  public void service(
    final HttpServletRequest request,
    final HttpServletResponse response)
    throws ServletException, IOException
  {
    final String attribute = request.getParameter("attr");
    byte[] value = null;
    final String content = request.getParameter("content-type");

    if (content != null && content.equalsIgnoreCase("octet")) {
      response.setContentType("application/octet-stream");
      response.setHeader(
        "Content-Disposition",
        "attachment; filename=\"" + attribute + ".bin\"");
    } else {
      response.setContentType("text/plain");
    }

    try {
      final LdapResult result = search(
        request.getParameter("query"),
        request.getParameterValues("attrs"));
      for (LdapEntry e : result.getEntries()) {
        final LdapAttribute a = e.getLdapAttributes().getAttribute(attribute);
        if (a != null && a.getValues().size() > 0) {
          final Object rawValue = a.getValues().iterator().next();
          if (rawValue instanceof String) {
            final String stringValue = (String) rawValue;
            value = stringValue.getBytes();
          } else {
            value = (byte[]) rawValue;
          }
        }
      }

      if (value != null) {
        final OutputStream out = response.getOutputStream();
        out.write(value);
        out.flush();
        out.close();
      }

    } catch (Exception e) {
      logger.error("Error performing search", e);
      throw new ServletException(e.getMessage());
    }
  }
}
