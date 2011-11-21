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
public class IntegerConverter extends AbstractDERConverter<BigInteger>
{


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
  public BigInteger decode(final ByteBuffer encoded)
  {
    final byte[] bytes = new byte[encoded.limit() - encoded.position()];
    encoded.get(bytes);
    return new BigInteger(bytes);
  }
}
