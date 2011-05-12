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
  private LdapAttributeHandler[] ldapAttributeHandler;


  /** {@inheritDoc} */
  @Override
  public LdapAttributeHandler[] getAttributeHandler()
  {
    return ldapAttributeHandler;
  }


  /** {@inheritDoc} */
  @Override
  public void setAttributeHandler(final LdapAttributeHandler[] ah)
  {
    ldapAttributeHandler = ah;
  }


  /** {@inheritDoc} */
  @Override
  public void process(final SearchCriteria sc, final LdapResult lr)
    throws LdapException
  {
    if (lr != null) {
      for (LdapEntry le : lr.getEntries()) {
        le.setDn(processDn(sc, le));
        processAttributes(sc, le);
      }
    }
  }


  /**
   * Process the dn of an ldap entry.
   *
   * @param  sc  search criteria used to find the ldap entry
   * @param  le  ldap entry to extract the dn from
   *
   * @return  processed dn
   */
  protected String processDn(final SearchCriteria sc, final LdapEntry le)
  {
    return le.getDn();
  }


  /**
   * Process the attributes of an ldap entry.
   *
   * @param  sc  search criteria used to find the ldap entry
   * @param  le  ldap entry to extract the attributes from
   *
   * @throws  LdapException  if the LDAP returns an error
   */
  protected void processAttributes(final SearchCriteria sc, final LdapEntry le)
    throws LdapException
  {
    if (ldapAttributeHandler != null &&
        ldapAttributeHandler.length > 0) {
      for (LdapAttributeHandler ah : ldapAttributeHandler) {
        for (LdapAttribute la : le.getLdapAttributes().getAttributes()) {
          ah.process(sc, la);
        }
      }
    }
  }
}
