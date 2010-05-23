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
import javax.net.ssl.SSLContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>TLSSocketFactory</code> is an extension of SSLSocketFactory. Note that
 * {@link #initialize()} must be called prior to using this socket factory. This
 * means that this class cannot be passed to implementations that expect the
 * socket factory to function immediately after construction.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public class TLSSocketFactory extends AbstractTLSSocketFactory
{

  /** SSLContextInitializer used for initializing SSL contexts. */
  protected SSLContextInitializer contextInitializer =
    new DefaultSSLContextInitializer();


  /**
   * Returns the SSL context initializer.
   *
   * @return  <code>SSLContextInitializer</code>
   */
  public SSLContextInitializer getSSLContextInitializer()
  {
    return this.contextInitializer;
  }


  /**
   * Sets the SSL context initializer.
   *
   * @param  initializer  to create SSL contexts with
   */
  public void setSSLContextInitializer(final SSLContextInitializer initializer)
  {
    this.contextInitializer = initializer;
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
    final SSLContext ctx = this.contextInitializer.initSSLContext(
      DEFAULT_PROTOCOL);
    this.factory = ctx.getSocketFactory();
  }


  /**
   * This returns the default SSL socket factory.
   *
   * @return  <code>SocketFactory</code>
   */
  public static SocketFactory getDefault()
  {
    final TLSSocketFactory sf = new TLSSocketFactory();
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
