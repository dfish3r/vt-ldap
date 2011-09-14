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
import javax.net.SocketFactory;
import org.slf4j.LoggerFactory;

/**
 * TLSSocketFactory implementation that uses a static SSLContextInitializer.
 * Useful for SSL configurations that can only retrieve the SSLSocketFactory
 * from getDefault().
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SingletonTLSSocketFactory extends TLSSocketFactory
{

  /** SSLContextInitializer used for initializing SSL contexts. */
  private static SSLContextInitializer staticContextInitializer;


  /** {@inheritDoc} */
  @Override
  public void setSSLContextInitializer(final SSLContextInitializer initializer)
  {
    if (staticContextInitializer != null) {
      LoggerFactory.getLogger(SingletonTLSSocketFactory.class).warn(
        "SSLContextInitializer is being overridden");
    }
    staticContextInitializer = initializer;
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
    throws GeneralSecurityException
  {
    super.setSSLContextInitializer(staticContextInitializer);
    super.initialize();
  }


  /**
   * This returns the default SSL socket factory.
   *
   * @return  socket factory
   */
  public static SocketFactory getDefault()
  {
    final SingletonTLSSocketFactory sf = new SingletonTLSSocketFactory();
    try {
      sf.initialize();
    } catch (GeneralSecurityException e) {
      LoggerFactory.getLogger(SingletonTLSSocketFactory.class).error(
        "Error initializing socket factory", e);
    }
    return sf;
  }
}
