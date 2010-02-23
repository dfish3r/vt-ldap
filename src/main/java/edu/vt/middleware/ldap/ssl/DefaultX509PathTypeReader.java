/*
  $Id: LdapTLSSocketFactory.java 1106 2010-01-30 04:34:13Z dfisher $

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1106 $
  Updated: $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
*/
package edu.vt.middleware.ldap.ssl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import edu.vt.middleware.ldap.LdapUtil;

/**
 * PathTypeReader implementation that uses native JDK functionality to read
 * certificates and private keys. Supported certificate formats include:
 * PEM, DER, and PKCS7. Supported private key formats include: PKCS7.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class DefaultX509PathTypeReader extends AbstractX509PathTypeReader
{


  /** {@inheritDoc} */
  public SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException
  {
    final X509SSLContextInitializer sslInit = new X509SSLContextInitializer();
    sslInit.setTrustCertificates(this.readTrustCertificates());
    sslInit.setAuthenticationCertificate(this.readAuthenticationCertificate());
    sslInit.setAuthenticationKey(this.readAuthenticationKey());
    return sslInit;
  }


  /** {@inheritDoc} */
  protected Certificate[] loadCertificateChain(final InputStream is)
    throws GeneralSecurityException
  {
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    final InputStream in = new BufferedInputStream(is);

    final List<X509Certificate> certList = new ArrayList<X509Certificate>();
    try {
      while (in.available() > 0) {
        final X509Certificate cert =
          (X509Certificate) cf.generateCertificate(in);
        if (cert != null) {
          certList.add(cert);
        }
      }
    } catch (IOException e) {
      throw new GeneralSecurityException(e);
    }
    return certList.toArray(new X509Certificate[0]);
  }


  /** {@inheritDoc} */
  protected Certificate loadCertificate(final InputStream is)
    throws GeneralSecurityException
  {
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    return (X509Certificate) cf.generateCertificate(is);
  }


  /** {@inheritDoc} */
  protected PrivateKey loadPrivateKey(final InputStream is)
    throws GeneralSecurityException
  {
    final KeyFactory kf = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec spec;
    try {
      spec = new PKCS8EncodedKeySpec(
        LdapUtil.readInputStream(is));
    } catch (IOException e) {
      throw new GeneralSecurityException(e);
    }
    return kf.generatePrivate(spec);
  }
}
