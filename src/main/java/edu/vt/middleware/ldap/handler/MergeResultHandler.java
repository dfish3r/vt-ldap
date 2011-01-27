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

/**
 * Merges the attributes found in each search result into the first search
 * result.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class MergeResultHandler extends CopyLdapResultHandler
{

  /** Whether to allow duplicate attribute values. */
  private boolean allowDuplicates;


  /**
   * Returns whether to allow duplicate attribute values.
   *
   * @return  whether to allow duplicate attribute values
   */
  public boolean getAllowDuplicates()
  {
    return this.allowDuplicates;
  }


  /**
   * Sets whether to allow duplicate attribute values.
   *
   * @param  b  whether to allow duplicate attribute values
   */
  public void setAllowDuplicates(final boolean b)
  {
    this.allowDuplicates = b;
  }


  /** {@inheritDoc} */
  public void process(final SearchCriteria sc, final LdapResult lr)
    throws LdapException
  {
    if (lr != null) {
      super.process(sc, lr);
      this.mergeResults(lr);
    }
  }


  /**
   * Merges the search results in the supplied results into a single search
   * result. This method always returns a ldap result of size zero or one.
   *
   * @param  results  ldap result to merge
   *
   * @throws  LdapException  if an error occurs reading attribute values
   */
  protected void mergeResults(final LdapResult results)
    throws LdapException
  {
    LdapEntry mergedEntry = null;
    for (LdapEntry le : results.getEntries()) {
      if (mergedEntry == null) {
        mergedEntry = le;
      } else {
        for (LdapAttribute la : le.getLdapAttributes().getAttributes()) {
          final LdapAttribute oldAttr =
            mergedEntry.getLdapAttributes().getAttribute(la.getName());
          if (oldAttr == null) {
            mergedEntry.getLdapAttributes().addAttribute(la);
          } else {
            for (Object o : la.getValues()) {
              if (this.allowDuplicates) {
                oldAttr.getValues().add(o);
              } else {
                boolean add = true;
                for (Object existing : oldAttr.getValues()) {
                  if (existing.equals(o)) {
                    add = false;
                    break;
                  }
                }
                if (add) {
                  oldAttr.getValues().add(o);
                }
              }
            }
          }
        }
      }
    }
    results.clear();
    if (mergedEntry != null) {
      results.addEntry(mergedEntry);
    }
  }
}
