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
 * Provides a <code>SSLContextInitializer</code> which can use X509 certificates
 * to create key and trust managers.
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
   * @return  <code>X509Certificates[]</code>
   */
  public X509Certificate[] getTrustCertificates()
  {
    return this.trustCerts;
  }


  /**
   * Sets the certificates to use for creating the trust managers.
   *
   * @param  certs  <code>X509Certificates[]</code>
   */
  public void setTrustCertificates(final X509Certificate[] certs)
  {
    this.trustCerts = certs;
  }


  /**
   * Returns the certificate to use for creating the key managers.
   *
   * @return  <code>X509Certificate</code>
   */
  public X509Certificate getAuthenticationCertificate()
  {
    return this.authenticationCert;
  }


  /**
   * Sets the certificate to use for creating the key managers.
   *
   * @param  cert  <code>X509Certificate</code>
   */
  public void setAuthenticationCertificate(final X509Certificate cert)
  {
    this.authenticationCert = cert;
  }


  /**
   * Returns the private key associated with the authentication certificate.
   *
   * @return  <code>PrivateKey</code>
   */
  public PrivateKey getAuthenticationKey()
  {
    return this.authenticationKey;
  }


  /**
   * Sets the private key associated with the authentication certificate.
   *
   * @param  key  <code>PrivateKey</code>
   */
  public void setAuthenticationKey(final PrivateKey key)
  {
    this.authenticationKey = key;
  }


  /** {@inheritDoc} */
  public TrustManager[] getTrustManagers()
    throws GeneralSecurityException
  {
    TrustManager[] tm = null;
    if (this.trustCerts != null && this.trustCerts.length > 0) {
      final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      try {
        ks.load(null, null);
      } catch (IOException e) {
        throw new GeneralSecurityException(e);
      }
      for (int i = 0; i < this.trustCerts.length; i++) {
        ks.setCertificateEntry("ldap_trust_" + i, this.trustCerts[i]);
      }
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(ks);
      tm = tmf.getTrustManagers();
    }
    return tm;
  }


  /** {@inheritDoc} */
  public KeyManager[] getKeyManagers()
    throws GeneralSecurityException
  {
    KeyManager[] km = null;
    if (this.authenticationCert != null && this.authenticationKey != null) {
      final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
      try {
        ks.load(null, null);
      } catch (IOException e) {
        throw new GeneralSecurityException(e);
      }
      ks.setKeyEntry(
        "ldap_client_auth",
        this.authenticationKey,
        "changeit".toCharArray(),
        new X509Certificate[] {this.authenticationCert});
      final KeyManagerFactory kmf = KeyManagerFactory.getInstance(
        KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(ks, "changeit".toCharArray());
      km = kmf.getKeyManagers();
    }
    return km;
  }
}
