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

import java.util.Arrays;
import edu.vt.middleware.ldap.handler.BinarySearchResultHandler;
import edu.vt.middleware.ldap.handler.EntryDnSearchResultHandler;
import edu.vt.middleware.ldap.handler.MergeSearchResultHandler;
import edu.vt.middleware.ldap.handler.RecursiveSearchResultHandler;
import edu.vt.middleware.ldap.handler.SearchResultHandler;
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


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ldaptest"})
  public void nullProperties()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      LdapConfigTest.class.getResourceAsStream("/ldap.null.properties"));

    AssertJUnit.assertNull(l.getLdapConfig().getSslSocketFactory());
    AssertJUnit.assertNull(l.getLdapConfig().getHostnameVerifier());
    AssertJUnit.assertNull(l.getLdapConfig().getOperationRetryExceptions());
    AssertJUnit.assertNull(l.getLdapConfig().getSearchResultHandlers());
    AssertJUnit.assertNull(l.getLdapConfig().getHandlerIgnoreExceptions());
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ldaptest"})
  public void parserProperties()
    throws Exception
  {
    final Ldap l = new Ldap();
    l.loadFromProperties(
      LdapConfigTest.class.getResourceAsStream("/ldap.parser.properties"));

    for (SearchResultHandler srh :
         l.getLdapConfig().getSearchResultHandlers()) {
      if (RecursiveSearchResultHandler.class.isInstance(srh)) {
        final RecursiveSearchResultHandler h =
          (RecursiveSearchResultHandler) srh;
        AssertJUnit.assertEquals("member", h.getSearchAttribute());
        AssertJUnit.assertEquals(
          Arrays.asList(new String[]{"mail", "department"}),
          Arrays.asList(h.getMergeAttributes()));
      } else if (MergeSearchResultHandler.class.isInstance(srh)) {
        final MergeSearchResultHandler h = (MergeSearchResultHandler) srh;
        AssertJUnit.assertTrue(h.getAllowDuplicates());
      } else if (BinarySearchResultHandler.class.isInstance(srh)) {
        final BinarySearchResultHandler h = (BinarySearchResultHandler) srh;
        AssertJUnit.assertNotNull(h);
      } else if (EntryDnSearchResultHandler.class.isInstance(srh)) {
        final EntryDnSearchResultHandler h = (EntryDnSearchResultHandler) srh;
        AssertJUnit.assertEquals("myDN", h.getDnAttributeName());
      } else {
        throw new Exception("Unknown search result handler type " + srh);
      }
    }
  }
}
