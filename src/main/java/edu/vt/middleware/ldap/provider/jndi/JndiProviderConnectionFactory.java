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
import java.util.Hashtable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.provider.ProviderConnectionFactory;

/**
 * Provides an interface for creating JNDI specific provider connections.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public interface JndiProviderConnectionFactory extends ProviderConnectionFactory
{


  /**
   * Prepares this connection factory for use by inspecting the connection
   * configuration properties.
   *
   * @param  cc  connection config
   */
  void initialize(ConnectionConfig cc);


  /**
   * Returns the ldap context environment properties that are used to make LDAP
   * connections.
   *
   * @return  context environment
   */
  Hashtable<String, Object> getEnvironment();


  /**
   * Sets the ldap context environment properties that are used to make LDAP
   * connections.
   *
   * @param  env  context environment
   */
  void setEnvironment(Hashtable<String, Object> env);


  /**
   * Returns the print stream used to print ASN.1 BER packets.
   *
   * @return  print stream
   */
  PrintStream getTracePackets();


  /**
   * Sets the print stream to print ASN.1 BER packets to.
   *
   * @param  stream  to print to
   */
  void setTracePackets(PrintStream stream);


  /**
   * Returns whether the URL will be removed from any DNs which are not
   * relative
   *
   * @return  whether the URL will be removed from DNs
   */
  boolean getRemoveDnUrls();


  /**
   * Sets whether the URL will be removed from any DNs which are not relative
   *
   * @param  b  whether the URL will be removed from DNs
   */
  void setRemoveDnUrls(boolean b);


  /**
   * Returns the SSL socket factory to use for TLS/SSL connections.
   *
   * @return  SSL socket factory
   */
  SSLSocketFactory getSslSocketFactory();


  /**
   * Sets the SSL socket factory to use for TLS/SSL connections.
   *
   * @param  factory  SSL socket factory
   */
  void setSslSocketFactory(SSLSocketFactory factory);


  /**
   * Returns the hostname verifier to use for TLS connections.
   *
   * @return  hostname verifier
   */
  HostnameVerifier getHostnameVerifier();


  /**
   * Sets the hostname verifier to use for TLS connections.
   *
   * @param  verifier  for hostnames
   */
  void setHostnameVerifier(HostnameVerifier verifier);
}
