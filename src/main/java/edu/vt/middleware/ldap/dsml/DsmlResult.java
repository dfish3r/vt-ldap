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
import java.io.StringReader;
import java.io.StringWriter;
import javax.naming.NamingException;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
import org.dom4j.DocumentException;

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
   * This reads any entries in the supplied DSML into this
   * <code>DsmlResult</code>.
   *
   * @param  dsml  <code>String</code> to read
   * @throws DocumentException if an error occurs reading the supplied DSML
   */
  public void fromDsmlv1(final String dsml)
    throws DocumentException
  {
    try {
      this.addEntries(
        this.dsmlv1.importDsml(new StringReader(dsml)));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read dsml from StringReader", e);
      }
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Unexpected naming exception occurred", e);
      }
    }
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


  /**
   * This reads any entries in the supplied DSML into this
   * <code>DsmlResult</code>.
   *
   * @param  dsml  <code>String</code> to read
   * @throws DocumentException if an error occurs reading the supplied DSML
   */
  public void fromDsmlv2(final String dsml)
    throws DocumentException
  {
    try {
      this.addEntries(
        this.dsmlv2.importDsml(new StringReader(dsml)));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read dsml from StringReader", e);
      }
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Unexpected naming exception occurred", e);
      }
    }
  }
}
