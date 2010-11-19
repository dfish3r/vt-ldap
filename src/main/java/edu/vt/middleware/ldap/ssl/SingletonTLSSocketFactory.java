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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
  protected static SSLContextInitializer staticContextInitializer;


  /** {@inheritDoc} */
  public void setSSLContextInitializer(final SSLContextInitializer initializer)
  {
    staticContextInitializer = initializer;
  }


  /** {@inheritDoc} */
  public void initialize()
    throws GeneralSecurityException
  {
    super.setSSLContextInitializer(staticContextInitializer);
    super.initialize();
  }


  /**
   * This returns the default SSL socket factory.
   *
   * @return  <code>SocketFactory</code>
   */
  public static SocketFactory getDefault()
  {
    final SingletonTLSSocketFactory sf = new SingletonTLSSocketFactory();
    try {
      sf.initialize();
    } catch (GeneralSecurityException e) {
      final Log logger = LogFactory.getLog(TLSSocketFactory.class);
      if (logger.isErrorEnabled()) {
        logger.error("Error initializing socket factory", e);
      }
    }
    return sf;
  }
}
