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


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry5")
  @BeforeClass(groups = {"renametest"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"renametest"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  oldDn  to rename.
   * @param  newDn  to rename to.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "renameOldDn", "renameNewDn" })
  @Test(groups = {"renametest"})
  public void renameLdapEntry(final String oldDn, final String newDn)
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    AssertJUnit.assertTrue(
      search.execute(
        SearchRequest.newObjectScopeSearchRequest(
          oldDn)).getResult().size() > 0);
    final RenameOperation rename = new RenameOperation(conn);
    rename.execute(new RenameRequest(oldDn, newDn));
    AssertJUnit.assertTrue(
      search.execute(
        SearchRequest.newObjectScopeSearchRequest(
          newDn)).getResult().size() > 0);
    try {
      search.execute(SearchRequest.newObjectScopeSearchRequest(oldDn));
      AssertJUnit.fail(
        "Should have thrown NameNotFoundException, no exception thrown");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(ResultCode.NO_SUCH_OBJECT, e.getResultCode());
    } catch (Exception e) {
      AssertJUnit.fail("Should have thrown NameNotFoundException, threw " + e);
    }
    rename.execute(new RenameRequest(newDn, oldDn));
    AssertJUnit.assertTrue(
      search.execute(
        SearchRequest.newObjectScopeSearchRequest(
          oldDn)).getResult().size() > 0);
    try {
      search.execute(SearchRequest.newObjectScopeSearchRequest(newDn));
      AssertJUnit.fail(
        "Should have thrown NameNotFoundException, no exception thrown");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(ResultCode.NO_SUCH_OBJECT, e.getResultCode());
    } catch (Exception e) {
      AssertJUnit.fail("Should have thrown NameNotFoundException, threw " + e);
    }
    conn.close();
  }
}
