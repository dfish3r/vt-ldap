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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all credential readers. It provides support for loading files
 * from resources on the classpath or a filepath. If a path is prefixed with the
 * string "classpath:" it is interpreted as a classpath specification. If a path
 * is prefixed with the string "file:" it is interpreted as a file path. Any
 * other input throws IllegalArgumentException.
 *
 * @param  <T>  Type of credential read by this instance.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractCredentialReader<T> implements CredentialReader<T>
{

  /** Prefix used to indicate a classpath resource. */
  public static final String CLASSPATH_PREFIX = "classpath:";

  /** Prefix used to indicate a file resource. */
  public static final String FILE_PREFIX = "file:";

  /** Start index of path specification when given a classpath resource. */
  private static final int CLASSPATH_START_INDEX = CLASSPATH_PREFIX.length();

  /** Start index of path specification when given a file resource. */
  private static final int FILE_START_INDEX = FILE_PREFIX.length();

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());


  /** {@inheritDoc} */
  @Override
  public T read(final String path, final String... params)
    throws IOException, GeneralSecurityException
  {
    InputStream is = null;
    if (path.startsWith(CLASSPATH_PREFIX)) {
      is = getClass().getResourceAsStream(
        path.substring(CLASSPATH_START_INDEX));
    } else if (path.startsWith(FILE_PREFIX)) {
      is = new FileInputStream(new File(path.substring(FILE_START_INDEX)));
    } else {
      throw new IllegalArgumentException(
        "path must start with either " + CLASSPATH_PREFIX + " or " +
        FILE_PREFIX);
    }
    if (is != null) {
      try {
        return read(is, params);
      } finally {
        logger.debug("Successfully loaded {}", path);
        is.close();
      }
    } else {
      logger.debug("Failed to load {}", path);
      return null;
    }
  }


  /**
   * Gets a buffered input stream from the given input stream. If the given
   * instance is already buffered, it is simply returned.
   *
   * @param  is  input stream from which to create buffered instance.
   *
   * @return  buffered input stream. If the given instance is already buffered,
   * it is simply returned.
   */
  protected InputStream getBufferedInputStream(final InputStream is)
  {
    if (is.markSupported()) {
      return is;
    } else {
      return new BufferedInputStream(is);
    }
  }
}
