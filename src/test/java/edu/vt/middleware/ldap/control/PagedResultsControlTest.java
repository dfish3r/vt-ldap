/*
  $Id$

  Copyright (C) 2011 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.control;

import edu.vt.middleware.ldap.LdapUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link PagedResultsControl}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class PagedResultsControlTest
{


  /**
   * Paged results control test data.
   *
   * @return  response test data
   */
  @DataProvider(name = "request-response")
  public Object[][] createData()
  {
    return new Object[][] {
      // request size 0, no cookie
      // BER: 30:05:02:01:00:04:00
      new Object[] {
        LdapUtil.base64Decode("MAUCAQAEAA=="),
        new PagedResultsControl(0, null, true),
      },
      // request size 0, cookie
      // BER: 30:0D:02:01:00:04:08:FF:FF:FF:FF:FF:FF:FF:FF
      new Object[] {
        LdapUtil.base64Decode("MA0CAQAECP//////////"),
        new PagedResultsControl(
          0,
          new byte[] {
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
          },
          true),
      },
      // request size 1, no cookie
      // BER: 30:05:02:01:01:04:00
      new Object[] {
        LdapUtil.base64Decode("MAUCAQEEAA=="),
        new PagedResultsControl(1, null, true),
      },
      // request size 1, cookie
      // BER: 30:0D:02:01:01:04:08:EF:5C:15:00:00:00:00:00
      new Object[] {
        LdapUtil.base64Decode("MA0CAQEECO9cFQAAAAAA"),
        new PagedResultsControl(
          1,
          new byte[] {
            (byte) 0xef,
            (byte) 0x5c,
            (byte) 0x15,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
          },
          true),
      },
      // request size 20, no cookie
      // BER: 30:05:02:01:14:04:00
      new Object[] {
        LdapUtil.base64Decode("MAUCARQEAA=="),
        new PagedResultsControl(20, null, true),
      },
      // request size 20, cookie
      // BER: 30:0D:02:01:14:04:08:A7:C7:18:00:00:00:00:00
      new Object[] {
        LdapUtil.base64Decode("MA0CARQECKfHGAAAAAAA"),
        new PagedResultsControl(
          20,
          new byte[] {
            (byte) 0xa7,
            (byte) 0xc7,
            (byte) 0x18,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00,
          },
          true),
      },
    };
  }


  /**
   * @param  berValue  to encode.
   * @param  expected  paged results control to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"control"}, dataProvider = "request-response")
  public void encode(final byte[] berValue, final PagedResultsControl expected)
    throws Exception
  {
    Assert.assertEquals(expected.encode(), berValue);
  }


  /**
   * @param  berValue  to decode.
   * @param  expected  paged results control to test.
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"control"}, dataProvider = "request-response")
  public void decode(final byte[] berValue, final PagedResultsControl expected)
    throws Exception
  {
    final PagedResultsControl actual = new PagedResultsControl(
      expected.getCriticality());
    actual.decode(berValue);
    Assert.assertEquals(actual, expected);
  }
}
