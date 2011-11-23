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

import java.util.Arrays;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up the LDAP entry associated with a user using an LDAP search.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchEntryResolver implements EntryResolver
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** User attributes to return. */
  private String[] returnAttributes;


  /** Default constructor. */
  public SearchEntryResolver() {}


  /**
   * Creates a new search entry resolver.
   *
   * @param  attrs  to return
   */
  public SearchEntryResolver(final String[] attrs)
  {
    setReturnAttributes(attrs);
  }


  /**
   * Returns the return attributes.
   *
   * @return  attributes to return
   */
  public String[] getReturnAttributes()
  {
    return returnAttributes;
  }


  /**
   * Sets the return attributes.
   *
   * @param  attrs  to return
   */
  public void setReturnAttributes(final String[] attrs)
  {
    returnAttributes = attrs;
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry resolve(
    final Connection conn, final AuthenticationCriteria ac)
    throws LdapException
  {
    logger.debug(
      "Resolving entry attributes: {}",
      returnAttributes == null ?
        "all attributes" : Arrays.toString(returnAttributes));
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      SearchRequest.newObjectScopeSearchRequest(
        ac.getDn(), returnAttributes)).getResult();
    return result.getEntry();
  }
}
