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
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>CompareLdapValidator</code> validates an ldap connection is healthy by
 * performing a compare operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CompareLdapValidator implements LdapValidator<LdapConnection>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** DN for validating connections. Default value is {@value}. */
  private String validateDn = "";

  /** Filter for validating connections. Default value is {@value}. */
  private SearchFilter validateFilter = new SearchFilter("(objectClass=*)");


  /** Default constructor. */
  public CompareLdapValidator() {}


  /**
   * Creates a new <code>CompareLdapValidator</code> with the supplied compare
   * dn and filter.
   *
   * @param  dn  to use for compares
   * @param  filter  to use for compares
   */
  public CompareLdapValidator(final String dn, final SearchFilter filter)
  {
    validateDn = dn;
    validateFilter = filter;
  }


  /**
   * Returns the validate DN.
   *
   * @return  validate DN
   */
  public String getValidateDn()
  {
    return validateDn;
  }


  /**
   * Returns the validate filter.
   *
   * @return  validate filter
   */
  public SearchFilter getValidateFilter()
  {
    return validateFilter;
  }


  /**
   * Sets the validate DN.
   *
   * @param  s  DN
   */
  public void setValidateDn(final String s)
  {
    validateDn = s;
  }


  /**
   * Sets the validate filter.
   *
   * @param  filter  to compare with
   */
  public void setValidateFilter(final SearchFilter filter)
  {
    validateFilter = filter;
  }


  /** {@inheritDoc} */
  @Override
  public boolean validate(final LdapConnection lc)
  {
    boolean success = false;
    if (lc != null) {
      try {
        final SearchOperation search = new SearchOperation(lc);
        final LdapResult lr = search.execute(
          new SearchRequest(
            validateDn, validateFilter)).getResult();
        success = lr.size() == 1;
      } catch (LdapException e) {
        logger.debug(
          "validation failed for compare {}", validateFilter, e);
      }
    }
    return success;
  }
}
