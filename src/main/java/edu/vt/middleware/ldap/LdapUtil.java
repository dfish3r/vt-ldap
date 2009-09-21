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
   * This will convert the supplied value to a base64 encoded string. Returns
   * null if the bytes cannot be encoded.
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
   * This will convert the supplied value to a base64 encoded string. Returns
   * null if the string cannot be encoded.
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
