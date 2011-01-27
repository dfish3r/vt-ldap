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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for result handlers which simply returns values unaltered.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CopyLdapResultHandler implements LdapResultHandler
{
  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Attribute handler. */
  private LdapAttributeHandler[] ldapAttributeHandler;


  /** {@inheritDoc} */
  public LdapAttributeHandler[] getAttributeHandler()
  {
    return this.ldapAttributeHandler;
  }


  /** {@inheritDoc} */
  public void setAttributeHandler(final LdapAttributeHandler[] ah)
  {
    this.ldapAttributeHandler = ah;
  }


  /** {@inheritDoc} */
  public void process(final SearchCriteria sc, final LdapResult lr)
    throws LdapException
  {
    if (lr != null) {
      for (LdapEntry le : lr.getEntries()) {
        le.setDn(this.processDn(sc, le));
        this.processAttributes(sc, le);
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
    if (this.ldapAttributeHandler != null &&
        this.ldapAttributeHandler.length > 0) {
      for (LdapAttributeHandler ah : this.ldapAttributeHandler) {
        for (LdapAttribute la : le.getLdapAttributes().getAttributes()) {
          ah.process(sc, la);
        }
      }
    }
  }
}
