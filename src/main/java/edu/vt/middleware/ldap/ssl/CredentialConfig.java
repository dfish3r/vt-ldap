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

/**
 * <code>CredentialConfig</code> provides a base interface for all
 * credential configurations. Since credential configs are invoked via
 * reflection by the PropertyInvoker their method signatures are not important.
 * They only need to be able to create an SSL context initializer once their
 * properties have been set.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public interface CredentialConfig
{


  /**
   * Creates an <code>SSLContextInitializer</code> using the configured trust
   * and authentication material in this config.
   *
   * @return  <code>SSLContextInitializer</code>
   * @throws  GeneralSecurityException  if the ssl context initializer cannot be
   * created
   */
  SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException;
}
