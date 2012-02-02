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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Trust manager that delegates to multiple trust managers.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AggregateTrustManager implements X509TrustManager
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Trust managers to invoke. */
  private X509TrustManager[] trustManagers;


  /**
   * Creates a new aggregate trust manager.
   *
   * @param managers  to aggregate
   */
  public AggregateTrustManager(final X509TrustManager... managers)
  {
    trustManagers = managers;
  }


  /** {@inheritDoc} */
  public void checkClientTrusted(
    final X509Certificate[] chain, final String authType)
    throws CertificateException
  {
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("invoking checkClientTrusted for " + tm);
        }
        tm.checkClientTrusted(chain, authType);
      }
    }
  }


  /** {@inheritDoc} */
  public void checkServerTrusted(
    final X509Certificate[] chain, final String authType)
    throws CertificateException
  {
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("invoking checkServerTrusted for " + tm);
        }
        tm.checkServerTrusted(chain, authType);
      }
    }
  }


  /** {@inheritDoc} */
  public X509Certificate[] getAcceptedIssuers()
  {
    final List<X509Certificate> issuers = new ArrayList<X509Certificate>();
    if (trustManagers != null) {
      for (X509TrustManager tm : trustManagers) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("invoking getAcceptedIssuers invoked for " + tm);
        }
        for (X509Certificate cert : tm.getAcceptedIssuers()) {
          issuers.add(cert);
        }
      }
    }
    return issuers.toArray(new X509Certificate[issuers.size()]);
  }
}
