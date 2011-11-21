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
 * Interface for encoding and decoding DER objects.
 *
 * @param  <T>  type of DER converter
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface DERConverter<T>
{


  /**
   * Decode the supplied bytes into a DER type.
   *
   * @param  encoded  bytes
   * @return  DER type
   */
  T decode(ByteBuffer encoded);


  /**
   * Encode the supplied DER type with the supplied bytes.
   *
   * @param  item  to populate
   * @param  encoded  to read DER data from
   */
  void encode(T item, ByteBuffer encoded);
}
