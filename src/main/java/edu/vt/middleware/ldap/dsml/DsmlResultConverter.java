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
package edu.vt.middleware.ldap.dsml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.naming.NamingException;
import edu.vt.middleware.ldap.bean.LdapBeanFactory;
import edu.vt.middleware.ldap.bean.LdapBeanProvider;
import edu.vt.middleware.ldap.bean.LdapResult;
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

  /** Ldap bean factory. */
  protected LdapBeanFactory beanFactory = LdapBeanProvider.getLdapBeanFactory();

  /** Class for outputting version 1 DSML. */
  private Dsmlv1 dsmlv1 = new Dsmlv1();

  /** Class for outputting version 2 DSML. */
  private Dsmlv2 dsmlv2 = new Dsmlv2();


  /**
   * Returns the factory for creating ldap beans.
   *
   * @return  <code>LdapBeanFactory</code>
   */
  public LdapBeanFactory getLdapBeanFactory()
  {
    return this.beanFactory;
  }


  /**
   * Sets the factory for creating ldap beans.
   *
   * @param  lbf  <code>LdapBeanFactory</code>
   */
  public void setLdapBeanFactory(final LdapBeanFactory lbf)
  {
    if (lbf != null) {
      this.beanFactory = lbf;
      this.dsmlv1.setLdapBeanFactory(lbf);
      this.dsmlv2.setLdapBeanFactory(lbf);
    }
  }


  /**
   * This returns this <code>DsmlResult</code> as version 1 DSML.
   *
   * @param  result  <code>LdapResult</code> to convert
   * @return  <code>String</code>
   */
  public String toDsmlv1(final LdapResult result)
  {
    final StringWriter writer = new StringWriter();
    try {
      this.dsmlv1.outputDsml(result.toSearchResults().iterator(), writer);
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
   * @return  <code>LdapResult</code>
   *
   * @throws  DocumentException  if an error occurs reading the supplied DSML
   */
  public LdapResult fromDsmlv1(final String dsml)
    throws DocumentException
  {
    final LdapResult result = this.beanFactory.newLdapResult();
    try {
      result.addEntries(this.dsmlv1.importDsml(new StringReader(dsml)));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read dsml from StringReader", e);
      }
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Unexpected naming exception occurred", e);
      }
    }
    return result;
  }


  /**
   * This returns this <code>DsmlResult</code> as version 2 DSML.
   *
   * @param  result  <code>LdapResult</code> to convert
   * @return  <code>String</code>
   */
  public String toDsmlv2(final LdapResult result)
  {
    final StringWriter writer = new StringWriter();
    try {
      this.dsmlv2.outputDsml(result.toSearchResults().iterator(), writer);
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
   * @return  <code>LdapResult</code>
   *
   * @throws  DocumentException  if an error occurs reading the supplied DSML
   */
  public LdapResult fromDsmlv2(final String dsml)
    throws DocumentException
  {
    final LdapResult result = this.beanFactory.newLdapResult();
    try {
      result.addEntries(this.dsmlv2.importDsml(new StringReader(dsml)));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read dsml from StringReader", e);
      }
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Unexpected naming exception occurred", e);
      }
    }
    return result;
  }
}
