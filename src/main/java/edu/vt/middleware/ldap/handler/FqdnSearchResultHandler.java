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

import java.net.URI;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
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
        if (this.logger.isTraceEnabled()) {
          this.logger.trace("processing relative dn: " + resultName);
        }
        if (sc.getDn() != null) {
          if (!"".equals(resultName)) {
            fqName = new StringBuffer(
              readCompositeName(resultName)).append(",").append(sc.getDn());
          } else {
            fqName = new StringBuffer(sc.getDn());
          }
        } else {
          fqName = new StringBuffer(readCompositeName(resultName));
        }
      } else {
        if (this.logger.isTraceEnabled()) {
          this.logger.trace("processing non-relative dn: " + resultName);
        }
        if (this.removeUrls) {
          fqName = new StringBuffer(
            readCompositeName(URI.create(resultName).getPath().substring(1)));
        } else {
          fqName = new StringBuffer(readCompositeName(resultName));
        }
      }
      newDn = fqName.toString();
    }
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("processed dn: " + newDn);
    }
    return newDn;
  }


  /**
   * Uses a <code>CompositeName</code> to parse the supplied string.
   *
   * @param  s  <code>String</code> composite name to read
   *
   * @return  <code>String</code> ldap name
   */
  private String readCompositeName(final String s)
  {
    final StringBuffer name = new StringBuffer();
    try {
      final CompositeName cName = new CompositeName(s);
      for (int i = 0; i < cName.size(); i++) {
        name.append(cName.get(i));
        if (i + 1 < cName.size()) {
          name.append("/");
        }
      }
    } catch (InvalidNameException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Error formatting name: " + s, e);
      }
    }
    return name.toString();
  }
}
