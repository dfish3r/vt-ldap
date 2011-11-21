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
 * Provides a hook in the DER parser for handling specific paths as they are
 * encountered.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface ParseHandler
{


  /**
   * Invoked when a DER path is encountered that belongs to this parse handler.
   *
   * @param  parser  that invoked this handler
   * @param  encoded  to handle
   */
  void handle(DERParser parser, ByteBuffer encoded);
}
