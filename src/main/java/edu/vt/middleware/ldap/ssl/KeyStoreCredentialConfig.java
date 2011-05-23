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
 * with a keystore credential reader.
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
   * Returns the name of the truststore to use.
   *
   * @return  truststore name
   */
  public String getTrustStore()
  {
    return trustStore;
  }


  /**
   * Sets the name of the truststore to use.
   *
   * @param  s  truststore name
   */
  public void setTrustStore(final String s)
  {
    trustStore = s;
  }


  /**
   * Returns the password for the truststore.
   *
   * @return  truststore password
   */
  public String getTrustStorePassword()
  {
    return trustStorePassword;
  }


  /**
   * Sets the password for the truststore.
   *
   * @param  s  truststore password
   */
  public void setTrustStorePassword(final String s)
  {
    trustStorePassword = s;
  }


  /**
   * Returns the type of the truststore.
   *
   * @return  truststore type
   */
  public String getTrustStoreType()
  {
    return trustStoreType;
  }


  /**
   * Sets the type of the truststore.
   *
   * @param  s  truststore type
   */
  public void setTrustStoreType(final String s)
  {
    trustStoreType = s;
  }


  /**
   * Returns the name of the keystore to use.
   *
   * @return  keystore name
   */
  public String getKeyStore()
  {
    return keyStore;
  }


  /**
   * Sets the name of the keystore to use.
   *
   * @param  s  keystore name
   */
  public void setKeyStore(final String s)
  {
    keyStore = s;
  }


  /**
   * Returns the password for the keystore.
   *
   * @return  keystore password
   */
  public String getKeyStorePassword()
  {
    return keyStorePassword;
  }


  /**
   * Sets the password for the keystore.
   *
   * @param  s  keystore password
   */
  public void setKeyStorePassword(final String s)
  {
    keyStorePassword = s;
  }


  /**
   * Returns the type of the keystore.
   *
   * @return  keystore type
   */
  public String getKeyStoreType()
  {
    return keyStoreType;
  }


  /**
   * Sets the type of the keystore.
   *
   * @param  s  keystore type
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
