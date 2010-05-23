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

/**
 * Provides the properties necessary for creating an SSL context initializer
 * with a <code>X509CredentialReader</code>.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class X509CredentialConfig implements CredentialConfig
{

  /** Reads X.509 certificates credential. */
  protected X509CertificatesCredentialReader certsReader =
    new X509CertificatesCredentialReader();

  /** Reads X.509 certificate credential. */
  protected X509CertificateCredentialReader certReader =
    new X509CertificateCredentialReader();

  /** Reads private key credential. */
  protected PrivateKeyCredentialReader keyReader =
    new PrivateKeyCredentialReader();

  /** Name of the trust certificates to use for the SSL connection. */
  private String trustCertificates;

  /** Name of the authentication certificate to use for the SSL connection. */
  private String authenticationCertificate;

  /** Name of the key to use for the SSL connection. */
  private String authenticationKey;


  /**
   * This returns the name of the trust certificates to use.
   *
   * @return  <code>String</code> trust certificates name
   */
  public String getTrustCertificates()
  {
    return this.trustCertificates;
  }


  /**
   * This sets the name of the trust certificates to use.
   *
   * @param  s  <code>String</code> trust certificates name
   */
  public void setTrustCertificates(final String s)
  {
    this.trustCertificates = s;
  }


  /**
   * This returns the name of the authentication certificate to use.
   *
   * @return  <code>String</code> authentication certificate name
   */
  public String getAuthenticationCertificate()
  {
    return this.authenticationCertificate;
  }


  /**
   * This sets the name of the authentication certificate to use.
   *
   * @param  s  <code>String</code> authentication certificate name
   */
  public void setAuthenticationCertificate(final String s)
  {
    this.authenticationCertificate = s;
  }


  /**
   * This returns the name of the authentication key to use.
   *
   * @return  <code>String</code> authentication key name
   */
  public String getAuthenticationKey()
  {
    return this.authenticationKey;
  }


  /**
   * This sets the name of the authentication key to use.
   *
   * @param  s  <code>String</code> authentication key name
   */
  public void setAuthenticationKey(final String s)
  {
    this.authenticationKey = s;
  }


  /** {@inheritDoc} */
  public SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException
  {
    final X509SSLContextInitializer sslInit = new X509SSLContextInitializer();
    try {
      if (this.trustCertificates != null) {
        sslInit.setTrustCertificates(
          this.certsReader.read(this.trustCertificates));
      }
      if (this.authenticationCertificate != null) {
        sslInit.setAuthenticationCertificate(
          this.certReader.read(this.authenticationCertificate));
      }
      if (this.authenticationKey != null) {
        sslInit.setAuthenticationKey(
          this.keyReader.read(this.authenticationKey));
      }
    } catch (IOException e) {
      throw new GeneralSecurityException(e);
    }
    return sslInit;
  }
}
