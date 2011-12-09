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

/**
 * Converts booleans to and from their DER encoded format.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class BooleanType extends AbstractDERType implements DEREncoder
{

  /** Boolean true byte representation. */
  private static final int TRUE_BYTE = 0xff;

  /** Boolean false byte representation. */
  private static final int FALSE_BYTE = 0x00;

  /** Boolean to encode. */
  private final byte[] derItem;


  /**
   * Creates a new boolean type.
   *
   * @param  item  to DER encode
   */
  public BooleanType(final boolean item)
  {
    derItem = toBytes(item);
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    return encode(UniversalDERTag.BOOL.getTagNo(), derItem);
  }


  /**
   * Converts bytes in the buffer to a boolean by reading from the current
   * position to the limit.
   *
   * @param  encoded  buffer containing DER-encoded data where the buffer is
   * positioned at the start of boolean bytes and the limit is set beyond the
   * last byte of integer data.
   *
   * @return  decoded bytes as a boolean.
   */
  public static boolean decode(final ByteBuffer encoded)
  {
    final byte[] bytes = readBuffer(encoded);
    if (bytes.length > 1) {
      throw new IllegalArgumentException(
        "Boolean cannot be longer than 1 byte");
    }
    if (bytes[0] == TRUE_BYTE) {
      return true;
    } else if (bytes[0] == FALSE_BYTE) {
      return false;
    } else {
      throw new IllegalArgumentException(
        "Invalid boolean value: " + (int) bytes[0]);
    }
  }


  /**
   * Converts the supplied boolean to a byte array.
   *
   * @param  b  to convert
   *
   * @return  byte array
   */
  public static byte[] toBytes(final boolean b)
  {
    return new byte[] {(byte) (b ? TRUE_BYTE : FALSE_BYTE)};
  }
}
