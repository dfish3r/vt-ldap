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
 * <code>PathTypeReader</code> provides a base interface for all path type
 * readers.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public interface PathTypeReader
{

  /** Types of paths. */
  public enum PathType {

    /** File path location. */
    FILEPATH,

    /** Classpath location. */
    CLASSPATH
  }


  /**
   * Creates an <code>SSLContextInitializer</code> using the configured trust
   * and authentication material in this reader.
   *
   * @return  <code>SSLContextInitializer</code>
   * @throws  GeneralSecurityException  if the ssl context initializer cannot be
   * created
   */
  SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException;
}
