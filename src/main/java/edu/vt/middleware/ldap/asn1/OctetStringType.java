/*
  $Id$

  Copyright (C) 2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.asn1;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Converts strings to and from their DER encoded format.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class OctetStringType extends AbstractDERType implements DEREncoder
{

  /** String to encode. */
  private final byte[] derItem;


  /**
   * Creates a new octet string type.
   *
   * @param  item  to DER encode
   */
  public OctetStringType(final String item)
  {
    derItem = item.getBytes(Charset.forName("UTF-8"));
  }


  /**
   * Creates a new octet string type.
   *
   * @param  item  to DER encode
   */
  public OctetStringType(final byte[] item)
  {
    derItem = item;
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    return encode(UniversalDERTag.OCTSTR.getTagNo(), derItem);
  }


  /**
   * Converts bytes in the buffer to a string by reading from the current
   * position to the limit, which assumes the bytes of the string are in
   * big-endian order.
   *
   * @param  encoded  buffer containing DER-encoded data where the buffer is
   * positioned at the start of string bytes and the limit is set beyond the
   * last byte of string data.
   *
   * @return  decoded bytes as an string
   */
  public static String decode(final ByteBuffer encoded)
  {
    return new String(readBuffer(encoded), Charset.forName("UTF-8"));
  }


  /**
   * Converts the supplied string to a byte array using the UTF-8 encoding.
   *
   * @param  s  to convert
   *
   * @return  byte array
   */
  public static byte[] toBytes(final String s)
  {
    return s.getBytes(Charset.forName("UTF-8"));
  }
}
