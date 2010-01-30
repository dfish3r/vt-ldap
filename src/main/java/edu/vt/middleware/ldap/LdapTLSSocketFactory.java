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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>TLSSocketFactory</code> is an extension of SSLSocketFactory. It was
 * written to allow easy use of keystores and truststores. Note that {@link
 * #initialize()} must be called prior to using this socket factory. This means
 * that this class cannot be passed to implementations that expect the socket
 * factory to function immediately after construction.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapTLSSocketFactory extends SSLSocketFactory
{

  /** Default SSL protocol, value is {@value}. */
  public static final String DEFAULT_PROTOCOL = "TLS";

  /** Default truststore name, value is {@value}. */
  public static final String DEFAULT_TRUSTSTORE_NAME = "/vt-ldap.truststore";

  /** Default truststore password, value is {@value}. */
  public static final String DEFAULT_TRUSTSTORE_PASSWORD = "changeit";

  /** Default truststore type, value is {@value}. */
  public static final String DEFAULT_TRUSTSTORE_TYPE = "JKS";

  /** Default keystore name, value is {@value}. */
  public static final String DEFAULT_KEYSTORE_NAME = "/vt-ldap.keystore";

  /** Default keystore password, value is {@value}. */
  public static final String DEFAULT_KEYSTORE_PASSWORD = "changeit";

  /** Default keystore type, value is {@value}. */
  public static final String DEFAULT_KEYSTORE_TYPE = "JKS";

  /** Types of paths. */
  public enum PathType {

    /** File path location. */
    FILEPATH,

    /** Classpath location. */
    CLASSPATH
  }

  /** SSLSocketFactory used for creating SSL sockets. */
  protected SSLSocketFactory factory;

  /** Name of the truststore to use for the SSL connection. */
  private String trustStoreName = DEFAULT_TRUSTSTORE_NAME;

  /** Password needed to open the truststore. */
  private String trustStorePassword = DEFAULT_TRUSTSTORE_PASSWORD;

  /** Truststore path type. */
  private PathType trustStorePathType = PathType.CLASSPATH;

  /** Truststore type. */
  private String trustStoreType = DEFAULT_TRUSTSTORE_TYPE;

  /** Name of the keystore to use for the SSL connection. */
  private String keyStoreName = DEFAULT_KEYSTORE_NAME;

  /** Password needed to open the keystore. */
  private String keyStorePassword = DEFAULT_KEYSTORE_PASSWORD;

  /** Keystore path type. */
  private PathType keyStorePathType = PathType.CLASSPATH;

  /** Keystore type. */
  private String keyStoreType = DEFAULT_KEYSTORE_TYPE;


  /** Default constructor. */
  public LdapTLSSocketFactory() {}


  /**
   * Creates the underlying SSLContext using truststore and keystore attributes
   * and makes this factory ready for use. Must be called before factory can be
   * used.
   *
   * @throws  IOException  if the keystore cannot be loaded
   * @throws  GeneralSecurityException  if the SSLContext cannot be created
   */
  public void initialize()
    throws IOException, GeneralSecurityException
  {
    final SSLContext ctx = SSLContext.getInstance(DEFAULT_PROTOCOL);
    final TrustManager[] tm = this.initTrustManager(
      this.getTrustStoreStream(),
      this.getTrustStorePassword(),
      this.getTrustStoreType());
    final KeyManager[] km = this.initKeyManager(
      this.getKeyStoreStream(),
      this.getKeyStorePassword(),
      this.getKeyStoreType());
    ctx.init(km, tm, null);
    this.factory = ctx.getSocketFactory();
  }


  /**
   * This attempts to load the TrustManagers from the supplied <code>
   * InputStream</code> using the supplied password.
   *
   * @param  is  <code>InputStream</code> containing the truststore
   * @param  password  <code>String</code> to unlock the truststore
   * @param  storeType  <code>String</code> of truststore
   *
   * @return  <code>TrustManager[]</code>
   *
   * @throws  IOException  if the keystore cannot be loaded
   * @throws  GeneralSecurityException  if an errors occurs while loading the
   * TrustManagers
   */
  private TrustManager[] initTrustManager(
    final InputStream is,
    final String password,
    final String storeType)
    throws IOException, GeneralSecurityException
  {
    TrustManager[] tm = null;
    if (is != null) {
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(this.loadKeyStore(is, password, storeType));
      tm = tmf.getTrustManagers();
    }
    return tm;
  }


  /**
   * This attempts to load the KeyManagers from the supplied <code>
   * InputStream</code> using the supplied password.
   *
   * @param  is  <code>InputStream</code> containing the keystore
   * @param  password  <code>String</code> to unlock the keystore
   * @param  storeType  <code>String</code> of keystore
   *
   * @return  <code>KeyManager[]</code>
   *
   * @throws  IOException  if the keystore cannot be loaded
   * @throws  GeneralSecurityException  if an errors occurs while loading the
   * KeyManagers
   */
  private KeyManager[] initKeyManager(
    final InputStream is,
    final String password,
    final String storeType)
    throws IOException, GeneralSecurityException
  {
    KeyManager[] km = null;
    if (is != null) {
      final KeyManagerFactory kmf = KeyManagerFactory.getInstance(
        KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(
        this.loadKeyStore(is, password, storeType),
        password != null ? password.toCharArray() : null);
      km = kmf.getKeyManagers();
    }
    return km;
  }


  /**
   * This returns the name of the truststore to use.
   *
   * @return  <code>String</code> truststore name
   */
  public String getTrustStoreName()
  {
    return this.trustStoreName;
  }


  /**
   * This sets the name of the truststore to use.
   *
   * @param  s  <code>String</code> truststore name
   */
  public void setTrustStoreName(final String s)
  {
    this.trustStoreName = s;
  }


  /**
   * This gets the path type of the truststore.
   *
   * @return  <code>PathType</code> truststore path type
   */
  public PathType getTrustStorePathType()
  {
    return this.trustStorePathType;
  }


  /**
   * This sets the path type of the truststore.
   *
   * @param  pt  <code>PathType</code> truststore path type
   */
  public void setTrustStorePathType(final PathType pt)
  {
    this.trustStorePathType = pt;
  }


  /**
   * This returns the truststore as an <code>InputStream</code>. If the
   * truststore could not be loaded this method returns null.
   *
   * @return  <code>InputStream</code> truststore
   */
  protected InputStream getTrustStoreStream()
  {
    return this.getInputStream(this.trustStoreName, this.trustStorePathType);
  }


  /**
   * This returns the password for the truststore.
   *
   * @return  <code>String</code> truststore password
   */
  public String getTrustStorePassword()
  {
    return this.trustStorePassword;
  }


  /**
   * This sets the password for the truststore.
   *
   * @param  s  <code>String</code> truststore password
   */
  public void setTrustStorePassword(final String s)
  {
    this.trustStorePassword = s;
  }


  /**
   * This returns the type of the truststore.
   *
   * @return  <code>String</code> truststore type
   */
  public String getTrustStoreType()
  {
    return this.trustStoreType;
  }


  /**
   * This sets the type of the truststore.
   *
   * @param  s  <code>String</code> truststore type
   */
  public void setTrustStoreType(final String s)
  {
    this.trustStoreType = s;
  }


  /**
   * This returns the name of the keystore to use.
   *
   * @return  <code>String</code> keystore name
   */
  public String getKeyStoreName()
  {
    return this.keyStoreName;
  }


  /**
   * This sets the name of the keystore to use.
   *
   * @param  s  <code>String</code> keystore name
   */
  public void setKeyStoreName(final String s)
  {
    this.keyStoreName = s;
  }


  /**
   * This gets the path type of the keystore.
   *
   * @return  <code>PathType</code> keystore path type
   */
  public PathType getKeyStorePathType()
  {
    return this.keyStorePathType;
  }


  /**
   * This sets the path type of the keystore.
   *
   * @param  pt  <code>PathType</code> keystore path type
   */
  public void setKeyStorePathType(final PathType pt)
  {
    this.keyStorePathType = pt;
  }


  /**
   * This returns the keystore as an <code>InputStream</code>. If the keystore
   * could not be loaded this method returns null.
   *
   * @return  <code>InputStream</code> keystore
   */
  protected InputStream getKeyStoreStream()
  {
    return this.getInputStream(this.keyStoreName, this.keyStorePathType);
  }


  /**
   * This returns the password for the keystore.
   *
   * @return  <code>String</code> keystore password
   */
  public String getKeyStorePassword()
  {
    return this.keyStorePassword;
  }


  /**
   * This sets the password for the keystore.
   *
   * @param  s  <code>String</code> keystore password
   */
  public void setKeyStorePassword(final String s)
  {
    this.keyStorePassword = s;
  }


  /**
   * This returns the type of the keystore.
   *
   * @return  <code>String</code> keystore type
   */
  public String getKeyStoreType()
  {
    return this.keyStoreType;
  }


  /**
   * This sets the type of the keystore.
   *
   * @param  s  <code>String</code> keystore type
   */
  public void setKeyStoreType(final String s)
  {
    this.keyStoreType = s;
  }


  /**
   * This returns the underlying <code>SSLSocketFactory</code> that this class
   * uses for creating SSL Sockets.
   *
   * @return  <code>SSLSocketFactory</code>
   */
  public SSLSocketFactory getFactory()
  {
    return this.factory;
  }


  /**
   * This returns the default SSL socket factory.
   *
   * @return  <code>SocketFactory</code>
   */
  public static SocketFactory getDefault()
  {
    final LdapTLSSocketFactory sf = new LdapTLSSocketFactory();
    try {
      sf.initialize();
    } catch (IOException e) {
      final Log logger = LogFactory.getLog(LdapTLSSocketFactory.class);
      if (logger.isErrorEnabled()) {
        logger.error("Error loading keystore", e);
      }
    } catch (GeneralSecurityException e) {
      final Log logger = LogFactory.getLog(LdapTLSSocketFactory.class);
      if (logger.isErrorEnabled()) {
        logger.error("Error initializing socket factory", e);
      }
    }
    return sf;
  }


  /**
   * This returns a socket layered over an existing socket connected to the
   * named host, at the given port.
   *
   * @param  s  <code>Socket</code> existing socket
   * @param  host  <code>String</code> server hostname
   * @param  port  <code>int</code> server port
   * @param  autoClose  <code>boolean</code> close the underlying socket when
   * this socket is closed
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final Socket s,
    final String host,
    final int port,
    final boolean autoClose)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(s, host, port, autoClose);
    }
    return socket;
  }


  /**
   * This creates a socket and connects it to the specified port number at the
   * specified addres.
   *
   * @param  host  <code>InetAddress</code> server hostname
   * @param  port  <code>int</code> server port
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(final InetAddress host, final int port)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(host, port);
    }
    return socket;
  }


  /**
   * This creates a socket and connect it to the specified port number at the
   * specified address. The socket will also be bound to the supplied local
   * address and port.
   *
   * @param  address  <code>InetAddress</code> server hostname
   * @param  port  <code>int</code> server port
   * @param  localAddress  <code>InetAddress</code> client hostname
   * @param  localPort  <code>int</code> client port
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final InetAddress address,
    final int port,
    final InetAddress localAddress,
    final int localPort)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(
        address,
        port,
        localAddress,
        localPort);
    }
    return socket;
  }


  /**
   * This creates a socket and connects it to the specified port number at the
   * specified addres.
   *
   * @param  host  <code>String</code> server hostname
   * @param  port  <code>int</code> server port
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(final String host, final int port)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(host, port);
    }
    return socket;
  }


  /**
   * This creates a socket and connect it to the specified port number at the
   * specified address. The socket will also be bound to the supplied local
   * address and port.
   *
   * @param  host  <code>String</code> server hostname
   * @param  port  <code>int</code> server port
   * @param  localHost  <code>InetAddress</code> client hostname
   * @param  localPort  <code>int</code> client port
   *
   * @return  <code>Socket</code> - connected to the specified host and port
   *
   * @throws  IOException  if an I/O error occurs when creating the socket
   */
  public Socket createSocket(
    final String host,
    final int port,
    final InetAddress localHost,
    final int localPort)
    throws IOException
  {
    Socket socket = null;
    if (this.factory != null) {
      socket = this.factory.createSocket(host, port, localHost, localPort);
    }
    return socket;
  }


  /**
   * This returns the list of cipher suites which are enabled by default.
   *
   * @return  <code>String[]</code> - array of the cipher suites
   */
  public String[] getDefaultCipherSuites()
  {
    String[] ciphers = null;
    if (this.factory != null) {
      ciphers = this.factory.getDefaultCipherSuites();
    }
    return ciphers;
  }


  /**
   * This returns the names of the cipher suites which could be enabled for use
   * on an SSL connection.
   *
   * @return  <code>String[]</code> - array of the cipher suites
   */
  public String[] getSupportedCipherSuites()
  {
    String[] ciphers = null;
    if (this.factory != null) {
      ciphers = this.factory.getSupportedCipherSuites();
    }
    return ciphers;
  }


  /**
   * This returns a keystore as an <code>InputStream</code>. If the keystore
   * could not be loaded this method returns null.
   *
   * @param  filename  <code>String</code> to read
   * @param  pt  <code>PathType</code> how to read file
   *
   * @return  <code>InputStream</code> keystore
   */
  private InputStream getInputStream(final String filename, final PathType pt)
  {
    final Log logger = LogFactory.getLog(LdapTLSSocketFactory.class);
    InputStream is = null;
    if (pt == PathType.CLASSPATH) {
      is = LdapTLSSocketFactory.class.getResourceAsStream(filename);
    } else if (pt == PathType.FILEPATH) {
      File file;
      try {
        file = new File(URI.create(filename));
      } catch (IllegalArgumentException e) {
        file = new File(filename);
      }
      try {
        is = new FileInputStream(file);
      } catch (IOException e) {
        if (logger.isWarnEnabled()) {
          logger.warn("Error loading keystore from " + filename, e);
        }
      }
    }
    if (is != null) {
      if (logger.isDebugEnabled()) {
        logger.debug("Successfully loaded " + filename + " from " + pt);
      }
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("Failed to load " + filename + " from " + pt);
      }
    }
    return is;
  }


  /**
   * This attempts to load a keystore from the supplied <code>InputStream</code>
   * using the supplied password.
   *
   * @param  is  <code>InputStream</code> containing the keystore
   * @param  password  <code>String</code> to unlock the keystore
   * @param  storeType  <code>String</code> of keystore
   *
   * @return  <code>KeyStore</code>
   *
   * @throws  IOException  if the keystore cannot be loaded
   * @throws  GeneralSecurityException  if an errors occurs while loading the
   * KeyManagers
   */
  private KeyStore loadKeyStore(
    final InputStream is,
    final String password,
    final String storeType)
    throws IOException, GeneralSecurityException
  {
    KeyStore keystore = null;
    if (is != null) {
      String type = storeType;
      if (type == null) {
        type = KeyStore.getDefaultType();
      }
      keystore = KeyStore.getInstance(type);

      char[] pw = null;
      if (password != null) {
        pw = password.toCharArray();
      }
      keystore.load(is, pw);
    }
    return keystore;
  }
}
