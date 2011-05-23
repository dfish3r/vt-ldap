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
 * with an X.509 credential reader.
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
   * Returns the name of the trust certificates to use.
   *
   * @return  trust certificates name
   */
  public String getTrustCertificates()
  {
    return trustCertificates;
  }


  /**
   * Sets the name of the trust certificates to use.
   *
   * @param  s  trust certificates name
   */
  public void setTrustCertificates(final String s)
  {
    trustCertificates = s;
  }


  /**
   * Returns the name of the authentication certificate to use.
   *
   * @return  authentication certificate name
   */
  public String getAuthenticationCertificate()
  {
    return authenticationCertificate;
  }


  /**
   * Sets the name of the authentication certificate to use.
   *
   * @param  s  authentication certificate name
   */
  public void setAuthenticationCertificate(final String s)
  {
    authenticationCertificate = s;
  }


  /**
   * Returns the name of the authentication key to use.
   *
   * @return  authentication key name
   */
  public String getAuthenticationKey()
  {
    return authenticationKey;
  }


  /**
   * Sets the name of the authentication key to use.
   *
   * @param  s  authentication key name
   */
  public void setAuthenticationKey(final String s)
  {
    authenticationKey = s;
  }


  /** {@inheritDoc} */
  @Override
  public SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException
  {
    final X509SSLContextInitializer sslInit = new X509SSLContextInitializer();
    try {
      if (trustCertificates != null) {
        sslInit.setTrustCertificates(
          certsReader.read(trustCertificates));
      }
      if (authenticationCertificate != null) {
        sslInit.setAuthenticationCertificate(
          certReader.read(authenticationCertificate));
      }
      if (authenticationKey != null) {
        sslInit.setAuthenticationKey(
          keyReader.read(authenticationKey));
      }
    } catch (IOException e) {
      throw new GeneralSecurityException(e);
    }
    return sslInit;
  }
}
