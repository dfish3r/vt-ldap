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
package edu.vt.middleware.ldap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides utility methods for this package.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public final class LdapUtil
{
  /**
   * UTF-8 character set identifier. The value of this constant is
   * {@value}.
   */
  public static final String UTF8_CHARSET = "UTF-8";

  /** Size of buffer in bytes to use when reading files. */
  private static final int READ_BUFFER_SIZE = 128;

  /** Default constructor. */
  private LdapUtil() {}


  /**
   * This will convert the supplied value to a base64 encoded string. Returns
   * null if the bytes cannot be encoded.
   *
   * @param  value  to base64 encode
   *
   * @return  base64 encoded value
   */
  public static String base64Encode(final byte[] value)
  {
    String encodedValue = null;
    if (value != null) {
      try {
        encodedValue = new String(Base64.encodeBase64(value), UTF8_CHARSET);
      } catch (UnsupportedEncodingException e) {
        final Log logger = LogFactory.getLog(LdapUtil.class);
        if (logger.isErrorEnabled()) {
          logger.error("Could not encode value using " + UTF8_CHARSET, e);
        }
      }
    }
    return encodedValue;
  }


  /**
   * This will convert the supplied value to a base64 encoded string. Returns
   * null if the string cannot be encoded.
   *
   * @param  value  to base64 encode
   *
   * @return  base64 encoded value
   */
  public static String base64Encode(final String value)
  {
    String encodedValue = null;
    if (value != null) {
      try {
        encodedValue = base64Encode(value.getBytes(UTF8_CHARSET));
      } catch (UnsupportedEncodingException e) {
        final Log logger = LogFactory.getLog(LdapUtil.class);
        if (logger.isErrorEnabled()) {
          logger.error("Could not encode value using " + UTF8_CHARSET, e);
        }
      }
    }
    return encodedValue;
  }


  /**
   * This will convert the supplied value to a UTF-8 encoded byte array. Returns
   * null if the string cannot be encoded.
   *
   * @param  value  to UTF-8 encode
   *
   * @return  UTF-8 encoded value
   */
  public static byte[] utf8Encode(final String value)
  {
    byte[] encodedValue = null;
    if (value != null) {
      try {
        encodedValue = value.getBytes(UTF8_CHARSET);
      } catch (UnsupportedEncodingException e) {
        final Log logger = LogFactory.getLog(LdapUtil.class);
        if (logger.isErrorEnabled()) {
          logger.error("Could not encode value using " + UTF8_CHARSET, e);
        }
      }
    }
    return encodedValue;
  }


  /**
   * This will decode the supplied value as a base64 encoded string to a byte[].
   *
   * @param  value  to base64 decode
   *
   * @return  base64 decoded value
   */
  public static byte[] base64Decode(final String value)
  {
    byte[] decodedValue = null;
    if (value != null) {
      decodedValue = Base64.decodeBase64(value.getBytes());
    }
    return decodedValue;
  }


  /**
   * Reads the data at the supplied URL and returns it as a byte array.
   *
   * @param  url  to read
   *
   * @return  bytes read from the URL
   *
   * @throws  IOException  if an error occurs reading data
   */
  public static byte[] readURL(final URL url)
    throws IOException
  {
    return readInputStream(url.openStream());
  }


  /**
   * Reads the data in the supplied stream and returns it as a byte array.
   *
   * @param  is  stream to read
   *
   * @return  bytes read from the stream
   *
   * @throws  IOException  if an error occurs reading data
   */
  public static byte[] readInputStream(final InputStream is)
    throws IOException
  {
    final ByteArrayOutputStream data = new ByteArrayOutputStream();
    try {
      final byte[] buffer = new byte[READ_BUFFER_SIZE];
      int length;
      while ((length = is.read(buffer)) != -1) {
        data.write(buffer, 0, length);
      }
    } finally {
      is.close();
      data.close();
    }
    return data.toByteArray();
  }
}
