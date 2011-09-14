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
package edu.vt.middleware.ldap.handler;

import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for result handlers which simply returns values unaltered.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CopyLdapResultHandler implements LdapResultHandler
{

  /** Log for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Attribute handler. */
  private LdapAttributeHandler[] attributeHandler;


  /** {@inheritDoc} */
  @Override
  public LdapAttributeHandler[] getAttributeHandler()
  {
    return attributeHandler;
  }


  /** {@inheritDoc} */
  @Override
  public void setAttributeHandler(final LdapAttributeHandler[] handlers)
  {
    attributeHandler = handlers;
  }


  /** {@inheritDoc} */
  @Override
  public void process(final SearchCriteria criteria, final LdapResult result)
    throws LdapException
  {
    if (result != null) {
      for (LdapEntry le : result.getEntries()) {
        le.setDn(processDn(criteria, le));
        processAttributes(criteria, le);
      }
    }
  }


  /**
   * Process the dn of an ldap entry.
   *
   * @param  criteria  search criteria used to find the ldap entry
   * @param  entry  ldap entry to extract the dn from
   *
   * @return  processed dn
   */
  protected String processDn(
    final SearchCriteria criteria, final LdapEntry entry)
  {
    return entry.getDn();
  }


  /**
   * Process the attributes of an ldap entry.
   *
   * @param  criteria  search criteria used to find the ldap entry
   * @param  entry  ldap entry to extract the attributes from
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  protected void processAttributes(
    final SearchCriteria criteria, final LdapEntry entry)
    throws LdapException
  {
    if (attributeHandler != null &&
        attributeHandler.length > 0) {
      for (LdapAttributeHandler ah : attributeHandler) {
        for (LdapAttribute la : entry.getAttributes()) {
          ah.process(criteria, la);
        }
      }
    }
  }
}
