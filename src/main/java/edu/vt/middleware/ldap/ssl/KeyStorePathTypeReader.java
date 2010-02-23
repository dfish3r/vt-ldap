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
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * <code>KeyStorePathTypeReader</code> provides functionality for reading
 * keystores from classpaths or filepaths.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class KeyStorePathTypeReader extends AbstractPathTypeReader
{

  /** Default truststore name, value is {@value}. */
  public static final String DEFAULT_TRUSTSTORE = "/vt-ldap.truststore";

  /** Default truststore password, value is {@value}. */
  public static final String DEFAULT_TRUSTSTORE_PASSWORD = "changeit";

  /** Default truststore type, value is {@value}. */
  public static final String DEFAULT_TRUSTSTORE_TYPE = "JKS";

  /** Default keystore name, value is {@value}. */
  public static final String DEFAULT_KEYSTORE = "/vt-ldap.keystore";

  /** Default keystore password, value is {@value}. */
  public static final String DEFAULT_KEYSTORE_PASSWORD = "changeit";

  /** Default keystore type, value is {@value}. */
  public static final String DEFAULT_KEYSTORE_TYPE = "JKS";

  /** Name of the truststore to use for the SSL connection. */
  private String trustStore = DEFAULT_TRUSTSTORE;

  /** Password needed to open the truststore. */
  private String trustStorePassword = DEFAULT_TRUSTSTORE_PASSWORD;

  /** Truststore path type. */
  private PathType trustStorePathType = PathType.CLASSPATH;

  /** Truststore type. */
  private String trustStoreType = DEFAULT_TRUSTSTORE_TYPE;

  /** Name of the keystore to use for the SSL connection. */
  private String keyStore = DEFAULT_KEYSTORE;

  /** Password needed to open the keystore. */
  private String keyStorePassword = DEFAULT_KEYSTORE_PASSWORD;

  /** Keystore path type. */
  private PathType keyStorePathType = PathType.CLASSPATH;

  /** Keystore type. */
  private String keyStoreType = DEFAULT_KEYSTORE_TYPE;


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
    if (s != null && s.startsWith(FILE_URI_PREFIX)) {
      this.setTrustStorePathType(PathType.FILEPATH);
    }
    this.trustStore = s;
  }


  /**
   * This gets the path type of the truststore.
   *
   * @return  <code>PathType</code> truststore path type
   */
  public PathType getTrustStorePathType()
  {
    return this.trustStorePathType;
  }


  /**
   * This sets the path type of the truststore.
   *
   * @param  pt  <code>PathType</code> truststore path type
   */
  public void setTrustStorePathType(final PathType pt)
  {
    this.trustStorePathType = pt;
  }


  /**
   * This returns the truststore as an <code>InputStream</code>. If the
   * truststore could not be loaded this method returns null.
   *
   * @return  <code>InputStream</code> truststore
   */
  protected InputStream getTrustStoreStream()
  {
    return this.getInputStream(this.trustStore, this.trustStorePathType);
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
    if (s != null && s.startsWith(FILE_URI_PREFIX)) {
      this.setKeyStorePathType(PathType.FILEPATH);
    }
    this.keyStore = s;
  }


  /**
   * This gets the path type of the keystore.
   *
   * @return  <code>PathType</code> keystore path type
   */
  public PathType getKeyStorePathType()
  {
    return this.keyStorePathType;
  }


  /**
   * This sets the path type of the keystore.
   *
   * @param  pt  <code>PathType</code> keystore path type
   */
  public void setKeyStorePathType(final PathType pt)
  {
    this.keyStorePathType = pt;
  }


  /**
   * This returns the keystore as an <code>InputStream</code>. If the keystore
   * could not be loaded this method returns null.
   *
   * @return  <code>InputStream</code> keystore
   */
  protected InputStream getKeyStoreStream()
  {
    return this.getInputStream(this.keyStore, this.keyStorePathType);
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


  /**
   * Reads the configured truststore from it's path.
   *
   * @return  <code>KeyStore</code>
   * @throws  GeneralSecurityException  if the truststore cannot be read
   */
  public KeyStore readTrustStore()
    throws GeneralSecurityException
  {
    try {
      return this.loadKeyStore(
        this.getTrustStoreStream(),
        this.getTrustStorePassword(),
        this.getTrustStoreType());
    } catch (IOException e) {
      throw new GeneralSecurityException(e);
    }
  }


  /**
   * Reads the configured keystore from it's path.
   *
   * @return  <code>KeyStore</code>
   * @throws  GeneralSecurityException  if the keystore cannot be read
   */
  public KeyStore readKeyStore()
    throws GeneralSecurityException
  {
    try {
      return this.loadKeyStore(
        this.getKeyStoreStream(),
        this.getKeyStorePassword(),
        this.getKeyStoreType());
    } catch (IOException e) {
      throw new GeneralSecurityException(e);
    }
  }


  /** {@inheritDoc} */
  public SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException
  {
    final KeyStoreSSLContextInitializer sslInit =
      new KeyStoreSSLContextInitializer();
    sslInit.setTrustKeystore(this.readTrustStore());
    sslInit.setAuthenticationKeystore(this.readKeyStore());
    sslInit.setAuthenticationPassword(this.getKeyStorePassword().toCharArray());
    return sslInit;
  }


  /**
   * This attempts to load a keystore from the supplied <code>InputStream</code>
   * using the supplied password.
   *
   * @param  is  <code>InputStream</code> containing the keystore
   * @param  password  <code>String</code> to unlock the keystore
   * @param  storeType  <code>String</code> of keystore
   *
   * @return  <code>KeyStore</code>
   *
   * @throws  IOException  if the keystore cannot be loaded
   * @throws  GeneralSecurityException  if an errors occurs while loading the
   * KeyManagers
   */
  protected KeyStore loadKeyStore(
    final InputStream is,
    final String password,
    final String storeType)
    throws IOException, GeneralSecurityException
  {
    KeyStore keystore = null;
    if (is != null) {
      String type = storeType;
      if (type == null) {
        type = KeyStore.getDefaultType();
      }
      keystore = KeyStore.getInstance(type);

      char[] pw = null;
      if (password != null) {
        pw = password.toCharArray();
      }
      keystore.load(is, pw);
    }
    return keystore;
  }
}
