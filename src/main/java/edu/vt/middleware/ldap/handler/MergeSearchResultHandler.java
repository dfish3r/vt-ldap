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
package edu.vt.middleware.ldap.handler;

import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;

/**
 * <code>MergeSearchResultHandler</code> merges the attributes found in each
 * search result into the first search result.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class MergeSearchResultHandler extends CopySearchResultHandler
{

  /** Whether to allow duplicate attribute values. */
  private boolean allowDuplicates;


  /**
   * Returns whether to allow duplicate attribute values.
   *
   * @return  <code>boolean</code>
   */
  public boolean getAllowDuplicates()
  {
    return this.allowDuplicates;
  }


  /**
   * Sets whether to allow duplicate attribute values.
   *
   * @param  b  <code>boolean</code>
   */
  public void setAllowDuplicates(final boolean b)
  {
    this.allowDuplicates = b;
  }


  /** {@inheritDoc} */
  public List<SearchResult> process(
    final SearchCriteria sc,
    final NamingEnumeration<? extends SearchResult> en,
    final Class<?>[] ignore)
    throws NamingException
  {
    return this.mergeResults(super.process(sc, en, ignore));
  }


  /** {@inheritDoc} */
  public List<SearchResult> process(
    final SearchCriteria sc,
    final List<? extends SearchResult> l)
    throws NamingException
  {
    return this.mergeResults(super.process(sc, l));
  }


  /**
   * Merges the search results in the supplied list into a single search result.
   * This method always returns a list of size zero or one.
   *
   * @param  results  <code>List</code> of search results to merge
   *
   * @return  <code>List</code> of merged search results
   *
   * @throws  NamingException  if an error occurs reading attribute values
   */
  protected List<SearchResult> mergeResults(final List<SearchResult> results)
    throws NamingException
  {
    final List<SearchResult> mergedResults = new ArrayList<SearchResult>();
    SearchResult mergedResult = null;
    for (SearchResult sr : results) {
      if (mergedResult == null) {
        mergedResult = sr;
      } else {
        final NamingEnumeration<? extends Attribute> en = sr.getAttributes()
            .getAll();
        while (en.hasMore()) {
          final Attribute newAttr = en.next();
          final Attribute oldAttr = mergedResult.getAttributes().get(
            newAttr.getID());
          if (oldAttr == null) {
            mergedResult.getAttributes().put(newAttr);
          } else {
            final NamingEnumeration<?> newValues = newAttr.getAll();
            while (newValues.hasMore()) {
              final Object newValue = newValues.next();
              if (this.allowDuplicates) {
                oldAttr.add(newValue);
              } else {
                boolean add = true;
                final NamingEnumeration<?> existingValues = oldAttr.getAll();
                while (existingValues.hasMore()) {
                  final Object existingValue = existingValues.next();
                  if (existingValue.equals(newValue)) {
                    add = false;
                    break;
                  }
                }
                if (add) {
                  oldAttr.add(newValue);
                }
              }
            }
          }
        }
      }
    }
    if (mergedResult != null) {
      mergedResults.add(mergedResult);
    }
    return mergedResults;
  }
}
