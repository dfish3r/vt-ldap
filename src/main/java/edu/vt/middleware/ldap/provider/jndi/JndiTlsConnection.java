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
package edu.vt.middleware.ldap.provider.jndi;

import java.io.IOException;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsResponse;
import edu.vt.middleware.ldap.LdapException;

/**
 * JNDI provider implementation of ldap operations over TLS.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiTlsConnection extends JndiConnection
{

  /** Start TLS response. */
  private StartTlsResponse startTlsResponse;

  /**
   * Whether to call {@link StartTlsResponse#close()} when {@link #close()} is
   * called.
   */
  private boolean stopTlsOnClose;


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
    final LdapContext lc,
    final StartTlsResponse tlsResponse)
  {
    super(lc);
    startTlsResponse = tlsResponse;
  }


  /**
   * Returns whether to call {@link StartTlsResponse#close()} when {@link
   * #close()} is called.
   *
   * @return  stop TLS on close
   */
  public boolean getStopTlsOnClose()
  {
    return stopTlsOnClose;
  }


  /**
   * Sets whether to call {@link StartTlsResponse#close()} when {@link #close()}
   * is called.
   *
   * @param  b  stop TLS on close
   */
  public void setStopTlsOnClose(final boolean b)
  {
    logger.trace("setting stopTlsOnClose: {}", b);
    stopTlsOnClose = b;
  }


  /**
   * Returns the start tls response used by this connection.
   *
   * @return  start tls response
   */
  public StartTlsResponse getStartTlsResponse()
  {
    return startTlsResponse;
  }


  /**
   * Sets the start tls response.
   *
   * @param  str  start tls response
   */
  public void setStartTlsResponse(final StartTlsResponse str)
  {
    startTlsResponse = str;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    try {
      if (stopTlsOnClose) {
        if (startTlsResponse != null) {
          startTlsResponse.close();
        }
      }
    } catch (IOException e) {
      logger.error("Error stopping TLS", e);
    } finally {
      startTlsResponse = null;
      super.close();
    }
  }
}
