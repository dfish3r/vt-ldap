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
package edu.vt.middleware.ldap.control;

import java.nio.ByteBuffer;
import java.util.Arrays;
import edu.vt.middleware.ldap.asn1.DERParser;
import edu.vt.middleware.ldap.asn1.DERPath;
import edu.vt.middleware.ldap.asn1.IntegerType;
import edu.vt.middleware.ldap.asn1.OctetStringType;
import edu.vt.middleware.ldap.asn1.ParseHandler;
import edu.vt.middleware.ldap.asn1.SequenceEncoder;

/**
 * Request/response control for PagedResults. See RFC 2696. Control is defined
 * as:
 * <pre>
 * realSearchControlValue ::= SEQUENCE {
 *   size            INTEGER (0..maxInt),
 *                           -- requested page size from client
 *                           -- result set size estimate from server
 *   cookie          OCTET STRING
 * }
 * </pre>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PagedResultsControl extends AbstractControl
                                 implements RequestControl, ResponseControl
{

  /** OID of this control. */
  public static final String OID = "1.2.840.113556.1.4.319";

  /** Empty byte array used for null cookies. */
  private static final byte[] EMPTY_COOKIE = new byte[0];

  /** paged results size. */
  private int resultSize;

  /** server generated cookie. */
  private byte[] cookie;


  /**
   * Default constructor.
   */
  public PagedResultsControl()
  {
    super(OID);
  }


  /**
   * Creates a new paged results control.
   *
   * @param  critical  whether this control is critical
   */
  public PagedResultsControl(final boolean critical)
  {
    super(OID, critical);
  }


  /**
   * Creates a new paged results control.
   *
   * @param  size  paged results size
   */
  public PagedResultsControl(final int size)
  {
    super(OID);
    setSize(size);
  }


  /**
   * Creates a new paged results control.
   *
   * @param  size  paged results size
   * @param  critical  whether this control is critical
   */
  public PagedResultsControl(final int size, final boolean critical)
  {
    super(OID, critical);
    setSize(size);
  }


  /**
   * Creates a new paged results control.
   *
   * @param  size  paged results size
   * @param  value  paged results cookie
   * @param  critical  whether this control is critical
   */
  public PagedResultsControl(
    final int size, final byte[] value, final boolean critical)
  {
    super(OID, critical);
    setSize(size);
    setCookie(value);
  }


  /** {@inheritDoc} */
  @Override
  public String getOID()
  {
    return OID;
  }


  /**
   * Returns the paged results size. For requests this is the requested page
   * size. For responses this is the result size estimate from the server.
   *
   * @return  paged results size
   */
  public int getSize()
  {
    return resultSize;
  }


  /**
   * Sets the paged results size. For requests this is the requested page size.
   * For responses this is the result size estimate from the server.
   *
   * @param  size  paged results size
   */
  public void setSize(final int size)
  {
    resultSize = size;
  }


  /**
   * Returns the paged results cookie.
   *
   * @return  paged results cookie
   */
  public byte[] getCookie()
  {
    return cookie;
  }


  /**
   * Sets the paged results cookie.
   *
   * @param  value  paged results cookie
   */
  public void setCookie(final byte[] value)
  {
    cookie = value;
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hc = super.hashCode();
    hc = (hc * HASH_CODE_SEED) + resultSize;
    hc = (hc * HASH_CODE_SEED) + (cookie != null ? Arrays.hashCode(cookie) : 0);
    return hc;
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
        "[%s@%d::criticality=%s, size=%s, cookie=%s]",
        getClass().getName(),
        hashCode(),
        getCriticality(),
        resultSize,
        Arrays.toString(cookie));
  }


  /** {@inheritDoc} */
  @Override
  public byte[] encode()
  {
    final SequenceEncoder se = new SequenceEncoder(
      new IntegerType(getSize()),
      new OctetStringType(getCookie() != null ? getCookie() : EMPTY_COOKIE));
    return se.encode();
  }


  /** {@inheritDoc} */
  @Override
  public void decode(final byte[] berValue)
  {
    final PagedResultsHandler handler = new PagedResultsHandler(this);
    final DERParser parser = new DERParser();
    parser.registerHandler(PagedResultsHandler.SIZE_PATH, handler);
    parser.registerHandler(PagedResultsHandler.COOKIE_PATH, handler);
    parser.parse(ByteBuffer.wrap(berValue));
  }


  /**
   * Parse handler implementation for the paged results control.
   */
  private static class PagedResultsHandler implements ParseHandler
  {

    /** DER path to result size. */
    public static final DERPath SIZE_PATH = new DERPath("/SEQ/INT[0]");

    /** DER path to cookie value. */
    public static final DERPath COOKIE_PATH = new DERPath("/SEQ/OCTSTR[1]");

    /** Paged results control to configure with this handler. */
    private final PagedResultsControl pagedResults;


    /**
     * Creates a new paged results handler.
     *
     * @param  control  to configure
     */
    public PagedResultsHandler(final PagedResultsControl control)
    {
      pagedResults = control;
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final DERParser parser, final ByteBuffer encoded)
    {
      if (SIZE_PATH.equals(parser.getCurrentPath())) {
        pagedResults.setSize(IntegerType.decode(encoded).intValue());
      } else if (COOKIE_PATH.equals(parser.getCurrentPath())) {
        final byte[] cookie = OctetStringType.readBuffer(encoded);
        if (cookie != null && cookie.length > 0) {
          pagedResults.setCookie(cookie);
        }
      }
    }
  }
}
