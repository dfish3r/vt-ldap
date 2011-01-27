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

import java.util.Arrays;
import edu.vt.middleware.ldap.handler.BinaryResultHandler;
import edu.vt.middleware.ldap.handler.DnAttributeResultHandler;
import edu.vt.middleware.ldap.handler.LdapResultHandler;
import edu.vt.middleware.ldap.handler.MergeResultHandler;
import edu.vt.middleware.ldap.handler.RecursiveResultHandler;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Unit test for {@link LdapConfig}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapConfigTest
{


  /** @throws  Exception  On test failure. */
  @Test(groups = {"configtest"})
  public void nullProperties()
    throws Exception
  {
    final LdapConnection conn = new LdapConnection(
      LdapConfig.createFromProperties(
        TestUtil.class.getResourceAsStream("/ldap.null.properties")));

    AssertJUnit.assertNull(conn.getLdapConfig().getSslSocketFactory());
    AssertJUnit.assertNull(conn.getLdapConfig().getHostnameVerifier());
    AssertJUnit.assertNull(conn.getLdapConfig().getLdapResultHandlers());
    AssertJUnit.assertNull(conn.getLdapConfig().getSearchIgnoreResultCodes());
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"configtest"})
  public void parserProperties()
    throws Exception
  {
    final LdapConnection conn = new LdapConnection(
      LdapConfig.createFromProperties(
        TestUtil.class.getResourceAsStream("/ldap.parser.properties")));

    AssertJUnit.assertEquals(
      SearchScope.OBJECT,
      conn.getLdapConfig().getSearchScope());
    AssertJUnit.assertEquals(10, conn.getLdapConfig().getBatchSize());
    AssertJUnit.assertEquals(5000, conn.getLdapConfig().getTimeLimit());
    AssertJUnit.assertEquals(8000, conn.getLdapConfig().getTimeout());
    AssertJUnit.assertEquals(
      "jpegPhoto", conn.getLdapConfig().getBinaryAttributes());

    for (LdapResultHandler srh :
         conn.getLdapConfig().getLdapResultHandlers()) {
      if (RecursiveResultHandler.class.isInstance(srh)) {
        final RecursiveResultHandler h = (RecursiveResultHandler)
          srh;
        AssertJUnit.assertEquals("member", h.getSearchAttribute());
        AssertJUnit.assertEquals(
          Arrays.asList(new String[] {"mail", "department"}),
          Arrays.asList(h.getMergeAttributes()));
      } else if (MergeResultHandler.class.isInstance(srh)) {
        final MergeResultHandler h = (MergeResultHandler) srh;
        AssertJUnit.assertTrue(h.getAllowDuplicates());
      } else if (BinaryResultHandler.class.isInstance(srh)) {
        final BinaryResultHandler h = (BinaryResultHandler) srh;
        AssertJUnit.assertNotNull(h);
      } else if (DnAttributeResultHandler.class.isInstance(srh)) {
        final DnAttributeResultHandler h = (DnAttributeResultHandler) srh;
        AssertJUnit.assertEquals("myDN", h.getDnAttributeName());
      } else {
        throw new Exception("Unknown search result handler type " + srh);
      }
    }
  }
}
