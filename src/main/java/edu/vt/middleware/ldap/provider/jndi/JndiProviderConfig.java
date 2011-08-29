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

import java.io.PrintStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.provider.ProviderConfig;

/**
 * Contains configuration data for the JNDI provider.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JndiProviderConfig extends ProviderConfig
{

  /** Stream to print LDAP ASN.1 BER packets. */
  protected PrintStream tracePackets;

  /** Whether to remove the URL from any DNs which are not relative. */
  protected boolean removeDnUrls = true;

  /** ldap socket factory used for SSL and TLS. */
  protected SSLSocketFactory sslSocketFactory;

  /** hostname verifier for TLS connections. */
  protected HostnameVerifier hostnameVerifier;


  /** Default constructor. */
  public JndiProviderConfig()
  {
    operationRetryResultCodes = new ResultCode[] {
      ResultCode.PROTOCOL_ERROR, ResultCode.BUSY, ResultCode.UNAVAILABLE, };
  }


  /**
   * Returns the print stream used to print ASN.1 BER packets.
   *
   * @return  print stream
   */
  public PrintStream getTracePackets()
  {
    return tracePackets;
  }


  /**
   * Sets the print stream to print ASN.1 BER packets to.
   *
   * @param  stream  to print to
   */
  public void setTracePackets(final PrintStream stream)
  {
    logger.trace("setting tracePackets: {}", stream);
    tracePackets = stream;
  }


  /**
   * Returns whether the URL will be removed from any DNs which are not
   * relative. The default value is true.
   *
   * @return  whether the URL will be removed from DNs
   */
  public boolean getRemoveDnUrls()
  {
    return removeDnUrls;
  }


  /**
   * Sets whether the URL will be removed from any DNs which are not relative
   * The default value is true.
   *
   * @param  b  whether the URL will be removed from DNs
   */
  public void setRemoveDnUrls(final boolean b)
  {
    logger.trace("setting removeDnUrls: {}", b);
    removeDnUrls = b;
  }


  /**
   * Returns the SSL socket factory to use for TLS/SSL connections.
   *
   * @return  SSL socket factory
   */
  public SSLSocketFactory getSslSocketFactory()
  {
    return sslSocketFactory;
  }


  /**
   * Sets the SSL socket factory to use for TLS/SSL connections.
   *
   * @param  sf  SSL socket factory
   */
  public void setSslSocketFactory(final SSLSocketFactory sf)
  {
    logger.trace("setting sslSocketFactory: {}", sf);
    sslSocketFactory = sf;
  }


  /**
   * Returns the hostname verifier to use for TLS connections.
   *
   * @return  hostname verifier
   */
  public HostnameVerifier getHostnameVerifier()
  {
    return hostnameVerifier;
  }


  /**
   * Sets the hostname verifier to use for TLS connections.
   *
   * @param  verifier  for hostnames
   */
  public void setHostnameVerifier(final HostnameVerifier verifier)
  {
    logger.trace("setting hostnameVerifier: {}", verifier);
    hostnameVerifier = verifier;
  }
}
