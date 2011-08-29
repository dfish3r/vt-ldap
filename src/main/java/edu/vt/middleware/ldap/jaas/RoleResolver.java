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
package edu.vt.middleware.ldap.jaas;

import java.util.Set;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up a user's roles using an LDAP search.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RoleResolver
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Connection config. */
  protected ConnectionConfig config;


  /** Default constructor. */
  public RoleResolver() {}


  /**
   * Creates a new role resolver.
   *
   * @param  cc  connection config
   */
  public RoleResolver(final ConnectionConfig cc)
  {
    setConnectionConfig(cc);
  }


  /**
   * Returns the connection config.
   *
   * @return  connection config
   */
  public ConnectionConfig getConnectionConfig()
  {
    return config;
  }


  /**
   * Sets the connection config.
   *
   * @param  cc  connection config
   */
  public void setConnectionConfig(final ConnectionConfig cc)
  {
    config = cc;
  }


  /**
   * Prepares this role resolver for use.
   */
  public void initialize() {}


  /**
   * Executes a search request and converts any attributes to ldap roles.
   *
   * @param  request  to execute
   *
   * @return  ldap roles
   *
   * @throws  LdapException  if the ldap operation fails
   */
  public Set<LdapRole> search(final SearchRequest request)
    throws LdapException
  {
    Connection conn = null;
    try {
      conn = getConnection();
      final SearchOperation search = new SearchOperation(conn);
      return LdapRole.toRoles(search.execute(request).getResult());
    } finally {
      if (conn != null) {
        conn.close();
      }
    }
  }


  /**
   * Retrieve a connection that is ready for use.
   *
   * @return  connection
   *
   * @throws LdapException  if an error occurs opening the connection
   */
  protected Connection getConnection()
    throws LdapException
  {
    final Connection conn = new Connection(config);
    conn.open();
    return conn;
  }


  /**
   * Frees any resources associated with this role resolver.
   */
  public void close() {}


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::config=%s]",
        getClass().getName(),
        hashCode(),
        config);
  }
}
