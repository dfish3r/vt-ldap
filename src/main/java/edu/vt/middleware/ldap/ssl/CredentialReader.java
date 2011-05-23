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

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Reads a credential from an IO source.
 *
 * @param  <T>  Type of credential read by this instance.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public interface CredentialReader<T>
{


  /**
   * Reads a credential object from a path.
   *
   * @param  path  from which to read credential.
   * @param  params  Arbitrary string parameters, e.g. password, needed to read
   * the credential.
   *
   * @return  credential read from data at path.
   *
   * @throws  IOException  On IO errors.
   * @throws  GeneralSecurityException  On errors with the credential data.
   */
  T read(String path, String... params)
    throws IOException, GeneralSecurityException;


  /**
   * Reads a credential object from an input stream.
   *
   * @param  is  input stream from which to read credential.
   * @param  params  Arbitrary string parameters, e.g. password, needed to read
   * the credential.
   *
   * @return  credential read from data in stream.
   *
   * @throws  IOException  On IO errors.
   * @throws  GeneralSecurityException  On errors with the credential data.
   */
  T read(InputStream is, String... params)
    throws IOException, GeneralSecurityException;
}
