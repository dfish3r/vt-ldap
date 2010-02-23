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
import java.util.Iterator;
import java.util.List;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.auth.Authenticator;
import edu.vt.middleware.ldap.auth.NoopDnResolver;
import edu.vt.middleware.ldap.bean.LdapAttributes;
import edu.vt.middleware.ldap.bean.LdapBeanProvider;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
import edu.vt.middleware.ldap.ldif.Ldif;
import edu.vt.middleware.ldap.ssl.KeyStorePathTypeReader;
import edu.vt.middleware.ldap.ssl.TLSSocketFactory;
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

    final KeyStorePathTypeReader reader = new KeyStorePathTypeReader();
    reader.setTrustStore("/ed.truststore");
    reader.setTrustStoreType("BKS");
    reader.setKeyStore("/ed.keystore");
    reader.setKeyStoreType("BKS");
    final TLSSocketFactory sf = new TLSSocketFactory();
    sf.setSSLContextInitializer(reader.createSSLContextInitializer());
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

    final KeyStorePathTypeReader reader = new KeyStorePathTypeReader();
    reader.setTrustStore("/ed.truststore");
    reader.setTrustStoreType("BKS");
    final TLSSocketFactory sf = new TLSSocketFactory();
    sf.setSSLContextInitializer(reader.createSSLContextInitializer());
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

    final KeyStorePathTypeReader reader = new KeyStorePathTypeReader();
    reader.setTrustStore("/ed.truststore");
    reader.setTrustStoreType("BKS");
    final TLSSocketFactory sf = new TLSSocketFactory();
    sf.setSSLContextInitializer(reader.createSSLContextInitializer());
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
   * Creates a new <code>LdapResult</code> with the supplied
   * <code>Iterator</code> of search results.
   *
   * @param  iter  <code>Iterator</code> of search results
   * @return  <code>LdapResult</code>
   * @throws  Exception  if search results cannot be read
   */
  public static LdapResult newLdapResult(final Iterator<SearchResult> iter)
    throws Exception
  {
    final LdapResult lr =
      LdapBeanProvider.getLdapBeanFactory().newLdapResult();
    lr.addEntries(iter);
    return lr;
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
    return (new Ldif()).importLdifToLdapResult(new StringReader(ldif));
  }


  /**
   * Creates a new <code>LdapEntry</code> with the supplied
   * <code>SearchResult</code>.
   *
   * @param  sr  <code>SearchResult</code>
   * @return  <code>LdapEntry</code>
   * @throws  Exception  if search result cannot be read
   */
  public static LdapEntry newLdapEntry(final SearchResult sr)
    throws Exception
  {
    final LdapEntry le =
      LdapBeanProvider.getLdapBeanFactory().newLdapEntry();
    le.setEntry(sr);
    return le;
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
    return (new Ldif()).importLdifToLdapResult(
      new StringReader(ldif)).getEntries().iterator().next();
  }


  /**
   * Creates a new <code>LdapAttributes</code> with the supplied
   * <code>Attributes</code>.
   *
   * @param  attrs  <code>Attributes</code>
   * @return  <code>LdapAttributes</code>
   * @throws  Exception  if attributes cannot be read
   */
  public static LdapAttributes newLdapAttributes(final Attributes attrs)
    throws Exception
  {
    final LdapAttributes la =
      LdapBeanProvider.getLdapBeanFactory().newLdapAttributes();
    la.addAttributes(attrs);
    return la;
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
    final LdapAttributes la =
      LdapBeanProvider.getLdapBeanFactory().newLdapAttributes();
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
