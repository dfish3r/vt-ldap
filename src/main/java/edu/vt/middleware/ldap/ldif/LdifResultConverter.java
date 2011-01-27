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
package edu.vt.middleware.ldap.ldif;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import edu.vt.middleware.ldap.LdapResult;
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

  /** Class for outputting LDIF. */
  private Ldif ldif = new Ldif();


  /**
   * This returns this <code>LdifResult</code> as LDIF.
   *
   * @param  result  <code>LdapResult</code> to convert
   *
   * @return  <code>String</code>
   */
  public String toLdif(final LdapResult result)
  {
    final StringWriter writer = new StringWriter();
    try {
      this.ldif.outputLdif(result, writer);
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
   *
   * @return  <code>LdapResult</code>
   */
  public LdapResult fromLdif(final String ldif)
  {
    LdapResult result = null;
    try {
      result = this.ldif.importLdif(new StringReader(ldif));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read ldif from StringReader", e);
      }
    }
    return result;
  }
}
