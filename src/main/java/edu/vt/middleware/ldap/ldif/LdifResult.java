/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.ldif;

import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>LdifResult</code> represents a DSML search result.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class LdifResult extends LdapResult
{

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(LdifResult.class);


  /** Default constructor. */
  public LdifResult() {}


  /**
   * This will create a new <code>LdifResult</code> with the supplied <code>
   * LdapResult</code>.
   *
   * @param  r  <code>LdapResult</code>
   */
  public LdifResult(final LdapResult r)
  {
    super(r);
  }


  /**
   * This will create a new <code>LdifResult</code> with the supplied <code>
   * LdapEntry</code>.
   *
   * @param  e  <code>LdapEntry</code>
   */
  public LdifResult(final LdapEntry e)
  {
    super(e);
  }


  /**
   * This returns this <code>LdifResult</code> as LDIF.
   *
   * @return  <code>String</code>
   */
  public String toLdif()
  {
    return (new Ldif()).createLdif(this.toSearchResults().iterator());
  }
}
