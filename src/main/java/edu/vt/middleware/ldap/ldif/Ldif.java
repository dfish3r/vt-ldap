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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.LdapConstants;
import edu.vt.middleware.ldap.bean.LdapAttribute;
import edu.vt.middleware.ldap.bean.LdapEntry;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>Ldif</code> contains functions for converting LDAP search result sets
 * into LDIF.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class Ldif implements Serializable
{

  /** ASCII decimal value of nul */
  public static final int NUL_CHAR = 0;

  /** ASCII decimal value of line feed */
  public static final int LF_CHAR = 10;

  /** ASCII decimal value of carriage return */
  public static final int CR_CHAR = 13;

  /** ASCII decimal value of space */
  public static final int SP_CHAR = 32;

  /** ASCII decimal value of colon */
  public static final int COLON_CHAR = 58;

  /** ASCII decimal value of left arrow */
  public static final int LA_CHAR = 60;

  /** ASCII decimal value of highest character */
  public static final int MAX_ASCII_CHAR = 127;

  /** serial version uid */
  private static final long serialVersionUID = 4704446748426929544L;

  /** Log for this class */
  private static final Log LOG = LogFactory.getLog(Ldif.class);

  /** Line separator */
  private static final String LINE_SEPARATOR = System.getProperty(
    "line.separator");


  /**
   * This will take the results of a prior LDAP query and convert it to LDIF.
   *
   * @param  results  <code>Iterator</code> of LDAP search results
   *
   * @return  <code>String</code>
   */
  public String createLdif(final Iterator<SearchResult> results)
  {
    // build string from results
    final StringBuffer ldif = new StringBuffer();
    if (results != null) {
      while (results.hasNext()) {
        final SearchResult sr = results.next();
        ldif.append(createLdif(sr));
      }
    }

    return ldif.toString();
  }


  /**
   * This will take the result of a prior LDAP query and convert it to LDIF.
   *
   * @param  result  <code>SearchResult</code> to convert
   *
   * @return  <code>String</code>
   */
  public String createLdif(final SearchResult result)
  {
    // build string from results
    final StringBuffer ldif = new StringBuffer();
    if (result != null) {
      try {
        ldif.append(createLdifEntry(result)).append(LINE_SEPARATOR);
      } catch (NamingException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Error creating String from SearchResult", e);
        }
      }
    }
    return ldif.toString();
  }


  /**
   * This will take a LDAP search result and convert it to LDIF.
   *
   * @param  result  <code>SearchResult</code> to convert
   *
   * @return  <code>Document</code>
   *
   * @throws  NamingException  if an error occurs while reading the search
   * result
   */
  protected String createLdifEntry(final SearchResult result)
    throws NamingException
  {
    final StringBuffer entry = new StringBuffer();
    if (result != null) {

      final LdapEntry ldapEntry = new LdapEntry(result);
      String dn = ldapEntry.getDn();
      if (dn != null) {
        if (encodeData(dn)) {
          try {
            dn = new String(
              Base64.encodeBase64(dn.getBytes(LdapConstants.DEFAULT_CHARSET)),
              LdapConstants.DEFAULT_CHARSET);
            entry.append("dn:: ").append(dn).append(LINE_SEPARATOR);
          } catch (UnsupportedEncodingException e) {
            if (LOG.isErrorEnabled()) {
              LOG.error(
                "Could not encode dn using " + LdapConstants.DEFAULT_CHARSET);
            }
          }
        } else {
          entry.append("dn: ").append(dn).append(LINE_SEPARATOR);
        }
      }

      for (LdapAttribute attr : ldapEntry.getLdapAttributes().getAttributes()) {
        final String attrName = attr.getName();
        for (Object attrValue : attr.getValues()) {
          if (encodeData(attrValue)) {
            try {
              if (attrValue instanceof String) {
                final String attrValueString = (String) attrValue;
                attrValue = new String(
                  Base64.encodeBase64(
                    attrValueString.getBytes(LdapConstants.DEFAULT_CHARSET)),
                  LdapConstants.DEFAULT_CHARSET);
                entry.append(attrName).append(":: ").append(attrValue).append(
                  LINE_SEPARATOR);
              } else if (attrValue instanceof byte[]) {
                final byte[] attrValueBytes = (byte[]) attrValue;
                attrValue = new String(
                  Base64.encodeBase64(attrValueBytes),
                  LdapConstants.DEFAULT_CHARSET);
                entry.append(attrName).append(":: ").append(attrValue).append(
                  LINE_SEPARATOR);
              } else {
                if (LOG.isWarnEnabled()) {
                  LOG.warn(
                    "Could not cast attribute value as a byte[]" +
                    " or a String");
                }
              }
            } catch (UnsupportedEncodingException e) {
              if (LOG.isErrorEnabled()) {
                LOG.error(
                  "Could not encode attribute value using " +
                  LdapConstants.DEFAULT_CHARSET);
              }
            }
          } else {
            try {
              new URL((String) attrValue);
              entry.append(attrName).append(":< ").append(attrValue).append(
                LINE_SEPARATOR);
            } catch (Exception e) {
              entry.append(attrName).append(": ").append(attrValue).append(
                LINE_SEPARATOR);
            }
          }
        }
      }
    }

    return entry.toString();
  }


  /**
   * This determines whether the supplied data should be base64 encoded. See
   * http://www.faqs.org/rfcs/rfc2849.html for more details.
   *
   * @param  data  <code>Object</code> to inspect
   *
   * @return  <code>boolean</code>
   */
  private boolean encodeData(final Object data)
  {
    boolean encode = false;
    if (data instanceof String) {
      final String stringData = (String) data;
      final char[] dataCharArray = stringData.toCharArray();
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
    } else {
      encode = true;
    }
    return encode;
  }


  /**
   * This will write the supplied LDAP search results to the supplied output
   * stream in LDIF form.
   *
   * @param  results  <code>Iterator</code> of LDAP search results
   * @param  out  <code>OutputStream</code> to write to
   *
   * @throws  IOException  if an error occurs while writing to the output stream
   */
  public void outputLdif(
    final Iterator<SearchResult> results,
    final OutputStream out)
    throws IOException
  {
    output(createLdif(results), out);
  }


  /**
   * This will write the supplied LDAP search result to the supplied output
   * stream in LDIF form.
   *
   * @param  result  <code>SearchResult</code> to write
   * @param  out  <code>OutputStream</code> to write to
   *
   * @throws  IOException  if an error occurs while writing to the output stream
   * @throws  NamingException  if an error occurs while reading the search
   * result
   */
  public void outputLdif(final SearchResult result, final OutputStream out)
    throws IOException, NamingException
  {
    output(createLdif(result), out);
  }


  /**
   * This will write the supplied LDIF to the supplied output stream.
   *
   * @param  ldif  <code>String</code> to write
   * @param  out  <code>OutputStream</code> to write to
   *
   * @throws  IOException  if an error occurs while writing to the output stream
   */
  public void output(final String ldif, final OutputStream out)
    throws IOException
  {
    if (ldif != null && out != null) {
      out.write(ldif.getBytes(LdapConstants.DEFAULT_CHARSET));
    }
  }
}
