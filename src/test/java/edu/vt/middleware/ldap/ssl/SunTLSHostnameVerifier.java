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
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import sun.security.util.HostnameChecker;

/**
 * A {@link HostnameVerifier} that delegates to the internal Sun implementation
 * at sun.security.util.HostnameChecker. This is the implementation used by
 * JNDI with StartTLS.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SunTLSHostnameVerifier implements HostnameVerifier
{


  /** {@inheritDoc} */
  public boolean verify(final String hostname, final SSLSession session)
  {
    boolean b = false;
    try {
      b = verify(hostname, (X509Certificate) session.getPeerCertificates()[0]);
    } catch (SSLPeerUnverifiedException e) {
      b = false;
    }
    return b;
  }


  /**
   * Expose convenience method for testing.
   *
   * @param  hostname  to verify
   * @param  cert  to verify hostname against
   *
   * @return  whether the certificate is allowed
   */
  public boolean verify(final String hostname, final X509Certificate cert)
  {
    boolean b = false;
    final HostnameChecker checker = HostnameChecker.getInstance(
      HostnameChecker.TYPE_LDAP);
    try {
      checker.match(hostname, cert);
      b = true;
    } catch (CertificateException e) {
      b = false;
    }
    return b;
  }
}
