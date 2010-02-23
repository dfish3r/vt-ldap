/*
  $Id: LdapTLSSocketFactory.java 1106 2010-01-30 04:34:13Z dfisher $

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1106 $
  Updated: $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
*/
package edu.vt.middleware.ldap.ssl;

import java.security.GeneralSecurityException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Provides an interface for the initialization of new SSL contexts.

 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public interface SSLContextInitializer
{


  /**
   * Creates an initialized SSLContext for the supplied protocol.
   *
   * @param  protocol  type to use for SSL
   * @return  <code>SSLContext</code>
   * @throws  GeneralSecurityException  if the SSLContext cannot be created
   */
  SSLContext initSSLContext(String protocol)
    throws GeneralSecurityException;


  /**
   * Returns the trust managers used when creating SSL contexts.
   *
   * @return  <code>TrustManager[]</code>
   * @throws  GeneralSecurityException  if an errors occurs while loading the
   * TrustManagers
   */
  TrustManager[] getTrustManagers() throws GeneralSecurityException;


  /**
   * Returns the key managers used when creating SSL contexts.
   *
   * @return  <code>KeyManagers[]</code>
   * @throws  GeneralSecurityException  if an errors occurs while loading the
   * KeyManagers
   */
  KeyManager[] getKeyManagers() throws GeneralSecurityException;
}
