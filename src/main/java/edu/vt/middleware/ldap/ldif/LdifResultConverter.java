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
package edu.vt.middleware.ldap.ldif;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.naming.NamingException;
import edu.vt.middleware.ldap.bean.LdapBeanFactory;
import edu.vt.middleware.ldap.bean.LdapBeanProvider;
import edu.vt.middleware.ldap.bean.LdapResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>LdifResultConverter</code> provides utility methods for converting
 * <code>LdapResult</code> to and from LDIF in string format.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdifResultConverter
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** Ldap bean factory. */
  protected LdapBeanFactory beanFactory = LdapBeanProvider.getLdapBeanFactory();

  /** Class for outputting LDIF. */
  private Ldif ldif = new Ldif();


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
    }
  }


  /**
   * This returns this <code>LdifResult</code> as LDIF.
   *
   * @param  result  <code>LdapResult</code> to convert
   * @return  <code>String</code>
   */
  public String toLdif(final LdapResult result)
  {
    final StringWriter writer = new StringWriter();
    try {
      this.ldif.outputLdif(result.toSearchResults().iterator(), writer);
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not write ldif to StringWriter", e);
      }
    }
    return writer.toString();
  }


  /**
   * This reads any entries in the supplied LDIF into this <code>
   * LdifResult</code>.
   *
   * @param  ldif  <code>String</code> to read
   * @return  <code>LdapResult</code>
   */
  public LdapResult fromLdif(final String ldif)
  {
    final LdapResult result = this.beanFactory.newLdapResult();
    try {
      result.addEntries(this.ldif.importLdif(new StringReader(ldif)));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read ldif from StringReader", e);
      }
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Unexpected naming exception occurred", e);
      }
    }
    return result;
  }
}
