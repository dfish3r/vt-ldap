/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
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
import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.auth.AuthorizationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CompareAuthorizationHandler performs a compare operation with a custom
 * filter. The DN of the authenticated user is automatically provided as the {0}
 * variable in the search filter arguments.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CompareAuthorizationHandler implements AuthorizationHandler
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());


  /** Search filter. */
  private SearchFilter searchFilter;


  /** Default constructor. */
  public CompareAuthorizationHandler() {}


  /**
   * Creates a new <code>CompareAuthorizationHandler</code> with the supplied
   * search filter.
   *
   * @param  sf  <code>SearchFilter</code>
   */
  public CompareAuthorizationHandler(final SearchFilter sf)
  {
    this.searchFilter = sf;
  }


  /**
   * Returns the search filter.
   *
   * @return  <code>SearchFilter</code>
   */
  public SearchFilter getSearchFilter()
  {
    return this.searchFilter;
  }


  /**
   * Sets the search filter.
   *
   * @param  sf  <code>SearchFilter</code>
   */
  public void setSearchFilter(final SearchFilter sf)
  {
    this.searchFilter = sf;
  }


  /** {@inheritDoc} */
  public void process(final AuthenticationCriteria ac, final LdapContext ctx)
    throws NamingException
  {
    // make DN the first filter arg
    final List<Object> filterArgs = new ArrayList<Object>();
    filterArgs.add(ac.getDn());
    filterArgs.addAll(this.searchFilter.getFilterArgs());

    // perform ldap compare operation
    NamingEnumeration<SearchResult> results = null;
    try {
      results = ctx.search(
        ac.getDn(),
        this.searchFilter.getFilter(),
        filterArgs.toArray(),
        LdapConfig.getCompareSearchControls());
      if (!results.hasMore()) {
        throw new AuthorizationException("Compare failed");
      }
    } finally {
      if (results != null) {
        results.close();
      }
    }
  }


  /**
   * Provides a descriptive string representation of this authorization handler.
   *
   * @return  String of the form $Classname::$filter.
   */
  @Override
  public String toString()
  {
    return
      String.format("%s::%s", this.getClass().getName(), this.searchFilter);
  }
}
