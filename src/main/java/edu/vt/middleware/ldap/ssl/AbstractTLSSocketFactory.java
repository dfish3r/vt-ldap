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
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocketFactory;

/**
 * Provides common implementation for <code>TLSSocketFactory</code>.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public abstract class AbstractTLSSocketFactory extends SSLSocketFactory
{

  /** Default SSL protocol, value is {@value}. */
  public static final String DEFAULT_PROTOCOL = "TLS";

  /** SSLSocketFactory used for creating SSL sockets. */
  protected SSLSocketFactory factory;


  /**
   * Prepares this socket factory for use. Must be called before factory can be
   * used.
   *
   * @throws  GeneralSecurityException  if the factory cannot be initialized
   */
  public abstract void initialize()
    throws GeneralSecurityException;


  /**
   * This returns the underlying <code>SSLSocketFactory</code> that this class
   * uses for creating SSL Sockets.
   *
   * @return  <code>SSLSocketFactory</code>
   */
  public SSLSocketFactory getFactory()
  {
    return this.factory;
  }


  /**
   * This returns a socket layered over an existing socket connected to the
   * named host, at the given port.
   *
   * @param  s  <code>Socket</code> existing socket
   * @param  host  <code>String</code> server hostname
   * @param  port  <code>int</code> server port
   * @param  autoClose  <code>boolean</code> close the underlying socket when
   * this socket is closed
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final Socket s,
    final String host,
    final int port,
    final boolean autoClose)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(s, host, port, autoClose);
    }
    return socket;
  }


  /**
   * This creates a socket and connects it to the specified port number at the
   * specified addres.
   *
   * @param  host  <code>InetAddress</code> server hostname
   * @param  port  <code>int</code> server port
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(final InetAddress host, final int port)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(host, port);
    }
    return socket;
  }


  /**
   * This creates a socket and connect it to the specified port number at the
   * specified address. The socket will also be bound to the supplied local
   * address and port.
   *
   * @param  address  <code>InetAddress</code> server hostname
   * @param  port  <code>int</code> server port
   * @param  localAddress  <code>InetAddress</code> client hostname
   * @param  localPort  <code>int</code> client port
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final InetAddress address,
    final int port,
    final InetAddress localAddress,
    final int localPort)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(
        address,
        port,
        localAddress,
        localPort);
    }
    return socket;
  }


  /**
   * This creates a socket and connects it to the specified port number at the
   * specified addres.
   *
   * @param  host  <code>String</code> server hostname
   * @param  port  <code>int</code> server port
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(final String host, final int port)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(host, port);
    }
    return socket;
  }


  /**
   * This creates a socket and connect it to the specified port number at the
   * specified address. The socket will also be bound to the supplied local
   * address and port.
   *
   * @param  host  <code>String</code> server hostname
   * @param  port  <code>int</code> server port
   * @param  localHost  <code>InetAddress</code> client hostname
   * @param  localPort  <code>int</code> client port
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final String host,
    final int port,
    final InetAddress localHost,
    final int localPort)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(host, port, localHost, localPort);
    }
    return socket;
  }


  /**
   * This returns the list of cipher suites which are enabled by default.
   *
   * @return  <code>String[]</code> - array of the cipher suites
   */
  public String[] getDefaultCipherSuites()
  {
    String[] ciphers = null;
    if (this.factory != null) {
      ciphers = this.factory.getDefaultCipherSuites();
    }
    return ciphers;
  }


  /**
   * This returns the names of the cipher suites which could be enabled for use
   * on an SSL connection.
   *
   * @return  <code>String[]</code> - array of the cipher suites
   */
  public String[] getSupportedCipherSuites()
  {
    String[] ciphers = null;
    if (this.factory != null) {
      ciphers = this.factory.getSupportedCipherSuites();
    }
    return ciphers;
  }
}
