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
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;

/**
 * <code>LdifResult</code> represents an LDIF search result.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class LdifResult extends LdapResult
{

  /** Class for outputting LDIF. */
  private Ldif ldif = new Ldif();


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
    final StringWriter writer = new StringWriter();
    try {
      this.ldif.outputLdif(this.toSearchResults().iterator(), writer);
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
   */
  public void fromLdif(final String ldif)
  {
    try {
      this.addEntries(this.ldif.importLdif(new StringReader(ldif)));
    } catch (IOException e) {
      if (this.logger.isWarnEnabled()) {
        this.logger.warn("Could not read ldif from StringReader", e);
      }
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Unexpected naming exception occurred", e);
      }
    }
  }
}
