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

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for reading X509 certificates.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public abstract class AbstractX509PathTypeReader
  extends AbstractPathTypeReader
{

  /** Default trust certificates name, value is {@value}. */
  public static final String DEFAULT_TRUSTCERTS = "/vt-ldap.trust.crt";

  /** Default authentication certificate name, value is {@value}. */
  public static final String DEFAULT_AUTHCERT = "/vt-ldap.auth.crt";

  /** Default authentication key name, value is {@value}. */
  public static final String DEFAULT_AUTHKEY = "/vt-ldap.auth.key";

  /** Name of the trust certificates to use for the SSL connection. */
  private String trustCertificates = DEFAULT_TRUSTCERTS;

  /** Trust certificates path type. */
  private PathType trustCertificatesPathType = PathType.CLASSPATH;

  /** Name of the authentication certificate to use for the SSL connection. */
  private String authenticationCertificate = DEFAULT_AUTHCERT;

  /** Authentication certificate path type. */
  private PathType authenticationCertificatePathType = PathType.CLASSPATH;

  /** Name of the key to use for the SSL connection. */
  private String authenticationKey = DEFAULT_AUTHKEY;

  /** Authentication key path type. */
  private PathType authenticationKeyPathType = PathType.CLASSPATH;


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
    if (s != null && s.startsWith(FILE_URI_PREFIX)) {
      this.setTrustCertificatesPathType(PathType.FILEPATH);
    }
    this.trustCertificates = s;
  }


  /**
   * This gets the path type of the trust certificates.
   *
   * @return  <code>PathType</code> trust certificates path type
   */
  public PathType getTrustCertificatesPathType()
  {
    return this.trustCertificatesPathType;
  }


  /**
   * This sets the path type of the trust certificates.
   *
   * @param  pt  <code>PathType</code> trust certificates path type
   */
  public void setTrustCertificatesPathType(final PathType pt)
  {
    this.trustCertificatesPathType = pt;
  }


  /**
   * This returns the trust certificates as an <code>InputStream</code>. If the
   * trust certificates could not be loaded this method returns null.
   *
   * @return  <code>InputStream</code> trust certificate
   */
  protected InputStream getTrustCertificateStream()
  {
    return this.getInputStream(
      this.trustCertificates, this.trustCertificatesPathType);
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
    if (s != null && s.startsWith(FILE_URI_PREFIX)) {
      this.setAuthenticationCertificatePathType(PathType.FILEPATH);
    }
    this.authenticationCertificate = s;
  }


  /**
   * This gets the path type of the authentication certificate.
   *
   * @return  <code>PathType</code> authentication certificate path type
   */
  public PathType getAuthenticationCertificatePathType()
  {
    return this.authenticationCertificatePathType;
  }


  /**
   * This sets the path type of the authentication certificate.
   *
   * @param  pt  <code>PathType</code> authentication certificate path type
   */
  public void setAuthenticationCertificatePathType(final PathType pt)
  {
    this.authenticationCertificatePathType = pt;
  }


  /**
   * This returns the authentication certificate as an <code>InputStream</code>.
   * If the authentication certificate could not be loaded this method returns
   * null.
   *
   * @return  <code>InputStream</code> authentication certificate
   */
  protected InputStream getAuthenticationCertificateStream()
  {
    return this.getInputStream(
      this.authenticationCertificate, this.authenticationCertificatePathType);
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
    if (s != null && s.startsWith(FILE_URI_PREFIX)) {
      this.setAuthenticationKeyPathType(PathType.FILEPATH);
    }
    this.authenticationKey = s;
  }


  /**
   * This gets the path type of the authentication key.
   *
   * @return  <code>PathType</code> authentication key path type
   */
  public PathType getAuthenticationKeyPathType()
  {
    return this.authenticationKeyPathType;
  }


  /**
   * This sets the path type of the authentication key.
   *
   * @param  pt  <code>PathType</code> authentication key path type
   */
  public void setAuthenticationKeyPathType(final PathType pt)
  {
    this.authenticationKeyPathType = pt;
  }


  /**
   * This returns the authentication key as an <code>InputStream</code>.
   * If the authentication key could not be loaded this method returns
   * null.
   *
   * @return  <code>InputStream</code> authentication key
   */
  protected InputStream getAuthenticationKeyStream()
  {
    return this.getInputStream(
      this.authenticationKey, this.authenticationKeyPathType);
  }


  /**
   * Reads the trust certificates and generates X509 certificates.
   *
   * @return  <code>X509Certificate[]</code>
   * @throws  GeneralSecurityException  if certificates cannot be generated
   */
  public X509Certificate[] readTrustCertificates()
    throws GeneralSecurityException
  {
    X509Certificate[] trustedCerts = null;
    final InputStream is = this.getTrustCertificateStream();
    if (is != null) {
      final List<X509Certificate> certList = new ArrayList<X509Certificate>();
      for (Certificate c : this.loadCertificateChain(is)) {
        certList.add((X509Certificate) c);
      }
      trustedCerts = certList.toArray(new X509Certificate[0]);
    }
    return trustedCerts;
  }


  /**
   * Reads the authentication certificate and generates an X509 certificate.
   *
   * @return  <code>X509Certificate</code>
   * @throws  GeneralSecurityException  if a certificate cannot be generated
   */
  public X509Certificate readAuthenticationCertificate()
    throws GeneralSecurityException
  {
    X509Certificate authCert = null;
    final InputStream is = this.getAuthenticationCertificateStream();
    if (is != null) {
      authCert = (X509Certificate) this.loadCertificate(is);
    }
    return authCert;
  }


  /**
   * Reads the authentication key and generates a private key.
   *
   * @return  <code>PrivateKey</code>
   * @throws  GeneralSecurityException  if a private key cannot be generated
   */
  public PrivateKey readAuthenticationKey()
    throws GeneralSecurityException
  {
    PrivateKey authKey = null;
    final InputStream is = this.getAuthenticationKeyStream();
    if (is != null) {
      authKey = this.loadPrivateKey(is);
    }
    return authKey;
  }


  /** {@inheritDoc} */
  public abstract SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException;


  /**
   * Reads a chain of certificates from the supplied input stream.
   *
   * @param  is  <code>InputStream</code> containing certificates
   * @return  <code>Certificate[]</code>
   * @throws  GeneralSecurityException  if an error occurs creating the
   * certificate chain
   */
  protected abstract Certificate[] loadCertificateChain(final InputStream is)
    throws GeneralSecurityException;


  /**
   * Reads a certificate from the supplied input stream.
   *
   * @param  is  <code>InputStream</code> containing a certificate
   * @return  <code>Certificate</code>
   * @throws  GeneralSecurityException  if an error occurs creating the
   * certificate
   */
  protected abstract Certificate loadCertificate(final InputStream is)
    throws GeneralSecurityException;


  /**
   * Reads a private key from the supplied input stream.
   *
   * @param  is  <code>InputStream</code> containing a private key
   * @return  <code>PrivateKey</code>
   * @throws  GeneralSecurityException  if an error occurs creating the
   * private key
   */
  protected abstract PrivateKey loadPrivateKey(final InputStream is)
    throws GeneralSecurityException;
}
