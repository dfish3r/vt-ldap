/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.CompareOperation;
import edu.vt.middleware.ldap.CompareRequest;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates a connection is healthy by performing a compare operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CompareValidator implements Validator<Connection>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Compare request to perform validation with. */
  private CompareRequest compareRequest;


  /** Creates a new compare validator. */
  public CompareValidator()
  {
    compareRequest = new CompareRequest();
    compareRequest.setDn("");
    compareRequest.setAttribute(new LdapAttribute("objectClass", "top"));
  }


  /**
   * Creates a new compare validator.
   *
   * @param  cr  to use for compares
   */
  public CompareValidator(final CompareRequest cr)
  {
    compareRequest = cr;
  }


  /**
   * Returns the compare request.
   *
   * @return  compare request
   */
  public CompareRequest getCompareRequest()
  {
    return compareRequest;
  }


  /**
   * Sets the compare request.
   *
   * @param  cr  compare request
   */
  public void setCompareRequest(final CompareRequest cr)
  {
    compareRequest = cr;
  }


  /** {@inheritDoc} */
  @Override
  public boolean validate(final Connection c)
  {
    boolean success = false;
    if (c != null) {
      try {
        final CompareOperation compare = new CompareOperation(c);
        final Boolean b = compare.execute(compareRequest).getResult();
        success = b.booleanValue();
      } catch (Exception e) {
        logger.debug(
          "validation failed for compare request {}",
          compareRequest,
          e);
      }
    }
    return success;
  }
}
