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
import edu.vt.middleware.ldap.CompareOperation;
import edu.vt.middleware.ldap.CompareRequest;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapAttribute;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.auth.AuthenticationException;

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
   * @param  lcc  ldap connection config
   */
  public CompareAuthenticationHandler(final ConnectionConfig lcc)
  {
    setConnectionConfig(lcc);
  }


  /**
   * Returns the password scheme.
   *
   * @return  password scheme
   */
  public String getPasswordScheme()
  {
    return passwordScheme;
  }


  /**
   * Sets the password scheme. Must equal a known message digest algorithm.
   *
   * @param  s  password scheme
   */
  public void setPasswordScheme(final String s)
  {
    passwordScheme = s;
  }


  /** {@inheritDoc} */
  @Override
  public Connection authenticate(final AuthenticationCriteria ac)
    throws LdapException
  {
    byte[] hash = new byte[DIGEST_SIZE];
    try {
      final MessageDigest md = MessageDigest.getInstance(passwordScheme);
      md.update(ac.getCredential().getBytes());
      hash = md.digest();
    } catch (NoSuchAlgorithmException e) {
      throw new LdapException(e);
    }

    final Connection conn = new Connection(config);
    conn.open();
    final LdapAttribute la = new LdapAttribute(
      "userPassword",
      String.format(
        "{%s}%s", passwordScheme, LdapUtil.base64Encode(hash)).getBytes());
    final CompareOperation compare = new CompareOperation(conn);
    final boolean success = compare.execute(
      new CompareRequest(ac.getDn(), la)).getResult();

    if (!success) {
      if (conn != null) {
        conn.close();
      }
      throw new AuthenticationException("Compare authentication failed.");
    } else {
      return conn;
    }
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
        "[%s@%d::passwordScheme=%s]",
        getClass().getName(),
        hashCode(),
        passwordScheme);
  }
}
