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
import java.util.Iterator;
import java.util.List;
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Authenticator config. */
  protected AuthenticatorConfig config;


  /** Default constructor. */
  public SearchDnResolver() {}


  /**
   * Creates a new search dn resolver.
   *
   * @param  ac  authenticator config
   */
  public SearchDnResolver(final AuthenticatorConfig ac)
  {
    this.setAuthenticatorConfig(ac);
  }


  /**
   * Returns the authenticator config.
   *
   * @return  authenticator config
   */
  public AuthenticatorConfig getAuthenticatorConfig()
  {
    return this.config;
  }


  /**
   * Sets the authenticator config.
   *
   * @param  ac  authenticator config
   */
  public void setAuthenticatorConfig(final AuthenticatorConfig ac)
  {
    this.config = ac;
  }


  /**
   * Attempts to find the DN for the supplied user. {@link
   * AuthenticatorConfig#getUserFilter()} is used to look up the DN. The user is
   * provided as the {0} variable filter argument. If more than one entry
   * matches the search, the result is controlled by
   * {@link AuthenticatorConfig#setAllowMultipleDns(boolean)}.
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
      final SearchFilter filter = this.createSearchFilter(user);

      if (filter.getFilter() != null) {
        final LdapResult result = this.performLdapSearch(filter);
        final Iterator<LdapEntry> answer = result.getEntries().iterator();

        // return first match, otherwise user doesn't exist
        if (answer != null && answer.hasNext()) {
          dn = answer.next().getDn();
          if (answer.hasNext()) {
            if (this.logger.isDebugEnabled()) {
              this.logger.debug(
                "Multiple results found for user: " + user + " using filter: " +
                filter);
            }
            if (!this.config.getAllowMultipleDns()) {
              throw new LdapException("Found more than (1) DN for: " + user);
            }
          }
        } else {
          if (this.logger.isInfoEnabled()) {
            this.logger.info(
              "Search for user: " + user + " failed using filter: " + filter);
          }
        }
      } else {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("DN search filter not found, no search performed");
        }
      }
    } else {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("User input was empty or null");
      }
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
    if (this.config.getUserFilter() != null) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Looking up DN using userFilter");
      }
      filter.setFilter(this.config.getUserFilter());
      filter.setFilterArgs(this.config.getUserFilterArgs());

      // make user the first filter arg
      final List<Object> filterArgs = new ArrayList<Object>();
      filterArgs.add(user);
      filterArgs.addAll(filter.getFilterArgs());
      filter.setFilterArgs(filterArgs);

    } else {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Invalid userFilter, cannot be null or empty.");
      }
    }
    return filter;
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
    final SearchRequest request = new SearchRequest();
    request.setSearchFilter(filter);
    request.setReturnAttributes(new String[0]);

    final LdapConnection conn = new LdapConnection(this.config);
    try {
      conn.open();
      final SearchOperation op = new SearchOperation(conn);
      return op.execute(request).getResult();
    } finally {
      conn.close();
    }
  }
}
