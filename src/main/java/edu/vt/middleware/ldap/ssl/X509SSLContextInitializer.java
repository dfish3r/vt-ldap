/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.ssl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Provides an SSL context initializer which can use X.509 certificates to
 * create key and trust managers.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class X509SSLContextInitializer extends AbstractSSLContextInitializer
{

  /** Certificates used to create trust managers. */
  private X509Certificate[] trustCerts;

  /** Certificate used to create key managers. */
  private X509Certificate authenticationCert;

  /** Private key used to create key managers. */
  private PrivateKey authenticationKey;


  /**
   * Returns the certificates to use for creating the trust managers.
   *
   * @return  X.509 certificates
   */
  public X509Certificate[] getTrustCertificates()
  {
    return trustCerts;
  }


  /**
   * Sets the certificates to use for creating the trust managers.
   *
   * @param  certs  X.509 certificates
   */
  public void setTrustCertificates(final X509Certificate[] certs)
  {
    trustCerts = certs;
  }


  /**
   * Returns the certificate to use for creating the key managers.
   *
   * @return  X.509 certificate
   */
  public X509Certificate getAuthenticationCertificate()
  {
    return authenticationCert;
  }


  /**
   * Sets the certificate to use for creating the key managers.
   *
   * @param  cert  X.509 certificate
   */
  public void setAuthenticationCertificate(final X509Certificate cert)
  {
    authenticationCert = cert;
  }


  /**
   * Returns the private key associated with the authentication certificate.
   *
   * @return  private key
   */
  public PrivateKey getAuthenticationKey()
  {
    return authenticationKey;
  }


  /**
   * Sets the private key associated with the authentication certificate.
   *
   * @param  key  private key
   */
  public void setAuthenticationKey(final PrivateKey key)
  {
    authenticationKey = key;
  }


  /** {@inheritDoc} */
  @Override
  public TrustManager[] getTrustManagers()
    throws GeneralSecurityException
  {
    TrustManager[] tm = null;
    if (trustCerts != null && trustCerts.length > 0) {
      final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      try {
        ks.load(null, null);
      } catch (IOException e) {
        throw new GeneralSecurityException(e);
      }
      for (int i = 0; i < trustCerts.length; i++) {
        ks.setCertificateEntry("ldap_trust_" + i, trustCerts[i]);
      }

      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(ks);
      tm = tmf.getTrustManagers();
    }
    return tm;
  }


  /** {@inheritDoc} */
  @Override
  public KeyManager[] getKeyManagers()
    throws GeneralSecurityException
  {
    KeyManager[] km = null;
    if (authenticationCert != null && authenticationKey != null) {
      final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      try {
        ks.load(null, null);
      } catch (IOException e) {
        throw new GeneralSecurityException(e);
      }
      ks.setKeyEntry(
        "ldap_client_auth",
        authenticationKey,
        "changeit".toCharArray(),
        new X509Certificate[] {authenticationCert});

      final KeyManagerFactory kmf = KeyManagerFactory.getInstance(
        KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ks, "changeit".toCharArray());
      km = kmf.getKeyManagers();
    }
    return km;
  }
}
