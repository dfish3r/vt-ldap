/*
  $Id: LdapTLSSocketFactory.java 1106 2010-01-30 04:34:13Z dfisher $

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1106 $
  Updated: $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
*/
package edu.vt.middleware.ldap.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.GeneralSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides functionality common to PathTypeReaders.
 *
 * @author  Middleware Services
 * @version  $Revision: 1106 $ $Date: 2010-01-29 23:34:13 -0500 (Fri, 29 Jan 2010) $
 */
public abstract class AbstractPathTypeReader implements PathTypeReader
{
  /** URI prefix that indicates a file path. */
  public static final String FILE_URI_PREFIX = "file:";

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());


  /** {@inheritDoc} */
  public abstract SSLContextInitializer createSSLContextInitializer()
    throws GeneralSecurityException;


  /**
   * This returns the supplied file as an <code>InputStream</code>. If the file
   * could not be loaded this method returns null.
   *
   * @param  filename  <code>String</code> to read
   * @param  pt  <code>PathType</code> how to read file
   *
   * @return  <code>InputStream</code> keystore
   */
  protected InputStream getInputStream(final String filename, final PathType pt)
  {
    InputStream is = null;
    if (pt == PathType.CLASSPATH) {
      is = this.getClass().getResourceAsStream(filename);
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
        if (this.logger.isWarnEnabled()) {
          this.logger.warn("Error loading keystore from " + filename, e);
        }
      }
    }
    if (is != null) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Successfully loaded " + filename + " from " + pt);
      }
    } else {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Failed to load " + filename + " from " + pt);
      }
    }
    return is;
  }
}
