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
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import org.slf4j.LoggerFactory;

/**
 * An extension of SSLSocketFactory that leverages an SSL context initializer.
 * Note that {@link #initialize()} must be called prior to using this socket
 * factory. This means that this class cannot be passed to implementations that
 * expect the socket factory to function immediately after construction.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class TLSSocketFactory extends AbstractTLSSocketFactory
{

  /** SSLContextInitializer used for initializing SSL contexts. */
  private SSLContextInitializer contextInitializer =
    new DefaultSSLContextInitializer();


  /**
   * Returns the SSL context initializer.
   *
   * @return  SSL context initializer
   */
  public SSLContextInitializer getSSLContextInitializer()
  {
    return contextInitializer;
  }


  /**
   * Sets the SSL context initializer.
   *
   * @param  initializer  to create SSL contexts with
   */
  public void setSSLContextInitializer(final SSLContextInitializer initializer)
  {
    contextInitializer = initializer;
  }


  /**
   * Creates the underlying SSLContext using truststore and keystore attributes
   * and makes this factory ready for use. Must be called before factory can be
   * used.
   *
   * @throws  GeneralSecurityException  if the SSLContext cannot be created
   */
  public void initialize()
    throws GeneralSecurityException
  {
    final SSLContext ctx = contextInitializer.initSSLContext(DEFAULT_PROTOCOL);
    factory = ctx.getSocketFactory();
  }


  /**
   * Returns the default SSL socket factory.
   *
   * @return  socket factory
   */
  public static SocketFactory getDefault()
  {
    final TLSSocketFactory sf = new TLSSocketFactory();
    try {
      sf.initialize();
    } catch (GeneralSecurityException e) {
      LoggerFactory.getLogger(TLSSocketFactory.class).error(
        "Error initializing socket factory",
        e);
    }
    return sf;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::sslContextInitializer=%s, factory=%s, " +
        "enabledCipherSuites=%s, enabledProtocols=%s]",
        getClass().getName(),
        hashCode(),
        getSSLContextInitializer(),
        getFactory(),
        getEnabledCipherSuites(),
        getEnabledProtocols());
  }
}
