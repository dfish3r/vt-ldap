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
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

/**
 * Provides a default implementation of SSL context initializer which allows the
 * setting of trust and key managers in order to create an SSL context.
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
  @Override
  public TrustManager[] getTrustManagers()
    throws GeneralSecurityException
  {
    return trustManagers;
  }


  /**
   * Sets the trust managers.
   *
   * @param  tm  trust managers
   */
  public void setTrustManagers(final TrustManager[] tm)
  {
    trustManagers = tm;
  }


  /** {@inheritDoc} */
  @Override
  public KeyManager[] getKeyManagers()
    throws GeneralSecurityException
  {
    return keyManagers;
  }


  /**
   * Sets the key managers.
   *
   * @param  km  key managers
   */
  public void setKeyManagers(final KeyManager[] km)
  {
    keyManagers = km;
  }
}
