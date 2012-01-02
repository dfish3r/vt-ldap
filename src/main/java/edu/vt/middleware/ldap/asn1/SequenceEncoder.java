/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Encodes sequences to their DER format.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SequenceEncoder extends AbstractDERType implements DEREncoder
{

  /** Constructed tags should have the 6th bit set. */
  public static final int ASN_CONSTRUCTED = 0x20;

  /** Encoders in this sequence. */
  private DEREncoder[] derEncoders;


  /**
   * Creates a new sequence encoder.
   *
   * @param  encoders  to encode in this sequence
   */
  public SequenceEncoder(final DEREncoder... encoders)
  {
    derEncoders = encoders;
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try {
      try {
        for (DEREncoder encoder : derEncoders) {
          bytes.write(encoder.encode());
        }
      } finally {
        bytes.close();
      }
    } catch (IOException e) {
      throw new IllegalStateException("Encode failed", e);
    }
    return
      encode(
        UniversalDERTag.SEQ.getTagNo() | ASN_CONSTRUCTED,
        bytes.toByteArray());
  }
}
