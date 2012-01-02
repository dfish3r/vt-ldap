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

/**
 * Interface for encoding DER objects.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface DEREncoder
{


  /**
   * Encode this object into it's DER type.
   *
   * @return  DER encoded object
   */
  byte[] encode();
}
