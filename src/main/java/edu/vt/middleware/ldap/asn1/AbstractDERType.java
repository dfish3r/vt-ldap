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
 * Provides functionality common to DER types implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractDERType
{

  /** Maximum supported length. */
  private static final int MAX_LENGTH = 127;


  /**
   * DER encode the supplied items with the supplied tag.  Does not support
   * encodings where the length is greater than 127 bytes long.
   *
   * @param  tag  for this DER type
   * @param  items  to encode
   *
   * @return  DER encoded items
   */
  protected static byte[] encode(final int tag, final byte[] ... items)
  {
    int itemLength = 0;
    for (byte[] b : items) {
      itemLength += b.length;
    }
    if (itemLength > MAX_LENGTH) {
      throw new IllegalArgumentException("Long form is not supported");
    }
    // add 1 for the type tag and 1 for the asn length
    final ByteBuffer encodedItem = ByteBuffer.allocate(itemLength + 2);
    encodedItem.put((byte) tag);
    encodedItem.put((byte) itemLength);
    for (byte[] b : items) {
      encodedItem.put(b);
    }
    return encodedItem.array();
  }


  /**
   * Returns a byte array containing the bytes from {@link ByteBuffer#limit()}
   * to {@link ByteBuffer#position()}.
   *
   * @param  encoded  to read bytes from
   *
   * @return  bytes
   */
  public static byte[] readBuffer(final ByteBuffer encoded)
  {
    final byte[] bytes = new byte[encoded.limit() - encoded.position()];
    encoded.get(bytes);
    return bytes;
  }
}
