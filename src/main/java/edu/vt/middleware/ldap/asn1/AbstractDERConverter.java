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
 * Base implementation for DER converters.
 *
 * @param  <T>  type of DER converter
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractDERConverter<T> implements DERConverter<T>
{


  /** {@inheritDoc} */
  @Override
  public void encode(final T item, final ByteBuffer encoded)
  {
    throw new UnsupportedOperationException("Not implemented");
  }
}
