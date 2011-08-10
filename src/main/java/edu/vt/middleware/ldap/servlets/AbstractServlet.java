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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.pool.BlockingConnectionPool;
import edu.vt.middleware.ldap.pool.ConnectionPool;
import edu.vt.middleware.ldap.pool.PoolConfig;
import edu.vt.middleware.ldap.pool.PoolException;
import edu.vt.middleware.ldap.pool.SoftLimitConnectionPool;
import edu.vt.middleware.ldap.props.ConnectionConfigPropertySource;
import edu.vt.middleware.ldap.props.PoolConfigPropertySource;
import edu.vt.middleware.ldap.props.SearchRequestPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for ldap search servlets.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractServlet extends HttpServlet
{

  /** Domain to look for properties in, value is {@value}. */
  public static final String PROPERTIES_DOMAIN =
    "edu.vt.middleware.ldap.servlets.";

  /** LDAP initialization properties file, value is {@value}. */
  public static final String PROPERTIES_FILE = PROPERTIES_DOMAIN +
    "propertiesFile";

  /** LDAP pool initialization properties file, value is {@value}. */
  public static final String POOL_PROPERTIES_FILE = PROPERTIES_DOMAIN +
    "poolPropertiesFile";

  /** Type of pool used, value is {@value}. */
  public static final String POOL_TYPE = PROPERTIES_DOMAIN + "poolType";

  /** serial version uid. */
  private static final long serialVersionUID = 1984990003439357859L;

  /** Types of available pools. */
  protected enum PoolType {

    /** blocking. */
    BLOCKING,

    /** soft limit. */
    SOFTLIMIT
  }

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Pool for searching. */
  private ConnectionPool ldapPool;

  /** Search request reader for reading search properties. */
  private SearchRequestPropertySource searchRequestSource;


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

    final String propertiesFile = getInitParameter(PROPERTIES_FILE);
    logger.debug("{} = {}", PROPERTIES_FILE, propertiesFile);

    final ConnectionConfigPropertySource ccSource =
      new ConnectionConfigPropertySource(
        SearchServlet.class.getResourceAsStream(propertiesFile));
    final ConnectionConfig cc = ccSource.get();

    searchRequestSource = new SearchRequestPropertySource(
      SearchServlet.class.getResourceAsStream(propertiesFile));

    final String poolPropertiesFile = getInitParameter(POOL_PROPERTIES_FILE);
    logger.debug("{} = {}", POOL_PROPERTIES_FILE, poolPropertiesFile);

    final PoolConfigPropertySource pcSource =
      new PoolConfigPropertySource(
        SearchServlet.class.getResourceAsStream(poolPropertiesFile));
    final PoolConfig pc = pcSource.get();

    final String poolType = getInitParameter(POOL_TYPE);
    logger.debug("{} = {}", POOL_TYPE, poolType);
    if (PoolType.BLOCKING == PoolType.valueOf(poolType)) {
      ldapPool = new BlockingConnectionPool(pc, cc);
    } else if (PoolType.SOFTLIMIT == PoolType.valueOf(poolType)) {
      ldapPool = new SoftLimitConnectionPool(pc, cc);
    } else {
      throw new ServletException("Unknown pool type: " + poolType);
    }
    ldapPool.initialize();
  }


  /**
   * Performs an ldap search uses this servlets ldap connection pool.
   *
   * @param  query  to execute
   * @param  attrs  to return
   * @return  ldap result
   * @throws  LdapException  if an error occurs
   */
  protected LdapResult search(
    final String query,
    final String[] attrs)
    throws LdapException
  {
    LdapResult result = null;
    if (query != null) {
      try {
        Connection conn = null;
        try {
          conn = ldapPool.getConnection();
          final SearchOperation search = new SearchOperation(conn);
          final SearchRequest sr = SearchRequest.newSearchRequest(
            searchRequestSource.get());
          sr.setSearchFilter(new SearchFilter(query));
          sr.setReturnAttributes(attrs);
          result = search.execute(sr).getResult();
        } finally {
          if (conn != null) {
            conn.close();
          }
        }
      } catch (PoolException e) {
        logger.error("Error using LDAP pool", e);
      }
    }
    return result;
  }


  /**
   * Called by the servlet container to indicate to a servlet that the servlet
   * is being taken out of service.
   */
  public void destroy()
  {
    try {
      ldapPool.close();
    } finally {
      super.destroy();
    }
  }
}
