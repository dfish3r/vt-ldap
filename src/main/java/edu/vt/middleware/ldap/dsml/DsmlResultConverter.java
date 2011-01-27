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
package edu.vt.middleware.ldap.dsml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import edu.vt.middleware.ldap.LdapResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;

/**
 * <code>DsmlResultConverter</code> provides utility methods for converting
 * <code>LdapResult</code> to and from DSML in string format.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DsmlResultConverter
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Class for outputting version 1 DSML. */
  private Dsmlv1 dsmlv1 = new Dsmlv1();

  /** Class for outputting version 2 DSML. */
  private Dsmlv2 dsmlv2 = new Dsmlv2();


  /**
   * This returns this <code>DsmlResult</code> as version 1 DSML.
   *
   * @param  result  <code>LdapResult</code> to convert
   *
   * @return  <code>String</code>
   */
  public String toDsmlv1(final LdapResult result)
  {
    final StringWriter writer = new StringWriter();
    try {
      this.dsmlv1.outputDsml(result, writer);
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not write dsml to StringWriter", e);
      }
    }
    return writer.toString();
  }


  /**
   * This reads any entries in the supplied DSML into this <code>
   * DsmlResult</code>.
   *
   * @param  dsml  <code>String</code> to read
   *
   * @return  <code>LdapResult</code>
   *
   * @throws  DocumentException  if an error occurs reading the supplied DSML
   */
  public LdapResult fromDsmlv1(final String dsml)
    throws DocumentException
  {
    LdapResult result = null;
    try {
      result = this.dsmlv1.importDsml(new StringReader(dsml));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read dsml from StringReader", e);
      }
    }
    return result;
  }


  /**
   * This returns this <code>DsmlResult</code> as version 2 DSML.
   *
   * @param  result  <code>LdapResult</code> to convert
   *
   * @return  <code>String</code>
   */
  public String toDsmlv2(final LdapResult result)
  {
    final StringWriter writer = new StringWriter();
    try {
      this.dsmlv2.outputDsml(result, writer);
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not write dsml to StringWriter", e);
      }
    }
    return writer.toString();
  }


  /**
   * This reads any entries in the supplied DSML into this <code>
   * DsmlResult</code>.
   *
   * @param  dsml  <code>String</code> to read
   *
   * @return  <code>LdapResult</code>
   *
   * @throws  DocumentException  if an error occurs reading the supplied DSML
   */
  public LdapResult fromDsmlv2(final String dsml)
    throws DocumentException
  {
    LdapResult result = null;
    try {
      result = this.dsmlv2.importDsml(new StringReader(dsml));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read dsml from StringReader", e);
      }
    }
    return result;
  }
}
