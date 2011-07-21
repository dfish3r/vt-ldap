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
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.provider.AbstractProvider;
import edu.vt.middleware.ldap.provider.ProviderConnectionFactory;

/**
 * Exposes a connection factory for creating ldap connections with JNDI.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiProvider extends AbstractProvider
{

  /** Stream to print LDAP ASN.1 BER packets. */
  protected PrintStream tracePackets;

  /** Whether to remove the URL from any DNs which are not relative. */
  protected boolean removeDnUrls = true;


  /** Default constructor. */
  public JndiProvider()
  {
    operationRetryResultCodes = new ResultCode[] {
      ResultCode.PROTOCOL_ERROR, ResultCode.BUSY, ResultCode.UNAVAILABLE, };
  }


  /** {@inheritDoc} */
  @Override
  public ProviderConnectionFactory getConnectionFactory(
    final ConnectionConfig cc)
  {
    JndiProviderConnectionFactory cf = null;
    if (cc.isTlsEnabled()) {
      cf = new JndiTlsConnectionFactory(cc.getLdapUrl());
    } else {
      cf = new JndiConnectionFactory(cc.getLdapUrl());
    }
    cf.initialize(cc);
    cf.setLogCredentials(cc.getLogCredentials());
    cf.setSslSocketFactory(cc.getSslSocketFactory());
    cf.setHostnameVerifier(cc.getHostnameVerifier());
    if (cc.getConnectionStrategy() != null) {
      cf.setConnectionStrategy(cc.getConnectionStrategy());
    }
    cf.setOperationRetryResultCodes(operationRetryResultCodes);
    cf.setProperties(properties);
    cf.setTracePackets(tracePackets);
    cf.setRemoveDnUrls(removeDnUrls);
    return cf;
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
}
