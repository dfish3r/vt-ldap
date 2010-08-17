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

import java.util.Arrays;
import javax.net.ssl.SSLSocket;
import edu.vt.middleware.ldap.AnyHostnameVerifier;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for {@link TLSSocketFactory}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class TLSSocketFactoryTest
{

  /** List of ciphers. */
  public static final String[] CIPHERS = new String[] {
    "TLS_DH_anon_WITH_AES_128_CBC_SHA",
    "TLS_DH_anon_WITH_AES_256_CBC_SHA",
    "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",
    "SSL_DH_anon_WITH_RC4_128_MD5",
    "TLS_RSA_WITH_AES_128_CBC_SHA",
    "TLS_RSA_WITH_AES_256_CBC_SHA",
    "SSL_RSA_WITH_3DES_EDE_CBC_SHA",
    "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
    "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
    "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
    "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
    "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
    "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
    "SSL_RSA_WITH_RC4_128_MD5",
    "SSL_RSA_WITH_RC4_128_SHA",
  };

  /** List of ciphers. */
  public static final String[] UNKNOWN_CIPHERS = new String[] {
    "TLS_DH_anon_WITH_AES_128_CBC_SHA",
    "TLS_DH_anon_WITH_3DES_256_CBC_SHA",
    "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",
    "SSL_DH_anon_WITH_RC4_128_MD5",
  };

  /** List of protocols. */
  public static final String[] ALL_PROTOCOLS = new String[] {
    "SSLv2Hello",
    "SSLv3",
    "TLSv1",
  };

  /** List of protocols. */
  public static final String[] PROTOCOLS = new String[] {
    "SSLv3",
    "TLSv1",
  };

  /** List of protocols. */
  public static final String[] FAIL_PROTOCOLS = new String[] {
    "SSLv2Hello",
  };

  /** List of protocols. */
  public static final String[] UNKNOWN_PROTOCOLS = new String[] {
    "SSLv2Hello",
    "SSLv3Hello",
    "TLSv1",
  };


  /**
   * @return  <code>Ldap</code>
   *
   * @throws  Exception  On ldap construction failure.
   */
  public Ldap createTLSLdap()
    throws Exception
  {
    // configure TLSSocketFactory
    final X509CertificatesCredentialReader reader =
      new X509CertificatesCredentialReader();
    final X509SSLContextInitializer ctxInit =
      new X509SSLContextInitializer();
    ctxInit.setTrustCertificates(
      reader.read("file:src/test/resources/ed.trust.crt"));
    final TLSSocketFactory sf = new TLSSocketFactory();
    sf.setSSLContextInitializer(ctxInit);
    sf.initialize();

    // configure ldap object to use TLS
    final Ldap ldap = TestUtil.createLdap();
    ldap.getLdapConfig().setTls(true);
    ldap.getLdapConfig().setSslSocketFactory(sf);
    ldap.getLdapConfig().setHostnameVerifier(new AnyHostnameVerifier());
    return ldap;
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ssltest"})
  public void setEnabledCipherSuites()
    throws Exception
  {
    final Ldap ldap = this.createTLSLdap();
    final TLSSocketFactory sf =
      (TLSSocketFactory) ldap.getLdapConfig().getSslSocketFactory();

    AssertJUnit.assertTrue(ldap.connect());
    ldap.getSchema("ou=test,dc=vt,dc=edu");
    AssertJUnit.assertEquals(
      Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledCipherSuites()),
      Arrays.asList(sf.getDefaultCipherSuites()));
    AssertJUnit.assertNotSame(
      Arrays.asList(sf.getDefaultCipherSuites()), Arrays.asList(CIPHERS));
    ldap.close();

    sf.setEnabledCipherSuites(UNKNOWN_CIPHERS);
    try {
      ldap.connect();
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, no exception thrown");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e);
    }
    ldap.close();

    sf.setEnabledCipherSuites(CIPHERS);
    AssertJUnit.assertTrue(ldap.connect());
    ldap.getSchema("ou=test,dc=vt,dc=edu");
    AssertJUnit.assertEquals(
      Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledCipherSuites()),
      Arrays.asList(CIPHERS));
    ldap.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"ssltest"})
  public void setEnabledProtocols()
    throws Exception
  {
    final Ldap ldap = this.createTLSLdap();
    final TLSSocketFactory sf =
      (TLSSocketFactory) ldap.getLdapConfig().getSslSocketFactory();

    AssertJUnit.assertTrue(ldap.connect());
    ldap.getSchema("ou=test,dc=vt,dc=edu");
    AssertJUnit.assertEquals(
      Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledProtocols()),
      Arrays.asList(ALL_PROTOCOLS));
    AssertJUnit.assertNotSame(
      Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledProtocols()),
      Arrays.asList(PROTOCOLS));
    ldap.close();

    sf.setEnabledProtocols(FAIL_PROTOCOLS);
    try {
      ldap.connect();
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, no exception thrown");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e);
    }
    ldap.close();

    sf.setEnabledProtocols(UNKNOWN_PROTOCOLS);
    try {
      ldap.connect();
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, no exception thrown");
    } catch (IllegalArgumentException e) {
      AssertJUnit.assertEquals(IllegalArgumentException.class, e.getClass());
    } catch (Exception e) {
      AssertJUnit.fail(
        "Should have thrown IllegalArgumentException, threw " + e);
    }
    ldap.close();

    sf.setEnabledProtocols(PROTOCOLS);
    AssertJUnit.assertTrue(ldap.connect());
    ldap.getSchema("ou=test,dc=vt,dc=edu");
    AssertJUnit.assertEquals(
      Arrays.asList(((SSLSocket) sf.createSocket()).getEnabledProtocols()),
      Arrays.asList(PROTOCOLS));
    ldap.close();
  }
}
