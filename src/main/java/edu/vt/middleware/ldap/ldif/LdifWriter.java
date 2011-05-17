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
import java.io.Writer;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.LdapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes an LDIF to a {@link Writer} using an {@link LdapResult}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdifWriter
{
  /** ASCII decimal value of nul. */
  private static final int NUL_CHAR = 0;

  /** ASCII decimal value of line feed. */
  private static final int LF_CHAR = 10;

  /** ASCII decimal value of carriage return. */
  private static final int CR_CHAR = 13;

  /** ASCII decimal value of space. */
  private static final int SP_CHAR = 32;

  /** ASCII decimal value of colon. */
  private static final int COLON_CHAR = 58;

  /** ASCII decimal value of left arrow. */
  private static final int LA_CHAR = 60;

  /** ASCII decimal value of highest character. */
  private static final int MAX_ASCII_CHAR = 127;

  /** Line separator. */
  private static final String LINE_SEPARATOR = System.getProperty(
    "line.separator");

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Writer to write to. */
  protected final Writer ldifWriter;


  /**
   * Creates a new ldif writer.
   *
   * @param  writer  to write LDIF to
   */
  public LdifWriter(final Writer writer)
  {
    ldifWriter = writer;
  }


  /**
   * Writes the supplied ldap result to the writer.
   *
   * @param  result  ldap result to write
   * @throws  IOException  if an error occurs using the writer
   */
  public void write(final LdapResult result)
    throws IOException
  {
    ldifWriter.write(createLdif(result));
    ldifWriter.flush();
  }


  /**
   * Creates an LDIF using the supplied ldap result.
   *
   * @param  lr  ldap result
   *
   * @return  LDIF
   */
  protected String createLdif(final LdapResult lr)
  {
    // build string from results
    final StringBuilder ldif = new StringBuilder();
    if (lr != null) {
      for (LdapEntry le : lr.getEntries()) {
        ldif.append(createLdifEntry(le));
      }
    }

    return ldif.toString();
  }


  /**
   * Creates an LDIF using the supplied ldap entry.
   *
   * @param  le  ldap entry
   *
   * @return  LDIF
   */
  protected String createLdifEntry(final LdapEntry le)
  {
    if (le == null) {
      return "";
    }

    final StringBuffer entry = new StringBuffer();
    final String dn = le.getDn();
    if (dn != null) {
      if (encodeData(dn)) {
        final String encodedDn = LdapUtil.base64Encode(dn);
        if (encodedDn != null) {
          entry.append("dn:: ").append(dn).append(LINE_SEPARATOR);
        }
      } else {
        entry.append("dn: ").append(dn).append(LINE_SEPARATOR);
      }
    }

    for (LdapAttribute attr : le.getAttributes()) {
      final String attrName = attr.getName();
      for (String attrValue : attr.getStringValues()) {
        if (attr.isBinary()) {
          entry.append(attrName).append(":: ").append(attrValue)
            .append(LINE_SEPARATOR);
        } else if (encodeData(attrValue)) {
          entry.append(attrName).append(":: ").append(LdapUtil.base64Encode(
            attrValue)).append(LINE_SEPARATOR);
        } else {
          entry.append(attrName).append(": ").append(attrValue).append(
            LINE_SEPARATOR);
        }
      }
    }

    if (entry.length() > 0) {
      entry.append(LINE_SEPARATOR);
    }
    return entry.toString();
  }


  /**
   * Determines whether the supplied data should be base64 encoded. See
   * http://www.faqs.org/rfcs/rfc2849.html for more details.
   *
   * @param  data  to inspect
   *
   * @return  whether the data should be base64 encoded
   */
  private boolean encodeData(final String data)
  {
    boolean encode = false;
    final char[] dataCharArray = data.toCharArray();
    for (int i = 0; i < dataCharArray.length; i++) {
      final int charInt = (int) dataCharArray[i];
      // check for NUL
      if (charInt == NUL_CHAR) {
        encode = true;
        // check for LF
      } else if (charInt == LF_CHAR) {
        encode = true;
        // check for CR
      } else if (charInt == CR_CHAR) {
        encode = true;
        // check for SP at beginning or end of string
      } else if (
        charInt == SP_CHAR &&
          (i == 0 || i == dataCharArray.length - 1)) {
        encode = true;
        // check for colon(:) at beginning of string
      } else if (charInt == COLON_CHAR && i == 0) {
        encode = true;
        // check for left arrow(<) at beginning of string
      } else if (charInt == LA_CHAR && i == 0) {
        encode = true;
        // check for any character above 127
      } else if (charInt > MAX_ASCII_CHAR) {
        encode = true;
      }
    }
    return encode;
  }
}
