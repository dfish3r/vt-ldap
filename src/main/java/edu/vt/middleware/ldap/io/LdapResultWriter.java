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
 * Interface for writing ldap results.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public interface LdapResultWriter
{


  /**
   * Writes the supplied ldap result.
   *
   * @param  result  ldap result to write
   * @throws  IOException  if an error occurs using the writer
   */
  void write(final LdapResult result) throws IOException;
}
