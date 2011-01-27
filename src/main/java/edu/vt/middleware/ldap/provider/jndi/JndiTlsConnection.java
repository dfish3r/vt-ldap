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
package edu.vt.middleware.ldap.provider.jndi;

import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsResponse;

/**
 * JNDI provider implementation of ldap operations over TLS.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiTlsConnection extends JndiConnection
{
  /** Start TLS response. */
  protected StartTlsResponse startTlsResponse;


  /**
   * Creates a new jndi tls connection.
   *
   * @param  lc  ldap context
   */
  public JndiTlsConnection(final LdapContext lc)
  {
    super(lc);
  }


  /**
   * Creates a new jndi tls connection.
   *
   * @param  lc  ldap contxt
   * @param  tlsResponse  of successful TLS handshake
   */
  public JndiTlsConnection(
    final LdapContext lc, final StartTlsResponse tlsResponse)
  {
    super(lc);
    this.startTlsResponse = tlsResponse;
  }


  /**
   * Returns the start tls response used by this connection.
   *
   * @return  start tls response
   */
  public StartTlsResponse getStartTlsResponse()
  {
    return this.startTlsResponse;
  }


  /**
   * Sets the start tls response.
   *
   * @param  str  start tls response
   */
  public void setStartTlsResponse(final StartTlsResponse str)
  {
    this.startTlsResponse = str;
  }


  /** {@inheritDoc} */
  public void clear()
  {
    this.startTlsResponse = null;
    super.clear();
  }
}
