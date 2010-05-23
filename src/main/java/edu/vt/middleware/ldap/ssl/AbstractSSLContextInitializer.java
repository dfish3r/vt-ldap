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
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides common implementation for <code>SSLContextInitializer</code>.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public abstract class AbstractSSLContextInitializer
  implements SSLContextInitializer
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());


  /** {@inheritDoc} */
  public SSLContext initSSLContext(final String protocol)
    throws GeneralSecurityException
  {
    final SSLContext ctx = SSLContext.getInstance(protocol);
    ctx.init(this.getKeyManagers(), this.getTrustManagers(), null);
    return ctx;
  }


  /** {@inheritDoc} */
  public abstract TrustManager[] getTrustManagers()
    throws GeneralSecurityException;


  /** {@inheritDoc} */
  public abstract KeyManager[] getKeyManagers()
    throws GeneralSecurityException;
}
