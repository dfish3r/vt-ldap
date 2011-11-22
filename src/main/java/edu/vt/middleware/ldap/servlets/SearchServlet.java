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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.io.Dsmlv1Writer;
import edu.vt.middleware.ldap.io.LdapResultWriter;
import edu.vt.middleware.ldap.io.LdifWriter;

/**
 * Queries an LDAP and returns the result as LDIF or DSML. The following init
 * params can be set for this servlet:
 * <ul>
 *   <li>edu.vt.middleware.ldap.servlets.propertiesFile</li>
 *   <li>edu.vt.middleware.ldap.servlets.poolPropertiesFile</li>
 *   <li>edu.vt.middleware.ldap.servlets.poolType</li>
 *   <li>edu.vt.middleware.ldap.servlets.outputFormat</li>
 * </ul>
 * Example: http://www.server.com/Search?query=uid=dfisher
 * If you need to pass complex queries, such as (&(cn=daniel*)(surname=fisher)),
 * then the query must be form encoded. If you only want to receive a subset of
 * attributes those can be specified. Example:
 * http://www.server.com/Search?query=uid=dfisher&attrs=givenname&attrs=surname
 *
 * <h3>LDIF</h3>
 * <p>The content returned by the servlet is of type text/plain.</p>
 * <hr/>
 * <h3>DSML</h3>
 * <p>The content returned by the servlet is of type text/xml, if you want to
 * receive the content as text/plain that can be specified as well. Example:
 * http://www.server.com/Search?query=uid=dfisher&content-type=text</p>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class SearchServlet extends AbstractServlet
{

  /** Format of search output, value is {@value}. */
  public static final String OUTPUT_FORMAT = PROPERTIES_DOMAIN + "outputFormat";

  /** Default format of search output, value is {@value}. */
  public static final String DEFAULT_OUTPUT_FORMAT = "DSML";

  /** serial version uid. */
  private static final long serialVersionUID = 1731614499970954068L;

  /** Types of available output. */
  private enum OutputType {

    /** LDIF output type. */
    LDIF,

    /** DSML output type. */
    DSML
  }

  /** Type of output to produce. */
  private OutputType output;


  /**
   * Initialize this servlet.
   *
   * @param  config  servlet config
   *
   * @throws  ServletException  if an error occurs
   */
  public void init(final ServletConfig config)
    throws ServletException
  {
    super.init(config);

    String outputType = getInitParameter(OUTPUT_FORMAT);
    if (outputType == null) {
      outputType = DEFAULT_OUTPUT_FORMAT;
    }
    logger.debug("{} = {}", OUTPUT_FORMAT, outputType);
    output = OutputType.valueOf(outputType);
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
    logger.info(
      "Performing search: {} for attributes: {}",
      request.getParameter("query"),
      request.getParameter("attrs"));
    try {
      final LdapResult result = search(
        request.getParameter("query"),
        request.getParameterValues("attrs"));
      LdapResultWriter writer = null;
      if (output == OutputType.LDIF) {
        response.setContentType("text/plain");
        writer = new LdifWriter(
          new BufferedWriter(
            new OutputStreamWriter(response.getOutputStream())));
      } else {
        final String content = request.getParameter("content-type");
        if (content != null && "text".equalsIgnoreCase(content)) {
          response.setContentType("text/plain");
        } else {
          response.setContentType("text/xml");
        }

        writer = new Dsmlv1Writer(
          new BufferedWriter(
            new OutputStreamWriter(response.getOutputStream())));
      }
      writer.write(result);
    } catch (Exception e) {
      logger.error("Error performing search", e);
      throw new ServletException(e);
    }
  }
}
