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
 * Unit test for {@link LdapResult}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapResultTest extends AbstractTest
{

  /** Entry created for tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry7")
  @BeforeClass(groups = {"beantest"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"beantest"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  dn  to search for.
   * @param  filter  to search with.
   * @param  returnAttrs  attributes to return from search
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "toSearchResultsDn",
      "toSearchResultsFilter",
      "toSearchResultsAttrs",
      "toSearchResultsResults"
    }
  )
  @Test(groups = {"beantest"})
  public void toSearchResults(
    final String dn,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final SearchOperation search = new SearchOperation(conn);

    final LdapResult result = search.execute(
      new SearchRequest(
        dn, new SearchFilter(filter), returnAttrs.split("\\|"))).getResult();
    final String expected = TestUtil.readFileIntoString(ldifFile);
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    conn.close();
  }
}
