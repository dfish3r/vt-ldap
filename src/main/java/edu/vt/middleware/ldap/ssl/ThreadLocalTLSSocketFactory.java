/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
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
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * TLSSocketFactory implementation that uses a thread local variable to store
 * configuration. Useful for SSL configurations that can only retrieve the
 * SSLSocketFactory from getDefault().
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ThreadLocalTLSSocketFactory extends TLSSocketFactory
{

  /** Thread local instance of the ssl config. */
  private static final ThreadLocalSslConfig THREAD_LOCAL_SSL_CONFIG =
    new ThreadLocalSslConfig();


  /** {@inheritDoc} */
  @Override
  public SSLContextInitializer getSSLContextInitializer()
  {
    return THREAD_LOCAL_SSL_CONFIG.get();
  }


  /** {@inheritDoc} */
  @Override
  public void setSSLContextInitializer(final SSLContextInitializer initializer)
  {
    THREAD_LOCAL_SSL_CONFIG.set(initializer);
  }


  /**
   * This returns the default SSL socket factory.
   *
   * @return  socket factory
   */
  public static SocketFactory getDefault()
  {
    final ThreadLocalTLSSocketFactory sf = new ThreadLocalTLSSocketFactory();
    if (sf.getSSLContextInitializer() == null) {
      throw new NullPointerException(
        "Thread local sslContextInitializer has not been set");
    }
    try {
      sf.initialize();
    } catch (GeneralSecurityException e) {
      throw new IllegalArgumentException(
        "Error initializing socket factory", e);
    }
    return sf;
  }


  /**
   * Returns an instance of this socket factory configured with a hostname
   * verifying trust manager.
   *
   * @param  names  to use for hostname verification
   *
   * @return  socket factory
   */
  public static SSLSocketFactory getHostnameVerifierFactory(
    final String[] names)
  {
    final ThreadLocalTLSSocketFactory sf = new ThreadLocalTLSSocketFactory();
    final DefaultSSLContextInitializer ctxInit =
      new DefaultSSLContextInitializer();
    try {
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm());
      tmf.init((KeyStore) null);
      final TrustManager[] tm = tmf.getTrustManagers();
      final X509TrustManager[] aggregate =
        new X509TrustManager[tm != null ? tm.length + 1 : 1];
      if (tm != null) {
        for (int i = 0; i < tm.length; i++) {
          aggregate[i] = (X509TrustManager) tm[i];
        }
      }
      aggregate[aggregate.length - 1] = new HostnameVerifyingTrustManager(
        new DefaultHostnameVerifier(), names);
      ctxInit.setTrustManagers(
        new TrustManager[] {new AggregateTrustManager(aggregate)});
      sf.setSSLContextInitializer(ctxInit);
      sf.initialize();
    } catch (GeneralSecurityException e) {
      throw new IllegalArgumentException(e);
    }
    return sf;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  String of the form $Classname::factory=$factory.
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "%s@%d::sslContextInitializer=%s,factory=%s," +
        "enabledCipherSuites=%s,enabledProtocols=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.getSSLContextInitializer(),
        this.getFactory(),
        this.getEnabledCipherSuites(),
        this.getEnabledProtocols());
  }


  /**
   * Thread local class for {@link SslConfig}.
   */
  private static class ThreadLocalSslConfig
    extends ThreadLocal<SSLContextInitializer> {}
}
