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
package edu.vt.middleware.ldap.auth.handler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import edu.vt.middleware.ldap.CompareRequest;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.auth.AuthenticationException;
import edu.vt.middleware.ldap.auth.AuthenticatorConfig;
import edu.vt.middleware.ldap.provider.Connection;
import edu.vt.middleware.ldap.provider.ConnectionFactory;

/**
 * Provides an LDAP authentication implementation that leverages a compare
 * operation against the userPassword attribute. The default password scheme
 * used is 'SHA'.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class CompareAuthenticationHandler extends AbstractAuthenticationHandler
{

  /** Maximum digest size. Value is {@value}. */
  private static final int DIGEST_SIZE = 256;

  /** Password scheme. Default value is {@value}. */
  private String passwordScheme = "SHA";


  /** Default constructor. */
  public CompareAuthenticationHandler() {}


  /**
   * Creates a new compare authentication handler.
   *
   * @param  ac  authenticator config
   */
  public CompareAuthenticationHandler(final AuthenticatorConfig ac)
  {
    this.setAuthenticatorConfig(ac);
  }


  /**
   * Returns the password scheme.
   *
   * @return  password scheme
   */
  public String getPasswordScheme()
  {
    return this.passwordScheme;
  }


  /**
   * Sets the password scheme. Must equal a known message digest algorithm.
   *
   * @param  s  password scheme
   */
  public void setPasswordScheme(final String s)
  {
    this.passwordScheme = s;
  }


  /** {@inheritDoc} */
  public Connection authenticate(
    final ConnectionFactory ch,
    final AuthenticationCriteria ac)
    throws LdapException
  {
    byte[] hash = new byte[DIGEST_SIZE];
    try {
      final MessageDigest md = MessageDigest.getInstance(this.passwordScheme);
      md.update(ac.getCredential().getBytes());
      hash = md.digest();
    } catch (NoSuchAlgorithmException e) {
      throw new LdapException(e);
    }

    final Connection conn = ch.create(
      this.config.getBindDn(), this.config.getBindCredential());
    final LdapAttribute la = new LdapAttribute(
      "userPassword",
      String.format(
        "{%s}%s", this.passwordScheme, LdapUtil.base64Encode(hash)).getBytes());
    final boolean success = conn.compare(new CompareRequest(ac.getDn(), la));

    if (!success) {
      ch.destroy(conn);
      throw new AuthenticationException("Compare authentication failed.");
    } else {
      return conn;
    }
  }


  /** {@inheritDoc} */
  public CompareAuthenticationHandler newInstance()
  {
    return new CompareAuthenticationHandler(this.config);
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "%s@%d: passwordScheme=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.passwordScheme);
  }
}
