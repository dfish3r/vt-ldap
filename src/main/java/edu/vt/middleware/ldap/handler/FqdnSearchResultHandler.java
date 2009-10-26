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

import java.net.URI;
import javax.naming.directory.SearchResult;

/**
 * <code>FqdnSearchResultHandler</code> ensures that the DN of a search result
 * is fully qualified. Any non-relative names will have the URL removed if
 * {@link #getRemoveUrls()} is true.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class FqdnSearchResultHandler extends CopySearchResultHandler
{

  /** Whether to remove the URL from any DNs which are not relative. */
  private boolean removeUrls = true;


  /**
   * Returns whether the URL will be removed from any DNs which are not
   * relative. The default value is true.
   *
   * @return  <code>boolean</code>
   */
  public boolean getRemoveUrls()
  {
    return this.removeUrls;
  }


  /**
   * Sets whether the URL will be removed from any DNs which are not relative
   * The default value is true.
   *
   * @param  b  <code>boolean</code>
   */
  public void setRemoveUrls(final boolean b)
  {
    this.removeUrls = b;
  }


  /** {@inheritDoc} */
  protected String processDn(final SearchCriteria sc, final SearchResult sr)
  {
    String newDn = null;
    final String resultName = sr.getName();
    if (resultName != null) {
      StringBuffer fqName = null;
      if (sr.isRelative()) {
        if (sc.getDn() != null) {
          if (!resultName.equals("")) {
            fqName = new StringBuffer(resultName).append(",").append(
              sc.getDn());
          } else {
            fqName = new StringBuffer(sc.getDn());
          }
        } else {
          fqName = new StringBuffer(resultName);
        }
      } else {
        if (this.removeUrls) {
          fqName = new StringBuffer(
            URI.create(resultName).getPath().substring(1));
        } else {
          fqName = new StringBuffer(resultName);
        }
      }
      newDn = fqName.toString();
    }
    return newDn;
  }
}
