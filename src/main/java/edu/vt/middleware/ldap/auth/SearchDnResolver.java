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
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.AbstractLdap;
import edu.vt.middleware.ldap.SearchFilter;

/**
 * <code>SearchDnResolver</code> looks up a user's DN using an LDAP search.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchDnResolver extends AbstractLdap<AuthenticatorConfig>
  implements DnResolver, Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -7615995272176088807L;


  /** Default constructor. */
  public SearchDnResolver() {}


  /**
   * This will create a new <code>SearchDnResolver</code> with the supplied
   * <code>AuthenticatorConfig</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public SearchDnResolver(final AuthenticatorConfig authConfig)
  {
    this.setAuthenticatorConfig(authConfig);
  }


  /**
   * This will set the config parameters of this <code>Authenticator</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public void setAuthenticatorConfig(final AuthenticatorConfig authConfig)
  {
    super.setLdapConfig(authConfig);
  }


  /**
   * This returns the <code>AuthenticatorConfig</code> of the <code>
   * Authenticator</code>.
   *
   * @return  <code>AuthenticatorConfig</code>
   */
  public AuthenticatorConfig getAuthenticatorConfig()
  {
    return this.config;
  }


  /**
   * This will attempt to find the dn for the supplied user. {@link
   * AuthenticatorConfig#getUserFilter()} or {@link
   * AuthenticatorConfig#getUserField()} is used to look up the dn. If a filter
   * is used, the user is provided as the {0} variable filter argument. If a
   * field is used, the filter is built by ORing the fields together. If more
   * than one entry matches the search, the result is controlled by {@link
   * AuthenticatorConfig#setAllowMultipleDns(boolean)}.
   *
   * @param  user  <code>String</code> to find dn for
   *
   * @return  <code>String</code> - user's dn
   *
   * @throws  NamingException  if the LDAP search fails
   */
  public String resolve(final String user)
    throws NamingException
  {
    String dn = null;
    if (user != null && !"".equals(user)) {
      // create the search filter
      final SearchFilter filter = new SearchFilter();
      if (this.config.getUserFilter() != null) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Looking up DN using userFilter");
        }
        filter.setFilter(this.config.getUserFilter());
        filter.setFilterArgs(this.config.getUserFilterArgs());
      } else {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Looking up DN using userField");
        }
        if (
          this.config.getUserField() == null ||
            this.config.getUserField().length == 0) {
          if (this.logger.isErrorEnabled()) {
            this.logger.error("Invalid userField, cannot be null or empty.");
          }
        } else {
          final StringBuffer searchFilter = new StringBuffer();
          if (this.config.getUserField().length > 1) {
            searchFilter.append("(|");
            for (int i = 0; i < this.config.getUserField().length; i++) {
              searchFilter.append("(").append(this.config.getUserField()[i])
                .append("=").append(user).append(")");
            }
            searchFilter.append(")");
          } else {
            searchFilter.append("(").append(this.config.getUserField()[0])
              .append("=").append(user).append(")");
          }
          filter.setFilter(searchFilter.toString());
        }
      }

      if (filter.getFilter() != null) {
        // make user the first filter arg
        final List<Object> filterArgs = new ArrayList<Object>();
        filterArgs.add(user);
        filterArgs.addAll(filter.getFilterArgs());

        final Iterator<SearchResult> answer = this.search(
          this.config.getBaseDn(),
          filter.getFilter(),
          filterArgs.toArray(),
          this.config.getSearchControls(new String[0]),
          this.config.getSearchResultHandlers());
        // return first match, otherwise user doesn't exist
        if (answer != null && answer.hasNext()) {
          final SearchResult sr = answer.next();
          dn = sr.getName();
          if (answer.hasNext()) {
            if (this.logger.isDebugEnabled()) {
              this.logger.debug(
                "Multiple results found for user: " + user + " using filter: " +
                filter);
            }
            if (!this.config.getAllowMultipleDns()) {
              throw new NamingException("Found more than (1) DN for: " + user);
            }
          }
        } else {
          if (this.logger.isInfoEnabled()) {
            this.logger.info(
              "Search for user: " + user + " failed using filter: " +
              filter.getFilter());
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


  /** {@inheritDoc} */
  public void close()
  {
    super.close();
  }
}
