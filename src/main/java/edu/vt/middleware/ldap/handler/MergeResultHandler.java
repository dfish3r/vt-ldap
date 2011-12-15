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
import edu.vt.middleware.ldap.LdapUtil;

/**
 * Merges the attributes found in each search result into the first search
 * result.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class MergeResultHandler extends CopyLdapResultHandler
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 839;


  /** {@inheritDoc} */
  @Override
  public void process(final SearchCriteria criteria, final LdapResult result)
    throws LdapException
  {
    if (result != null) {
      super.process(criteria, result);
      mergeResults(result);
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
        for (LdapAttribute la : le.getAttributes()) {
          final LdapAttribute oldAttr =
            mergedEntry.getAttribute(la.getName());
          if (oldAttr == null) {
            mergedEntry.addAttribute(la);
          } else {
            if (oldAttr.isBinary()) {
              oldAttr.addBinaryValues(la.getBinaryValues());
            } else {
              oldAttr.addStringValues(la.getStringValues());
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


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    return LdapUtil.computeHashCode(
      HASH_CODE_SEED, (Object) getAttributeHandlers());
  }
}
