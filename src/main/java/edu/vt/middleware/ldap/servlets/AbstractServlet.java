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
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.pool.BlockingLdapPool;
import edu.vt.middleware.ldap.pool.DefaultLdapFactory;
import edu.vt.middleware.ldap.pool.LdapPool;
import edu.vt.middleware.ldap.pool.LdapPoolConfig;
import edu.vt.middleware.ldap.pool.LdapPoolException;
import edu.vt.middleware.ldap.pool.SharedLdapPool;
import edu.vt.middleware.ldap.pool.SoftLimitLdapPool;
import edu.vt.middleware.ldap.props.LdapConnectionConfigProperties;
import edu.vt.middleware.ldap.props.LdapPoolConfigProperties;
import edu.vt.middleware.ldap.props.SearchRequestProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    SOFTLIMIT,

    /** shared. */
    SHARED
  }

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(SearchServlet.class);

  /** Pool for searching. */
  private LdapPool<LdapConnection> ldapPool;

  /** Search request reader for reading search properties. */
  private SearchRequestProperties searchRequestProps;


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
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(PROPERTIES_FILE + " = " + propertiesFile);
    }

    final LdapConnectionConfigProperties lccProps =
      new LdapConnectionConfigProperties(
        SearchServlet.class.getResourceAsStream(propertiesFile));
    final LdapConnectionConfig lcc = lccProps.get();

    this.searchRequestProps = new SearchRequestProperties(
      SearchServlet.class.getResourceAsStream(propertiesFile));

    final String poolPropertiesFile = getInitParameter(POOL_PROPERTIES_FILE);
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(POOL_PROPERTIES_FILE + " = " + poolPropertiesFile);
    }

    final LdapPoolConfigProperties lpcProps = new LdapPoolConfigProperties(
      SearchServlet.class.getResourceAsStream(poolPropertiesFile));
    final LdapPoolConfig lpc = lpcProps.get();

    final String poolType = getInitParameter(POOL_TYPE);
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(POOL_TYPE + " = " + poolType);
    }
    if (PoolType.BLOCKING == PoolType.valueOf(poolType)) {
      this.ldapPool = new BlockingLdapPool(lpc, new DefaultLdapFactory(lcc));
    } else if (PoolType.SOFTLIMIT == PoolType.valueOf(poolType)) {
      this.ldapPool = new SoftLimitLdapPool(lpc, new DefaultLdapFactory(lcc));
    } else if (PoolType.SHARED == PoolType.valueOf(poolType)) {
      this.ldapPool = new SharedLdapPool(lpc, new DefaultLdapFactory(lcc));
    } else {
      throw new ServletException("Unknown pool type: " + poolType);
    }
    this.ldapPool.initialize();
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
        LdapConnection conn = null;
        try {
          conn = this.ldapPool.checkOut();
          final SearchOperation search = new SearchOperation(conn);
          final SearchRequest sr = SearchRequest.newSearchRequest(
            this.searchRequestProps.get());
          sr.setSearchFilter(new SearchFilter(query));
          sr.setReturnAttributes(attrs);
          result = search.execute(sr).getResult();
        } finally {
          this.ldapPool.checkIn(conn);
        }
      } catch (LdapPoolException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("Error using LDAP pool", e);
        }
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
      this.ldapPool.close();
    } finally {
      super.destroy();
    }
  }
}
