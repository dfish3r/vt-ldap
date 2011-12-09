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

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Converts arbitrary-precision integers to and from their DER encoded format.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class IntegerType extends AbstractDERType implements DEREncoder
{

  /** Integer to encode. */
  private final byte[] derItem;


  /**
   * Creates a new integer type.
   *
   * @param  item  to DER encode
   */
  public IntegerType(final BigInteger item)
  {
    derItem = item.toByteArray();
  }


  /**
   * Creates a new integer type.
   *
   * @param  item  to DER encode
   */
  public IntegerType(final int item)
  {
    derItem = BigInteger.valueOf(item).toByteArray();
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    return encode(UniversalDERTag.INT.getTagNo(), derItem);
  }


  /**
   * Converts bytes in the buffer to an integer by reading from the current
   * position to the limit, which assumes the bytes of the integer are in
   * big-endian order.
   *
   * @param  encoded  buffer containing DER-encoded data where the buffer is
   * positioned at the start of integer bytes and the limit is set beyond the
   * last byte of integer data.
   *
   * @return  decoded bytes as an integer of arbitrary size.
   */
  public static BigInteger decode(final ByteBuffer encoded)
  {
    return new BigInteger(readBuffer(encoded));
  }


  /**
   * Converts the supplied big integer to a byte array.
   *
   * @param  i  to convert
   *
   * @return  byte array
   */
  public static byte[] toBytes(final BigInteger i)
  {
    return i.toByteArray();
  }
}
