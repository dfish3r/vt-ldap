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
package edu.vt.middleware.ldap.auth.handler;

import java.util.ArrayList;
import java.util.List;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.auth.AuthorizationException;

/**
 * Performs a compare operation with a custom filter. The DN of the
 * authenticated user is automatically provided as the {0} variable in the
 * search filter arguments.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CompareAuthorizationHandler implements AuthorizationHandler
{

  /** Search filter. */
  private SearchFilter searchFilter;


  /** Default constructor. */
  public CompareAuthorizationHandler() {}


  /**
   * Creates a new compare authorization handler.
   *
   * @param  sf  filter to execute on the user entry
   */
  public CompareAuthorizationHandler(final SearchFilter sf)
  {
    searchFilter = sf;
  }


  /**
   * Returns the search filter.
   *
   * @return  filter to execute on the user entry
   */
  public SearchFilter getSearchFilter()
  {
    return searchFilter;
  }


  /**
   * Sets the search filter.
   *
   * @param  sf  filter to execute on the user entry
   */
  public void setSearchFilter(final SearchFilter sf)
  {
    searchFilter = sf;
  }


  /** {@inheritDoc} */
  @Override
  public void process(
    final AuthenticationCriteria ac, final Connection conn)
    throws LdapException
  {
    final SearchFilter filter = new SearchFilter(searchFilter.getFilter());

    // make DN the first filter arg
    final List<Object> filterArgs = new ArrayList<Object>();
    filterArgs.add(ac.getDn());
    filterArgs.addAll(searchFilter.getFilterArgs());
    filter.setFilterArgs(filterArgs);

    // perform ldap object level operation
    final SearchOperation search = new SearchOperation(conn);
    final SearchRequest sr = SearchRequest.newObjectScopeSearchRequest(
      ac.getDn(), new String[0], filter);
    final LdapResult lr = search.execute(sr).getResult();
    if (lr.size() != 1) {
      throw new AuthorizationException("Compare failed");
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
        "[%s@%d::searchFilter=%s]",
        getClass().getName(),
        hashCode(),
        searchFilter);
  }
}
