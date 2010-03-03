/*
  $Id$

  Copyright (C) 2008-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Loads an X.509 certificate credential from a classpath, filepath,
 * or stream resource. Supported certificate formats include:
 * PEM, DER, and PKCS7.
 *
 * @author  Middleware Services
 * @version $Revision$
 */
public class X509CertificateCredentialReader
  extends AbstractCredentialReader<X509Certificate>
{

  /** {@inheritDoc} */
  public X509Certificate read(final InputStream is, final String ... params)
    throws IOException, GeneralSecurityException
  {
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    return (X509Certificate) cf.generateCertificate(
      this.getBufferedInputStream(is));
  }
}
