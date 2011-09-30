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
package edu.vt.middleware.ldap.io;

import java.io.IOException;
import edu.vt.middleware.ldap.LdapResult;

/**
 * Interface for reading ldap results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapResultReader
{


  /**
   * Reads an ldap result.
   *
   * @return  ldap result
   * @throws  IOException  if an error occurs using the reader
   */
  LdapResult read() throws IOException;
}
