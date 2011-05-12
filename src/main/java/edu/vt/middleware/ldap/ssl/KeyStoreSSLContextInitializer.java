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

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Provides a <code>SSLContextInitializer</code> which can use java KeyStores to
 * create key and trust managers.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class KeyStoreSSLContextInitializer extends AbstractSSLContextInitializer
{

  /** KeyStore used to create trust managers. */
  private KeyStore trustKeystore;

  /** KeyStore used to create key managers. */
  private KeyStore authenticationKeystore;

  /** Password used to access the authentication keystore. */
  private char[] authenticationPassword;


  /**
   * Sets the keystore to use for creating the trust managers.
   *
   * @param  ks  <code>KeyStore</code>
   */
  public void setTrustKeystore(final KeyStore ks)
  {
    trustKeystore = ks;
  }


  /**
   * Sets the keystore to use for creating the key managers.
   *
   * @param  ks  <code>KeyStore</code>
   */
  public void setAuthenticationKeystore(final KeyStore ks)
  {
    authenticationKeystore = ks;
  }


  /**
   * Sets the password used for accessing the authentication keystore.
   *
   * @param  password  <code>char[]</code>
   */
  public void setAuthenticationPassword(final char[] password)
  {
    authenticationPassword = password;
  }


  /** {@inheritDoc} */
  @Override
  public TrustManager[] getTrustManagers()
    throws GeneralSecurityException
  {
    TrustManager[] tm = null;
    if (trustKeystore != null) {
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(trustKeystore);
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
    if (
      authenticationKeystore != null &&
        authenticationPassword != null) {
      final KeyManagerFactory kmf = KeyManagerFactory.getInstance(
        KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(authenticationKeystore, authenticationPassword);
      km = kmf.getKeyManagers();
    }
    return km;
  }
}
