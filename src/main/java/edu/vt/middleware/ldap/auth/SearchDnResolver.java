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
package edu.vt.middleware.ldap.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up a user's DN using an LDAP search.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchDnResolver implements DnResolver, Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -7615995272176088807L;

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection config. */
  protected ConnectionConfig config;

  /** DN to search. */
  protected String baseDn = "";

  /** Filter for searching for the user. */
  private String userFilter;

  /** Filter arguments for searching for the user. */
  private Object[] userFilterArgs;

  /** Whether to throw an exception if multiple DNs are found. */
  private boolean allowMultipleDns;

  /** Whether to use a subtree search when resolving DNs. */
  private boolean subtreeSearch;


  /** Default constructor. */
  public SearchDnResolver() {}


  /**
   * Creates a new search dn resolver.
   *
   * @param  lcc  ldap connection config
   */
  public SearchDnResolver(final ConnectionConfig lcc)
  {
    setConnectionConfig(lcc);
  }


  /**
   * Returns the ldap connection config.
   *
   * @return  ldap connection config
   */
  public ConnectionConfig getConnectionConfig()
  {
    return config;
  }


  /**
   * Sets the ldap connection config.
   *
   * @param  lcc  ldap connection config
   */
  public void setConnectionConfig(final ConnectionConfig lcc)
  {
    config = lcc;
  }


  /**
   * Returns the base DN.
   *
   * @return  base DN
   */
  public String getBaseDn()
  {
    return baseDn;
  }


  /**
   * Sets the base DN.
   *
   * @param  dn base DN
   */
  public void setBaseDn(final String dn)
  {
    logger.trace("setting baseDn: {}", dn);
    baseDn = dn;
  }


  /**
   * Returns the filter used to search for the user.
   *
   * @return  filter  for searching
   */
  public String getUserFilter()
  {
    return userFilter;
  }


  /**
   * Sets the filter used to search for the user.
   *
   * @param  filter  for searching
   */
  public void setUserFilter(final String filter)
  {
    logger.trace("setting userFilter: {}", filter);
    userFilter = filter;
  }


  /**
   * Returns the filter arguments used to search for the user.
   *
   * @return  filter arguments
   */
  public Object[] getUserFilterArgs()
  {
    return userFilterArgs;
  }


  /**
   * Sets the filter arguments used to search for the user.
   *
   * @param  filterArgs  filter arguments
   */
  public void setUserFilterArgs(final Object[] filterArgs)
  {
    logger.trace(
      "setting userFilterArgs: {}", Arrays.toString(filterArgs));
    userFilterArgs = filterArgs;
  }


  /**
   * Returns whether DN resolution should fail if multiple DNs are found.
   *
   * @return  whether an exception will be thrown if multiple DNs are found
   */
  public boolean getAllowMultipleDns()
  {
    return allowMultipleDns;
  }


  /**
   * Sets whether DN resolution should fail if multiple DNs are found
   * If false an exception will be thrown if {@link #resolve(String)}
   * finds more than one DN matching it's filter. Otherwise the first DN found
   * is returned.
   *
   * @param  b  whether multiple DNs are allowed
   */
  public void setAllowMultipleDns(final boolean b)
  {
    logger.trace("setting allowMultipleDns: {}", b);
    allowMultipleDns = b;
  }


  /**
   * Returns whether subtree searching will be used.
   *
   * @return  whether the DN will be searched for over the entire base
   */
  public boolean getSubtreeSearch()
  {
    return subtreeSearch;
  }


  /**
   * Sets whether subtree searching will be used. If true, the DN used for
   * authenticating will be searched for over the entire {@link #getBaseDn()}.
   * Otherwise the DN will be search for in the {@link #getBaseDn()} context.
   *
   * @param  b  whether the DN will be searched for over the entire base
   */
  public void setSubtreeSearch(final boolean b)
  {
    logger.trace("setting subtreeSearch: {}", b);
    subtreeSearch = b;
  }


  /**
   * Attempts to find the DN for the supplied user. {@link
   * #getUserFilter()} is used to look up the DN. The user is
   * provided as the {0} variable filter argument. If more than one entry
   * matches the search, the result is controlled by
   * {@link #setAllowMultipleDns(boolean)}.
   *
   * @param  user  to find DN for
   *
   * @return  user DN
   *
   * @throws  LdapException  if the entry resolution fails
   */
  public String resolve(final String user)
    throws LdapException
  {
    String dn = null;
    if (user != null && !"".equals(user)) {
      // create the search filter
      final SearchFilter filter = createSearchFilter(user);

      if (filter.getFilter() != null) {
        final LdapResult result = performLdapSearch(filter);
        final Iterator<LdapEntry> answer = result.getEntries().iterator();

        // return first match, otherwise user doesn't exist
        if (answer != null && answer.hasNext()) {
          dn = answer.next().getDn();
          if (answer.hasNext()) {
            logger.debug(
              "Multiple results found for user: {} using filter: {}",
              user,
              filter);
            if (!allowMultipleDns) {
              throw new LdapException("Found more than (1) DN for: " + user);
            }
          }
        } else {
          logger.info(
            "Search for user: {} failed using filter: {}", user, filter);
        }
      } else {
        logger.error("DN search filter not found, no search performed");
      }
    } else {
      logger.warn(
        "DN resolution cannot occur, user input was empty or null");
    }
    return dn;
  }


  /**
   * Returns a search filter using the user filter and user filter args of the
   * authentication config. The user parameter is injected as the first filter
   * argument.
   *
   * @param  user identifier
   * @return  search filter
   */
  protected SearchFilter createSearchFilter(final String user)
  {
    final SearchFilter filter = new SearchFilter();
    if (userFilter != null) {
      logger.debug("Looking up DN using userFilter");
      filter.setFilter(userFilter);
      filter.setFilterArgs(userFilterArgs);

      // make user the first filter arg
      final List<Object> filterArgs = new ArrayList<Object>();
      filterArgs.add(user);
      filterArgs.addAll(filter.getFilterArgs());
      filter.setFilterArgs(filterArgs);

    } else {
      logger.error("Invalid userFilter, cannot be null or empty.");
    }
    return filter;
  }


  /**
   * Returns a search request for searching for a single entry in an LDAP,
   * returning no attributes.
   *
   * @param  filter  to execute
   * @return  search request
   */
  protected SearchRequest createSearchRequest(final SearchFilter filter)
  {
    final SearchRequest request = new SearchRequest();
    request.setBaseDn(baseDn);
    request.setSearchFilter(filter);
    request.setReturnAttributes(new String[0]);
    if (subtreeSearch) {
      request.setSearchScope(SearchScope.SUBTREE);
    } else {
      request.setSearchScope(SearchScope.ONELEVEL);
    }
    return request;
  }


  /**
   * Executes the ldap search operation with the supplied filter.
   *
   * @param  filter  to execute
   * @return  ldap search result
   * @throws  LdapException  if an error occurs
   */
  protected LdapResult performLdapSearch(final SearchFilter filter)
    throws LdapException
  {
    final SearchRequest request = createSearchRequest(filter);
    final Connection conn = new Connection(config);
    try {
      conn.open();
      final SearchOperation op = new SearchOperation(conn);
      return op.execute(request).getResult();
    } finally {
      conn.close();
    }
  }


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
        "[%s@%d::baseDn=%s, userFilter=%s, userFilterArgs=%s, " +
        "allowMultipleDns=%s, subtreeSearch=%s, config=%s]",
        getClass().getName(),
        hashCode(),
        baseDn,
        userFilter,
        userFilterArgs != null ? Arrays.asList(userFilterArgs) : null,
        allowMultipleDns,
        subtreeSearch,
        config);
  }
}
