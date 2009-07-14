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
package edu.vt.middleware.ldap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>LdapUtil</code> provides helper methods for <code>Ldap</code>.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class LdapUtil
{

  /** Log for this class. */
  private static final Log LOG = LogFactory.getLog(LdapUtil.class);


  /** Default constructor. */
  private LdapUtil() {}


  /**
   * This will recursively query the LDAP for entries matching the supplied
   * attributes which contain attributes that references other entries in the
   * search scope. See {@link Ldap#searchAttributes(Attributes)}.
   *
   * @param  ldap  <code>Ldap</code> to search with
   * @param  matchAttrs  <code>Attributes</code> attributes to match
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public static Iterator<SearchResult> searchAttributesRecursive(
    final Ldap ldap,
    final Attributes matchAttrs)
    throws NamingException
  {
    return searchAttributesRecursive(ldap, matchAttrs, null);
  }


  /**
   * This will recursively query the LDAP for entries matching the supplied
   * attributes which contain attributes that references other entries in the
   * search scope. See {@link Ldap#searchAttributes(Attributes, String[])}.
   *
   * @param  ldap  <code>Ldap</code> to search with
   * @param  matchAttrs  <code>Attributes</code> attributes to match
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  public static Iterator<SearchResult> searchAttributesRecursive(
    final Ldap ldap,
    final Attributes matchAttrs,
    final String[] retAttrs)
    throws NamingException
  {
    final List<SearchResult> results = searchAttributesRecursive(
      ldap,
      matchAttrs,
      retAttrs,
      new ArrayList<String>());
    return results.iterator();
  }


  /**
   * This will recursively query the LDAP for entries matching the supplied
   * attributes which contain attributes that references other entries in the
   * search scope.
   *
   * @param  ldap  <code>Ldap</code> to search with
   * @param  matchAttrs  <code>Attributes</code> attributes to match
   * @param  retAttrs  <code>String[]</code> attributes to return
   * @param  parentDns  <code>List</code> to check for circular references
   *
   * @return  <code>Iterator</code> - of LDAP search results
   *
   * @throws  NamingException  if the LDAP returns an error
   */
  private static List<SearchResult> searchAttributesRecursive(
    final Ldap ldap,
    final Attributes matchAttrs,
    final String[] retAttrs,
    final List<String> parentDns)
    throws NamingException
  {
    final List<SearchResult> results = new ArrayList<SearchResult>();
    final Iterator<SearchResult> i = ldap.searchAttributes(
      matchAttrs,
      retAttrs);
    while (i.hasNext()) {
      final SearchResult sr = i.next();
      results.add(sr);
      parentDns.add(sr.getName());
      if (sr != null) {
        final Attributes attrs = sr.getAttributes();
        final NamingEnumeration<? extends Attribute> ae = attrs.getAll();
        while (ae.hasMore()) {
          final Attribute attr = ae.next();
          if (attr != null) {
            final NamingEnumeration<?> e = attr.getAll();
            while (e.hasMore()) {
              final Object rawValue = e.next();
              if (rawValue instanceof String) {
                String value = (String) rawValue;
                final String dn = ldap.getLdapConfig().getBase();
                if (value.endsWith(dn) && !parentDns.contains(value)) {
                  final String attrId = value.substring(0, value.indexOf("="));
                  value = value.substring(
                    value.indexOf("=") + 1,
                    value.indexOf(dn) - 1);
                  matchAttrs.put(attrId, value);
                  results.addAll(
                    searchAttributesRecursive(
                      ldap,
                      matchAttrs,
                      retAttrs,
                      parentDns));
                }
              } else {
                if (LOG.isDebugEnabled()) {
                  LOG.debug("Recursive search does not support binary values");
                }
              }
            }
          }
        }
      }
    }
    return results;
  }


  /**
   * This checks a credential to ensure it is the right type and it is not
   * empty. A credential can be of type String, char[], or byte[].
   *
   * @param  credential  <code>Object</code> to check
   *
   * @return  <code>boolean</code> - whether the credential is valid
   */
  public static boolean checkCredential(final Object credential)
  {
    boolean answer = false;
    if (credential != null) {
      if (credential instanceof String) {
        final String string = (String) credential;
        if (!string.equals("")) {
          answer = true;
        }
      } else if (credential instanceof char[]) {
        final char[] array = (char[]) credential;
        if (array.length != 0) {
          answer = true;
        }
      } else if (credential instanceof byte[]) {
        final byte[] array = (byte[]) credential;
        if (array.length != 0) {
          answer = true;
        }
      }
    }
    return answer;
  }


  /**
   * This will convert the supplied value to a base64 encoded string.
   * Returns null if the bytes cannot be encoded.
   *
   * @param  value  <code>byte[]</code> to base64 encode
   *
   * @return  <code>String</code>
   */
  public static String base64Encode(final byte[] value)
  {
    String encodedValue = null;
    if (value != null) {
      try {
        encodedValue = new String(
          Base64.encodeBase64(value),
          LdapConstants.DEFAULT_CHARSET);
      } catch (UnsupportedEncodingException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error(
            "Could not encode value using " + LdapConstants.DEFAULT_CHARSET);
        }
      }
    }
    return encodedValue;
  }


  /**
   * This will convert the supplied value to a base64 encoded string.
   * Returns null if the string cannot be encoded.
   *
   * @param  value  <code>String</code> to base64 encode
   *
   * @return  <code>String</code>
   */
  public static String base64Encode(final String value)
  {
    String encodedValue = null;
    if (value != null) {
      try {
        encodedValue = base64Encode(
          value.getBytes(LdapConstants.DEFAULT_CHARSET));
      } catch (UnsupportedEncodingException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error(
            "Could not encode value using " + LdapConstants.DEFAULT_CHARSET);
        }
      }
    }
    return encodedValue;
  }


  /**
   * This will decode the supplied value as a base64 encoded string to a byte[].
   *
   * @param  value  <code>Object</code> to base64 encode
   *
   * @return  <code>String</code>
   */
  public static byte[] base64Decode(final String value)
  {
    byte[] decodedValue = null;
    if (value != null) {
      decodedValue = Base64.decodeBase64(value.getBytes());
    }
    return decodedValue;
  }
}
