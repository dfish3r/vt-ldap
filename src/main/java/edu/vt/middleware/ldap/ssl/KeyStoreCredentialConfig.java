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

/**
 * Provides the properties necessary for creating an SSL context initializer
 * with a <code>KeyStoreCredentialReader</code>.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class KeyStoreCredentialConfig implements CredentialConfig
{
  /** Handles loading keystores */
  protected KeyStoreCredentialReader keyStoreReader =
    new KeyStoreCredentialReader();

  /** Name of the truststore to use for the SSL connection. */
  private String trustStore;

  /** Password needed to open the truststore. */
  private String trustStorePassword;

  /** Truststore type. */
  private String trustStoreType;

  /** Name of the keystore to use for the SSL connection. */
  private String keyStore;

  /** Password needed to open the keystore. */
  private String keyStorePassword;

  /** Keystore type. */
  private String keyStoreType;


  /**
   * This returns the name of the truststore to use.
   *
   * @return  <code>String</code> truststore name
   */
  public String getTrustStore()
  {
    return this.trustStore;
  }


  /**
   * This sets the name of the truststore to use.
   *
   * @param  s  <code>String</code> truststore name
   */
  public void setTrustStore(final String s)
  {
    this.trustStore = s;
  }


  /**
   * This returns the password for the truststore.
   *
   * @return  <code>String</code> truststore password
   */
  public String getTrustStorePassword()
  {
    return this.trustStorePassword;
  }


  /**
   * This sets the password for the truststore.
   *
   * @param  s  <code>String</code> truststore password
   */
  public void setTrustStorePassword(final String s)
  {
    this.trustStorePassword = s;
  }


  /**
   * This returns the type of the truststore.
   *
   * @return  <code>String</code> truststore type
   */
  public String getTrustStoreType()
  {
    return this.trustStoreType;
  }


  /**
   * This sets the type of the truststore.
   *
   * @param  s  <code>String</code> truststore type
   */
  public void setTrustStoreType(final String s)
  {
    this.trustStoreType = s;
  }


  /**
   * This returns the name of the keystore to use.
   *
   * @return  <code>String</code> keystore name
   */
  public String getKeyStore()
  {
    return this.keyStore;
  }


  /**
   * This sets the name of the keystore to use.
   *
   * @param  s  <code>String</code> keystore name
   */
  public void setKeyStore(final String s)
  {
    this.keyStore = s;
  }


  /**
   * This returns the password for the keystore.
   *
   * @return  <code>String</code> keystore password
   */
  public String getKeyStorePassword()
  {
    return this.keyStorePassword;
  }


  /**
   * This sets the password for the keystore.
   *
   * @param  s  <code>String</code> keystore password
   */
  public void setKeyStorePassword(final String s)
  {
    this.keyStorePassword = s;
  }


  /**
   * This returns the type of the keystore.
   *
   * @return  <code>String</code> keystore type
   */
  public String getKeyStoreType()
  {
    return this.keyStoreType;
  }


  /**
   * This sets the type of the keystore.
   *
   * @param  s  <code>String</code> keystore type
   */
  public void setKeyStoreType(final String s)
  {
    this.keyStoreType = s;
  }


  /** {@inheritDoc} */
  public SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException
  {
    final KeyStoreSSLContextInitializer sslInit =
      new KeyStoreSSLContextInitializer();
    try {
      if (this.trustStore != null) {
        sslInit.setTrustKeystore(
            this.keyStoreReader.read(
              this.trustStore, this.trustStorePassword, this.trustStoreType));
      }
      if (this.keyStore != null) {
        sslInit.setAuthenticationKeystore(
            this.keyStoreReader.read(
              this.keyStore, this.keyStorePassword, this.keyStoreType));
        sslInit.setAuthenticationPassword(
            this.keyStorePassword != null ?
              this.keyStorePassword.toCharArray() : null);
      }
    } catch (IOException e) {
      throw new GeneralSecurityException(e);
    }
    return sslInit;
  }
}
