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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.DerefAliases;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.ReferralBehavior;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation for search dn resolvers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractSearchDnResolver implements DnResolver
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** DN to search. */
  private String baseDn = "";

  /** Filter for searching for the user. */
  private String userFilter;

  /** Filter arguments for searching for the user. */
  private Object[] userFilterArgs;

  /** Whether to throw an exception if multiple DNs are found. */
  private boolean allowMultipleDns;

  /** Whether to use a subtree search when resolving DNs. */
  private boolean subtreeSearch;

  /** How to handle aliases. */
  private DerefAliases derefAliases;

  /** How to handle referrals. */
  private ReferralBehavior referralBehavior;


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
   * Returns how to dereference aliases.
   *
   * @return  how to dereference aliases
   */
  public DerefAliases getDerefAliases()
  {
    return derefAliases;
  }


  /**
   * Sets how to dereference aliases.
   *
   * @param  da  how to dereference aliases
   */
  public void setDerefAliases(final DerefAliases da)
  {
    logger.trace("setting derefAliases: {}", da);
    derefAliases = da;
  }


  /**
   * Returns how to handle referrals.
   *
   * @return  how to handle referrals
   */
  public ReferralBehavior getReferralBehavior()
  {
    return referralBehavior;
  }


  /**
   * Sets how to handle referrals.
   *
   * @param  rb  how to handle referrals
   */
  public void setReferralBehavior(final ReferralBehavior rb)
  {
    logger.trace("setting referralBehavior: {}", rb);
    referralBehavior = rb;
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
    logger.debug("resolve user={}", user);
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
              "multiple results found for user={} using filter={}",
              user,
              filter);
            if (!allowMultipleDns) {
              throw new LdapException("Found more than (1) DN for: " + user);
            }
          }
        } else {
          logger.info(
            "search for user={} failed using filter={}", user, filter);
        }
      } else {
        logger.error("DN search filter not found, no search performed");
      }
    } else {
      logger.warn(
        "DN resolution cannot occur, user input was empty or null");
    }
    logger.debug("resolve dn={} for user={}", dn, user);
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
      logger.debug("searching for DN using userFilter");
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
    request.setDerefAliases(derefAliases);
    request.setReferralBehavior(referralBehavior);
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
    Connection conn = null;
    try {
      conn = getConnection();
      final SearchOperation op = new SearchOperation(conn);
      return op.execute(request).getResult();
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
  protected abstract Connection getConnection() throws LdapException;
}
