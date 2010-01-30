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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.bean.LdapAttribute;
import edu.vt.middleware.ldap.bean.LdapBeanFactory;
import edu.vt.middleware.ldap.bean.LdapBeanProvider;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
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

  /** ASCII decimal value of nul. */
  public static final int NUL_CHAR = 0;

  /** ASCII decimal value of line feed. */
  public static final int LF_CHAR = 10;

  /** ASCII decimal value of carriage return. */
  public static final int CR_CHAR = 13;

  /** ASCII decimal value of space. */
  public static final int SP_CHAR = 32;

  /** ASCII decimal value of colon. */
  public static final int COLON_CHAR = 58;

  /** ASCII decimal value of left arrow. */
  public static final int LA_CHAR = 60;

  /** ASCII decimal value of highest character. */
  public static final int MAX_ASCII_CHAR = 127;

  /** serial version uid. */
  private static final long serialVersionUID = -3763879179455001975L;

  /** Line separator. */
  private static final String LINE_SEPARATOR = System.getProperty(
    "line.separator");

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Ldap bean factory. */
  protected LdapBeanFactory beanFactory = LdapBeanProvider.getLdapBeanFactory();


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
   * This will take the results of a prior LDAP query and convert it to LDIF.
   *
   * @param  results  <code>Iterator</code> of LDAP search results
   *
   * @return  <code>String</code>
   */
  public String createLdif(final Iterator<SearchResult> results)
  {
    String ldif = "";
    try {
      final LdapResult lr = this.beanFactory.newLdapResult();
      lr.addEntries(results);
      ldif = this.createLdif(lr);
    } catch (NamingException e) {
      if (this.logger.isErrorEnabled()) {
        this.logger.error("Error creating String from SearchResults", e);
      }
    }
    return ldif;
  }


  /**
   * This will take the results of a prior LDAP query and convert it to LDIF.
   *
   * @param  result  <code>LdapResult</code>
   *
   * @return  <code>String</code>
   */
  public String createLdif(final LdapResult result)
  {
    // build string from results
    final StringBuffer ldif = new StringBuffer();
    if (result != null) {
      for (LdapEntry le : result.getEntries()) {
        ldif.append(createLdifEntry(le));
      }
    }

    return ldif.toString();
  }


  /**
   * This will take an LDAP entry and convert it to LDIF.
   *
   * @param  ldapEntry  <code>LdapEntry</code> to convert
   *
   * @return  <code>String</code>
   */
  protected String createLdifEntry(final LdapEntry ldapEntry)
  {
    final StringBuffer entry = new StringBuffer();
    if (ldapEntry != null) {

      final String dn = ldapEntry.getDn();
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

      for (LdapAttribute attr : ldapEntry.getLdapAttributes().getAttributes()) {
        final String attrName = attr.getName();
        for (Object attrValue : attr.getValues()) {
          if (encodeData(attrValue)) {
            String encodedAttrValue = null;
            if (attrValue instanceof String) {
              encodedAttrValue = LdapUtil.base64Encode((String) attrValue);
            } else if (attrValue instanceof byte[]) {
              encodedAttrValue = LdapUtil.base64Encode((byte[]) attrValue);
            } else {
              if (this.logger.isWarnEnabled()) {
                this.logger.warn(
                  "Could not cast attribute value as a byte[]" +
                  " or a String");
              }
            }
            if (encodedAttrValue != null) {
              entry.append(attrName).append(":: ").append(encodedAttrValue)
                .append(LINE_SEPARATOR);
            }
          } else {
            entry.append(attrName).append(": ").append(attrValue).append(
              LINE_SEPARATOR);
          }
        }
      }
    }

    if (entry.length() > 0) {
      entry.append(LINE_SEPARATOR);
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
   * This will write the supplied LDAP search results to the supplied writer in
   * LDIF form.
   *
   * @param  results  <code>Iterator</code> of LDAP search results
   * @param  writer  <code>Writer</code> to write to
   *
   * @throws  IOException  if an error occurs while writing to the output stream
   */
  public void outputLdif(
    final Iterator<SearchResult> results,
    final Writer writer)
    throws IOException
  {
    writer.write(createLdif(results));
    writer.flush();
  }


  /**
   * This will write the supplied LDAP search results to the supplied writer in
   * LDIF form.
   *
   * @param  result  <code>LdapResult</code>
   * @param  writer  <code>Writer</code> to write to
   *
   * @throws  IOException  if an error occurs while writing to the output stream
   */
  public void outputLdif(
    final LdapResult result,
    final Writer writer)
    throws IOException
  {
    writer.write(createLdif(result));
    writer.flush();
  }


  /**
   * This will take a Reader containing an LDIF and convert it to an Iterator of
   * LDAP search results. Provides a loose implementation of RFC 2849. Should
   * not be used to validate LDIF format as it does not enforce strictness.
   *
   * @param  reader  <code>Reader</code> containing LDIF content
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  IOException  if an I/O error occurs
   */
  public Iterator<SearchResult> importLdif(final Reader reader)
    throws IOException
  {
    return this.importLdifToLdapResult(reader).toSearchResults().iterator();
  }


  /**
   * This will take a Reader containing an LDIF and convert it to an <code>
   * LdapResult</code>. Provides a loose implementation of RFC 2849. Should
   * not be used to validate LDIF format as it does not enforce strictness.
   *
   * @param  reader  <code>Reader</code> containing LDIF content
   *
   * @return  <code>LdapResult</code> - LDAP search results
   *
   * @throws  IOException  if an I/O error occurs
   */
  public LdapResult importLdifToLdapResult(final Reader reader)
    throws IOException
  {
    final LdapResult ldapResult = this.beanFactory.newLdapResult();
    final BufferedReader br = new BufferedReader(reader);
    String line = null;
    int lineCount = 0;
    LdapEntry ldapEntry = null;
    StringBuffer lineValue = new StringBuffer();

    while ((line = br.readLine()) != null) {
      lineCount++;
      if (line.startsWith("dn:")) {
        lineValue.append(line);
        ldapEntry = this.beanFactory.newLdapEntry();
        break;
      }
    }

    boolean read = true;
    while (read) {
      line = br.readLine();
      if (line == null) {
        read = false;
        line = "";
      }
      if (!line.startsWith("#")) {
        if (line.startsWith("dn:")) {
          ldapResult.addEntry(ldapEntry);
          ldapEntry = this.beanFactory.newLdapEntry();
        }
        if (line.startsWith(" ")) {
          lineValue.append(line.substring(1));
        } else {
          final String s = lineValue.toString();
          if (s.indexOf(":") != -1) {
            boolean isBinary = false;
            boolean isUrl = false;
            final String[] parts = s.split(":", 2);
            final String attrName = parts[0];
            String attrValue = parts[1];
            if (attrValue.startsWith(":")) {
              isBinary = true;
              attrValue = attrValue.substring(1);
            } else if (attrValue.startsWith("<")) {
              isUrl = true;
              attrValue = attrValue.substring(1);
            }
            if (attrValue.startsWith(" ")) {
              attrValue = attrValue.substring(1);
            }
            if (attrName.equals("dn")) {
              ldapEntry.setDn(attrValue);
            } else {
              LdapAttribute ldapAttr = ldapEntry.getLdapAttributes()
                  .getAttribute(attrName);
              if (ldapAttr == null) {
                ldapAttr = this.beanFactory.newLdapAttribute();
                ldapAttr.setName(attrName);
                ldapEntry.getLdapAttributes().addAttribute(ldapAttr);
              }
              if (isBinary) {
                ldapAttr.getValues().add(LdapUtil.base64Decode(attrValue));
              } else if (isUrl) {
                ldapAttr.getValues().add(LdapUtil.readURL(new URL(attrValue)));
              } else {
                ldapAttr.getValues().add(attrValue);
              }
            }
          }
          lineValue = new StringBuffer(line);
        }
      }
    }
    if (ldapEntry != null) {
      ldapResult.addEntry(ldapEntry);
    }
    return ldapResult;
  }
}
