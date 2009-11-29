/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
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
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import edu.vt.middleware.ldap.bean.LdapAttributes;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
import edu.vt.middleware.ldap.ldif.Ldif;
import org.testng.annotations.DataProvider;

/**
 * Common methods for ldap tests.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public final class TestUtil
{

  /** Location of the hostname in the output of netstat. */
  public static final int NETSTAT_HOST_INDEX = 4;


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "setup-ldap")
  public static Ldap createSetupLdap()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.setup.properties"));
    return l;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "ldap")
  public static Ldap createLdap()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties();
    return l;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "sasl-external-ldap")
  public static Ldap createSaslExternalLdap()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.sasl.properties"));

    final LdapTLSSocketFactory sf = new LdapTLSSocketFactory();
    sf.setTrustStoreName("/ed.truststore");
    sf.setTrustStoreType("BKS");
    sf.setKeyStoreName("/ed.keystore");
    sf.setKeyStoreType("BKS");
    sf.initialize();
    l.getLdapConfig().setSslSocketFactory(sf);
    return l;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "gss-api-ldap")
  public static Ldap createGssApiLdap()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.gssapi.properties"));
    return l;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "ssl-auth")
  public static Authenticator createSSLAuthenticator()
    throws Exception
  {
    final Authenticator a = new Authenticator();
    a.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.ssl.properties"));
    return a;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "ssl-dn-auth")
  public static Authenticator createSSLDnAuthenticator()
    throws Exception
  {
    final Authenticator a = new Authenticator();
    a.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.ssl.properties"));
    a.getAuthenticatorConfig().setDnResolver(new NoopDnResolver());
    return a;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "tls-auth")
  public static Authenticator createTLSAuthenticator()
    throws Exception
  {
    final Authenticator a = new Authenticator();
    a.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.tls.properties"));

    final LdapTLSSocketFactory sf = new LdapTLSSocketFactory();
    sf.setTrustStoreName("/ed.truststore");
    sf.setTrustStoreType("BKS");
    sf.initialize();
    a.getAuthenticatorConfig().setSslSocketFactory(sf);
    return a;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "tls-dn-auth")
  public static Authenticator createTLSDnAuthenticator()
    throws Exception
  {
    final Authenticator a = new Authenticator();
    a.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.tls.properties"));
    a.getAuthenticatorConfig().setDnResolver(new NoopDnResolver());

    final LdapTLSSocketFactory sf = new LdapTLSSocketFactory();
    sf.setTrustStoreName("/ed.truststore");
    sf.setTrustStoreType("BKS");
    sf.initialize();
    a.getAuthenticatorConfig().setSslSocketFactory(sf);
    return a;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "digest-md5-auth")
  public static Authenticator createDigestMD5Authenticator()
    throws Exception
  {
    final Authenticator a = new Authenticator();
    a.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.digest-md5.properties"));
    a.getAuthenticatorConfig().setDnResolver(new NoopDnResolver());
    return a;
  }


  /**
   * @return  Test configuration.
   *
   * @throws  Exception  On test data generation failure.
   */
  @DataProvider(name = "cram-md5-auth")
  public static Authenticator createCramMD5Authenticator()
    throws Exception
  {
    final Authenticator a = new Authenticator();
    a.loadFromProperties(
      TestUtil.class.getResourceAsStream("/ldap.cram-md5.properties"));
    a.getAuthenticatorConfig().setDnResolver(new NoopDnResolver());
    return a;
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
   * Converts a ldif to a <code>LdapResult</code>.
   *
   * @param  ldif  to convert.
   *
   * @return  LdapResult.
   *
   * @throws  Exception  if ldif cannot be read
   */
  public static LdapResult convertLdifToResult(final String ldif)
    throws Exception
  {
    return new LdapResult((new Ldif()).importLdif(new StringReader(ldif)));
  }


  /**
   * Converts a ldif to a <code>LdapEntry</code>.
   *
   * @param  ldif  to convert.
   *
   * @return  LdapEntry.
   *
   * @throws  Exception  if ldif cannot be read
   */
  public static LdapEntry convertLdifToEntry(final String ldif)
    throws Exception
  {
    return
      new LdapEntry((new Ldif()).importLdif(new StringReader(ldif)).next());
  }


  /**
   * Converts a string of the form: givenName=John|sn=Doe into a ldap attributes
   * object.
   *
   * @param  attrs  to convert.
   *
   * @return  LdapAttributes.
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
