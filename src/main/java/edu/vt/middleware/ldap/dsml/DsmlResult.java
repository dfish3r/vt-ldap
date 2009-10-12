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
package edu.vt.middleware.ldap.dsml;

import java.io.IOException;
import java.io.StringWriter;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;

/**
 * <code>DsmlResult</code> represents a DSML search result.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class DsmlResult extends LdapResult
{

  /** Class for outputting version 1 DSML. */
  private Dsmlv1 dsmlv1 = new Dsmlv1();

  /** Class for outputting version 2 DSML. */
  private Dsmlv2 dsmlv2 = new Dsmlv2();


  /** Default constructor. */
  public DsmlResult() {}


  /**
   * This will create a new <code>DsmlResult</code> with the supplied <code>
   * LdapResult</code>.
   *
   * @param  r  <code>LdapResult</code>
   */
  public DsmlResult(final LdapResult r)
  {
    super(r);
  }


  /**
   * This will create a new <code>DsmlResult</code> with the supplied <code>
   * DsmlEntry</code>.
   *
   * @param  e  <code>LdapEntry</code>
   */
  public DsmlResult(final LdapEntry e)
  {
    super(e);
  }


  /**
   * This returns this <code>DsmlResult</code> as version 1 DSML.
   *
   * @return  <code>String</code>
   */
  public String toDsmlv1()
  {
    final StringWriter writer = new StringWriter();
    try {
      this.dsmlv1.outputDsml(this.toSearchResults().iterator(), writer);
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not write dsml to StringWriter", e);
      }
    }
    return writer.toString();
  }


  /**
   * This returns this <code>DsmlResult</code> as version 2 DSML.
   *
   * @return  <code>String</code>
   */
  public String toDsmlv2()
  {
    final StringWriter writer = new StringWriter();
    try {
      this.dsmlv2.outputDsml(this.toSearchResults().iterator(), writer);
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not write dsml to StringWriter", e);
      }
    }
    return writer.toString();
  }
}
