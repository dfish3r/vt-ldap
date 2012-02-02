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
package edu.vt.middleware.ldap.ssl;

import java.security.cert.X509Certificate;

/**
 * Interface for verifying a hostname matching a certificate.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface CertificateHostnameVerifier
{


  /**
   * Verify the supplied hostname matches the supplied certificate.
   *
   * @param  hostname  to verify
   * @param  cert  to verify hostname against
   *
   * @return  whether hostname is valid for the supplied certificate
   */
  boolean verify(final String hostname, final X509Certificate cert);
}
