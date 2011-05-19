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

import java.io.UnsupportedEncodingException;

/**
 * Provides convenience methods for converting the various types of passwords
 * into a byte array.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class Credential
{
  /** Credential stored as a byte array. */
  private byte[] bytes;


  /**
   * Creates a new credential.
   *
   * @param  password  converted from UTF-8 to a byte array
   */
  public Credential(final String password)
  {
    try {
      bytes = password.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(
        "Could not encode password as UTF-8", e);
    }
  }


  /**
   * Creates a new credential.
   *
   * @param  password  converted from UTF-8 to a byte array
   */
  public Credential(final char[] password)
  {
    try {
      bytes = new String(password).getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(
        "Could not encode password as UTF-8", e);
    }
  }


  /**
   * Creates a new credential.
   *
   * @param  password  to store
   */
  public Credential(final byte[] password)
  {
    bytes = password;
  }


  /**
   * Returns this credential as a byte array.
   *
   * @return  credential bytes
   */
  public byte[] getBytes()
  {
    return bytes;
  }


  /**
   * Returns this credential as a string.
   *
   * @return  credential string
   */
  public String getString()
  {
    try {
      return new String(bytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(
        "Could not encode password as UTF-8", e);
    }
  }


  /**
   * Returns this credential as a character array.
   *
   * @return  credential characters
   */
  public char[] getChars()
  {
    return getString().toCharArray();
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
        "[%s@%d::bytes=%s]",
        getClass().getName(),
        hashCode(),
        new String(bytes));
  }
}
