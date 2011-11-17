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

import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link RenameOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1633 $
 */
public class RenameOperationTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;

  /** Entry created for ldap tests. */
  private static LdapEntry renameLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry5")
  @BeforeClass(groups = {"rename"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"rename"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
    if (renameLdapEntry != null) {
      super.deleteLdapEntry(renameLdapEntry.getDn());
    }
  }


  /**
   * @param  oldDn  to rename.
   * @param  newDn  to rename to.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "renameOldDn", "renameNewDn" })
  @Test(groups = {"rename"})
  public void renameLdapEntry(final String oldDn, final String newDn)
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      AssertJUnit.assertTrue(
        search.execute(
          SearchRequest.newObjectScopeSearchRequest(
            oldDn)).getResult().size() > 0);
      final RenameOperation rename = new RenameOperation(conn);
      Response<Void> response = rename.execute(
        new RenameRequest(oldDn, newDn));
      AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
      renameLdapEntry = search.execute(
        SearchRequest.newObjectScopeSearchRequest(
          newDn)).getResult().getEntry();
      AssertJUnit.assertNotNull(renameLdapEntry);
      try {
        final Response<LdapResult> r = search.execute(
          SearchRequest.newObjectScopeSearchRequest(oldDn));
        AssertJUnit.assertEquals(ResultCode.NO_SUCH_OBJECT, r.getResultCode());
      } catch (LdapException e) {
        AssertJUnit.assertEquals(ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      } catch (Exception e) {
        AssertJUnit.fail("Should have thrown LdapException, threw " + e);
      }
      response = rename.execute(new RenameRequest(newDn, oldDn));
      AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
      AssertJUnit.assertTrue(
        search.execute(
          SearchRequest.newObjectScopeSearchRequest(
            oldDn)).getResult().size() > 0);
      try {
        final Response<LdapResult> r = search.execute(
          SearchRequest.newObjectScopeSearchRequest(newDn));
        AssertJUnit.assertEquals(ResultCode.NO_SUCH_OBJECT, r.getResultCode());
      } catch (LdapException e) {
        AssertJUnit.assertEquals(ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      } catch (Exception e) {
        AssertJUnit.fail("Should have thrown LdapException, threw " + e);
      }
    } finally {
      conn.close();
    }
  }
}
