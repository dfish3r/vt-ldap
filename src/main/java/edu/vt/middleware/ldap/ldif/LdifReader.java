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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.SortBehavior;

/**
 * Reads an LDIF from a {@link Reader} and supplies an {@link LdapResult}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LdifReader
{
  /** Reader to read from. */
  protected final Reader ldifReader;

  /** Sort behavior. */
  protected final SortBehavior sortBehavior;


  /**
   * Creates a new ldif reader.
   *
   * @param  reader  to read LDIF from
   */
  public LdifReader(final Reader reader)
  {
    this(reader, SortBehavior.getDefaultSortBehavior());
  }


  /**
   * Creates a new ldif reader.
   *
   * @param  reader  to read LDIF from
   * @param  sb  sort behavior of the ldap result
   */
  public LdifReader(final Reader reader, final SortBehavior sb)
  {
    ldifReader = reader;
    if (sb == null) {
      throw new IllegalArgumentException("Sort behavior cannot be null");
    }
    sortBehavior = sb;
  }


  /**
   * Reads LDIF data from the reader and returns an ldap result.
   *
   * @return  ldap result derived from the LDIF
   * @throws  IOException  if an error occurs using the reader
   */
  public LdapResult read()
    throws IOException
  {
    final LdapResult ldapResult = new LdapResult(sortBehavior);
    final BufferedReader br = new BufferedReader(ldifReader);
    String line = null;
    int lineCount = 0;
    LdapEntry ldapEntry = null;
    StringBuffer lineValue = new StringBuffer();

    while ((line = br.readLine()) != null) {
      lineCount++;
      if (line.startsWith("dn:")) {
        lineValue.append(line);
        ldapEntry = new LdapEntry(sortBehavior);
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
          ldapEntry = new LdapEntry(sortBehavior);
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
            if ("dn".equals(attrName)) {
              ldapEntry.setDn(attrValue);
            } else {
              LdapAttribute ldapAttr = ldapEntry.getAttribute(attrName);
              if (ldapAttr == null) {
                ldapAttr = new LdapAttribute(sortBehavior);
                ldapAttr.setName(attrName);
                ldapEntry.addAttribute(ldapAttr);
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
