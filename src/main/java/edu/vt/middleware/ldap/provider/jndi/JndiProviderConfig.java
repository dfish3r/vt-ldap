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

import java.io.OutputStream;
import java.util.Arrays;
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
  protected OutputStream tracePackets;

  /** Whether to remove the URL from any DNs which are not relative. */
  protected boolean removeDnUrls = true;

  /** Search result codes to ignore. */
  protected ResultCode[] searchIgnoreResultCodes;

  /** ldap socket factory used for SSL and TLS. */
  protected SSLSocketFactory sslSocketFactory;

  /** hostname verifier for TLS connections. */
  protected HostnameVerifier hostnameVerifier;

  /** JNDI specific control handler. */
  protected JndiControlHandler controlHandler;


  /** Default constructor. */
  public JndiProviderConfig()
  {
    operationRetryResultCodes = new ResultCode[] {
      ResultCode.PROTOCOL_ERROR, ResultCode.BUSY, ResultCode.UNAVAILABLE, };
    searchIgnoreResultCodes = new ResultCode[] {
      ResultCode.TIME_LIMIT_EXCEEDED, ResultCode.SIZE_LIMIT_EXCEEDED, };
    controlHandler = new JndiControlHandler();
  }


  /**
   * Returns the output stream used to print ASN.1 BER packets.
   *
   * @return  output stream
   */
  public OutputStream getTracePackets()
  {
    return tracePackets;
  }


  /**
   * Sets the output stream to print ASN.1 BER packets to.
   *
   * @param  stream  to output to
   */
  public void setTracePackets(final OutputStream stream)
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
   * Returns the search ignore result codes.
   *
   * @return  result codes to ignore
   */
  public ResultCode[] getSearchIgnoreResultCodes()
  {
    return searchIgnoreResultCodes;
  }


  /**
   * Sets the search ignore result codes.
   *
   * @param  codes  to ignore
   */
  public void setSearchIgnoreResultCodes(final ResultCode[] codes)
  {
    logger.trace(
      "setting searchIgnoreResultCodes: {}",
      codes != null ? Arrays.asList(codes) : null);
    searchIgnoreResultCodes = codes;
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


  /**
   * Returns the control handler.
   *
   * @return  control handler
   */
  public JndiControlHandler getControlHandler()
  {
    return controlHandler;
  }


  /**
   * Sets the control handler.
   *
   * @param  handler  control handler
   */
  public void setControlHandler(final JndiControlHandler handler)
  {
    logger.trace("setting controlHandler: {}", handler);
    controlHandler = handler;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return String.format(
      "[%s@%d::operationRetryResultCodes=%s, properties=%s, " +
      "connectionStrategy=%s, logCredentials=%s, tracePackets=%s, " +
      "removeDnUrls=%s, searchIgnoreResultCodes=%s, sslSocketFactory=%s, " +
      "hostnameVerifier=%s, controlHandler=%s]",
      getClass().getName(),
      hashCode(),
      operationRetryResultCodes != null ?
        Arrays.asList(operationRetryResultCodes) : null,
      properties,
      connectionStrategy,
      logCredentials,
      tracePackets,
      removeDnUrls,
      searchIgnoreResultCodes != null ?
        Arrays.asList(searchIgnoreResultCodes) : null,
      sslSocketFactory,
      hostnameVerifier,
      controlHandler);
  }
}
