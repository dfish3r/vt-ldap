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
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.handler.ExtendedLdapAttributeHandler;
import edu.vt.middleware.ldap.handler.ExtendedLdapResultHandler;
import edu.vt.middleware.ldap.handler.LdapAttributeHandler;
import edu.vt.middleware.ldap.handler.LdapResultHandler;

/**
 * Provides common implementation to ldap search operations.
 *
 * @param  <Q>  type of ldap request
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public abstract class AbstractSearchOperation<Q extends SearchRequest>
  extends AbstractLdapOperation<Q, LdapResult>
{


  /** {@inheritDoc} */
  protected void initializeRequest(final Q request, final LdapConfig lc)
  {
    if (request.getDn() == null) {
      request.setDn(lc.getBaseDn());
    }
    if (request.getSearchScope() == null) {
      request.setSearchScope(lc.getSearchScope());
    }
    if (request.getTimeLimit() == null) {
      request.setTimeLimit(lc.getTimeLimit());
    }
    if (request.getCountLimit() == null) {
      request.setCountLimit(lc.getCountLimit());
    }
    if (request.getBatchSize() == null) {
      request.setBatchSize(lc.getBatchSize());
    }
    if (request.getDerefAliases() == null) {
      request.setDerefAliases(lc.getDerefAliases());
    }
    if (request.getReferralBehavior() == null) {
      request.setReferralBehavior(lc.getReferralBehavior());
    }
    if (request.getTypesOnly() == null) {
      request.setTypeOnly(lc.getTypesOnly());
    }
    if (request.getBinaryAttributes() == null) {
      request.setBinaryAttributes(lc.getBinaryAttributes());
    }
    if (request.getSortBehavior() == null) {
      request.setSortBehavior(lc.getSortBehavior());
    }
    if (request.getLdapResultHandler() == null) {
      request.setLdapResultHandler(lc.getLdapResultHandlers());
    }
    if (request.getSearchIgnoreResultCodes() == null) {
      request.setSearchIgnoreResultCodes(lc.getSearchIgnoreResultCodes());
    }
    request.setLdapResultHandler(
      this.initializeLdapResultHandlers(request, this.ldapConnection));
  }


  /**
   * Initializes those ldap result handlers that require access to the ldap
   * connection.
   *
   * @param  request  to read result handlers from
   * @param  conn  to provide to result handlers
   * @return  initialized result handlers
   */
  protected LdapResultHandler[] initializeLdapResultHandlers(
    final Q request, final LdapConnection conn)
  {
    final LdapResultHandler[] handler = request.getLdapResultHandler();
    if (handler != null && handler.length > 0) {
      for (LdapResultHandler h : handler) {
        if (ExtendedLdapResultHandler.class.isInstance(h)) {
          ((ExtendedLdapResultHandler) h).setResultLdapConnection(conn);
        }

        final LdapAttributeHandler[] attrHandler = h.getAttributeHandler();
        if (attrHandler != null && attrHandler.length > 0) {
          for (LdapAttributeHandler ah : attrHandler) {
            if (ExtendedLdapAttributeHandler.class.isInstance(ah)) {
              ((ExtendedLdapAttributeHandler) ah).setResultLdapConnection(
                conn);
            }
          }
        }
      }
    }
    return handler;
  }
}
