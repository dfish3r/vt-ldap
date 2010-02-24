/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.ssl;

import java.security.GeneralSecurityException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

/**
 * Provides a default implementation of <code>SSLContextInitializer</code>
 * which allows the setting of trust and key managers in order to create an
 * SSL context.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultSSLContextInitializer extends AbstractSSLContextInitializer
{

  /** Trust managers. */
  private TrustManager[] trustManagers;

  /** Key managers. */
  private KeyManager[] keyManagers;


  /** {@inheritDoc} */
  public TrustManager[] getTrustManagers()
    throws GeneralSecurityException
  {
    return this.trustManagers;
  }


  /**
   * Sets the trust managers.
   *
   * @param  tm  <code>TrustManager[]</code>
   */
  public void setTrustManagers(final TrustManager[] tm)
  {
    this.trustManagers = tm;
  }


  /** {@inheritDoc} */
  public KeyManager[] getKeyManagers()
    throws GeneralSecurityException
  {
    return this.keyManagers;
  }


  /**
   * Sets the key managers.
   *
   * @param  km  <code>KeyManager[]</code>
   */
  public void setKeyManagers(final KeyManager[] km)
  {
    this.keyManagers = km;
  }
}
