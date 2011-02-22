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
package edu.vt.middleware.ldap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.AuthenticatorConfig;
import edu.vt.middleware.ldap.auth.NoopDnResolver;
import edu.vt.middleware.ldap.ldif.Ldif;
import edu.vt.middleware.ldap.props.AuthenticatorConfigProperties;
import edu.vt.middleware.ldap.props.LdapConnectionConfigProperties;
import org.testng.annotations.DataProvider;

/**
 * Utility methods for ldap tests.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public final class TestUtil
{

  /** Location of the hostname in the output of netstat. */
  public static final int NETSTAT_HOST_INDEX = 4;


  public static LdapConnectionConfig readLdapConnectionConfig(
    final InputStream is)
  {
    if (is != null) {
      final LdapConnectionConfigProperties lccReader =
        new LdapConnectionConfigProperties(is);
      return lccReader.get();
    } else {
      final LdapConnectionConfigProperties lccReader = 
        new LdapConnectionConfigProperties();
      return lccReader.get();
    }
  }


  public static AuthenticatorConfig readAuthenticatorConfig(
    final InputStream is)
  {
    if (is != null) {
      final AuthenticatorConfigProperties acReader =
        new AuthenticatorConfigProperties(is);
      return acReader.get();
    } else {
      final AuthenticatorConfigProperties acReader = 
        new AuthenticatorConfigProperties();
      return acReader.get();
    }
  }


  /**
   * @return  ldap connection
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "setup-ldap")
  public static LdapConnection createSetupLdapConnection()
    throws Exception
  {
    return new LdapConnection(
      readLdapConnectionConfig(
        TestUtil.class.getResourceAsStream("/ldap.setup.properties")));
  }


  /**
   * @return  ldap connection
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "ldap")
  public static LdapConnection createLdapConnection()
    throws Exception
  {
    return new LdapConnection(readLdapConnectionConfig(null));
  }


  /**
   * @return  ldap connection
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "sasl-external-ldap")
  public static LdapConnection createSaslExternalLdapConnection()
    throws Exception
  {
    return new LdapConnection(
      readLdapConnectionConfig(
        TestUtil.class.getResourceAsStream("/ldap.external.properties")));
  }


  /**
   * @return  ldap connection
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "gss-api-ldap")
  public static LdapConnection createGssApiLdapConnection()
    throws Exception
  {
    return new LdapConnection(
      readLdapConnectionConfig(
        TestUtil.class.getResourceAsStream("/ldap.gssapi.properties")));
  }


  /**
   * @return  authenticator
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "ssl-auth")
  public static Authenticator createSSLAuthenticator()
    throws Exception
  {
    return new Authenticator(
      readAuthenticatorConfig(
        TestUtil.class.getResourceAsStream("/ldap.ssl.properties")));
  }


  /**
   * @return  authenticator
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "ssl-dn-auth")
  public static Authenticator createSSLDnAuthenticator()
    throws Exception
  {
    final Authenticator auth = new Authenticator(
      readAuthenticatorConfig(
        TestUtil.class.getResourceAsStream("/ldap.ssl.properties")));
    auth.getAuthenticatorConfig().setDnResolver(new NoopDnResolver());
    return auth;
  }


  /**
   * @return  authenticator
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "tls-auth")
  public static Authenticator createTLSAuthenticator()
    throws Exception
  {
    return new Authenticator(
      readAuthenticatorConfig(
        TestUtil.class.getResourceAsStream("/ldap.tls.properties")));
  }


  /**
   * @return  authenticator
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "tls-dn-auth")
  public static Authenticator createTLSDnAuthenticator()
    throws Exception
  {
    final Authenticator auth = new Authenticator(
      readAuthenticatorConfig(
        TestUtil.class.getResourceAsStream("/ldap.tls.properties")));
    auth.getAuthenticatorConfig().setDnResolver(new NoopDnResolver());
    return auth;
  }


  /**
   * @return  authenticator
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "digest-md5-auth")
  public static Authenticator createDigestMD5Authenticator()
    throws Exception
  {
    final Authenticator auth = new Authenticator(
      readAuthenticatorConfig(
        TestUtil.class.getResourceAsStream("/ldap.digest-md5.properties")));
    auth.getAuthenticatorConfig().setDnResolver(new NoopDnResolver());
    return auth;
  }


  /**
   * @return  authenticator
   *
   * @throws  Exception  On test failure.
   */
  @DataProvider(name = "cram-md5-auth")
  public static Authenticator createCramMD5Authenticator()
    throws Exception
  {
    final Authenticator auth = new Authenticator(
      readAuthenticatorConfig(
        TestUtil.class.getResourceAsStream("/ldap.cram-md5.properties")));
    auth.getAuthenticatorConfig().setDnResolver(new NoopDnResolver());
    return auth;
  }


  /**
   * Reads a file on the classpath into a reader.
   *
   * @param  filename  to open.
   *
   * @return  reader.
   *
   * @throws  Exception  If file cannot be read.
   */
  public static BufferedReader readFile(final String filename)
    throws Exception
  {
    return
      new BufferedReader(
        new InputStreamReader(TestUtil.class.getResourceAsStream(filename)));
  }


  /**
   * Reads a file on the classpath into a string.
   *
   * @param  filename  to open.
   *
   * @return  string.
   *
   * @throws  Exception  If file cannot be read.
   */
  public static String readFileIntoString(final String filename)
    throws Exception
  {
    final StringBuffer result = new StringBuffer();
    final BufferedReader br = readFile(filename);
    try {
      String line;
      while ((line = br.readLine()) != null) {
        result.append(line).append(System.getProperty("line.separator"));
      }
    } finally {
      br.close();
    }
    return result.toString();
  }


  /**
   * Converts an ldif to a ldap result.
   *
   * @param  ldif  to convert.
   *
   * @return  ldap result.
   *
   * @throws  Exception  if ldif cannot be read
   */
  public static LdapResult convertLdifToResult(final String ldif)
    throws Exception
  {
    return (new Ldif()).importLdif(new StringReader(ldif));
  }


  /**
   * Converts a string of the form: givenName=John|sn=Doe into a ldap attributes
   * object.
   *
   * @param  attrs  to convert.
   *
   * @return  ldap attributes.
   */
  public static LdapAttributes convertStringToAttributes(final String attrs)
  {
    final LdapAttributes la = new LdapAttributes();
    final String[] s = attrs.split("\\|");
    for (int i = 0; i < s.length; i++) {
      final String[] nameValuePairs = s[i].trim().split("=", 2);
      if (la.getAttribute(nameValuePairs[0]) != null) {
        la.getAttribute(nameValuePairs[0]).getValues().add(nameValuePairs[1]);
      } else {
        la.addAttribute(nameValuePairs[0], nameValuePairs[1]);
      }
    }
    return la;
  }


  /**
   * Returns the number of open connections to the supplied host. Uses 'netstat
   * -al' to uncover open sockets.
   *
   * @param  host  host to look for.
   *
   * @return  number of open connections.
   *
   * @throws  IOException  if the process cannot be run
   */
  public static int countOpenConnections(final String host)
    throws IOException
  {
    final String[] cmd = new String[] {"netstat", "-al"};
    final Process p = new ProcessBuilder(cmd).start();
    final BufferedReader br = new BufferedReader(
      new InputStreamReader(p.getInputStream()));
    String line;
    final List<String> openConns = new ArrayList<String>();
    while ((line = br.readLine()) != null) {
      if (line.matches(".*ESTABLISHED$")) {
        final String s = line.split("\\s+")[NETSTAT_HOST_INDEX];
        openConns.add(s.substring(0, s.lastIndexOf(".")));
      }
    }

    int count = 0;
    for (String o : openConns) {
      if (o.contains(host)) {
        count++;
      }
    }
    return count;
  }
}
