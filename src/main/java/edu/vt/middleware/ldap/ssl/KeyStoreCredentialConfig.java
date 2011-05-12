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
 * with a <code>KeyStoreCredentialReader</code>.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class KeyStoreCredentialConfig implements CredentialConfig
{

  /** Handles loading keystores. */
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
    return trustStore;
  }


  /**
   * This sets the name of the truststore to use.
   *
   * @param  s  <code>String</code> truststore name
   */
  public void setTrustStore(final String s)
  {
    trustStore = s;
  }


  /**
   * This returns the password for the truststore.
   *
   * @return  <code>String</code> truststore password
   */
  public String getTrustStorePassword()
  {
    return trustStorePassword;
  }


  /**
   * This sets the password for the truststore.
   *
   * @param  s  <code>String</code> truststore password
   */
  public void setTrustStorePassword(final String s)
  {
    trustStorePassword = s;
  }


  /**
   * This returns the type of the truststore.
   *
   * @return  <code>String</code> truststore type
   */
  public String getTrustStoreType()
  {
    return trustStoreType;
  }


  /**
   * This sets the type of the truststore.
   *
   * @param  s  <code>String</code> truststore type
   */
  public void setTrustStoreType(final String s)
  {
    trustStoreType = s;
  }


  /**
   * This returns the name of the keystore to use.
   *
   * @return  <code>String</code> keystore name
   */
  public String getKeyStore()
  {
    return keyStore;
  }


  /**
   * This sets the name of the keystore to use.
   *
   * @param  s  <code>String</code> keystore name
   */
  public void setKeyStore(final String s)
  {
    keyStore = s;
  }


  /**
   * This returns the password for the keystore.
   *
   * @return  <code>String</code> keystore password
   */
  public String getKeyStorePassword()
  {
    return keyStorePassword;
  }


  /**
   * This sets the password for the keystore.
   *
   * @param  s  <code>String</code> keystore password
   */
  public void setKeyStorePassword(final String s)
  {
    keyStorePassword = s;
  }


  /**
   * This returns the type of the keystore.
   *
   * @return  <code>String</code> keystore type
   */
  public String getKeyStoreType()
  {
    return keyStoreType;
  }


  /**
   * This sets the type of the keystore.
   *
   * @param  s  <code>String</code> keystore type
   */
  public void setKeyStoreType(final String s)
  {
    keyStoreType = s;
  }


  /** {@inheritDoc} */
  @Override
  public SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException
  {
    final KeyStoreSSLContextInitializer sslInit =
      new KeyStoreSSLContextInitializer();
    try {
      if (trustStore != null) {
        sslInit.setTrustKeystore(
          keyStoreReader.read(
            trustStore,
            trustStorePassword,
            trustStoreType));
      }
      if (keyStore != null) {
        sslInit.setAuthenticationKeystore(
          keyStoreReader.read(
            keyStore,
            keyStorePassword,
            keyStoreType));
        sslInit.setAuthenticationPassword(
          keyStorePassword != null ? keyStorePassword.toCharArray()
                                        : null);
      }
    } catch (IOException e) {
      throw new GeneralSecurityException(e);
    }
    return sslInit;
  }
}
