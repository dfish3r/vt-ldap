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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import edu.vt.middleware.ldap.control.PagedResultsControl;
import edu.vt.middleware.ldap.control.SortKey;
import edu.vt.middleware.ldap.control.SortRequestControl;
import edu.vt.middleware.ldap.handler.CaseChangeEntryHandler;
import edu.vt.middleware.ldap.handler.CaseChangeEntryHandler.CaseChange;
import edu.vt.middleware.ldap.handler.DnAttributeEntryHandler;
import edu.vt.middleware.ldap.handler.LdapEntryHandler;
import edu.vt.middleware.ldap.handler.MergeAttributeEntryHandler;
import edu.vt.middleware.ldap.handler.NoOpEntryHandler;
import edu.vt.middleware.ldap.handler.RecursiveEntryHandler;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SearchOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1633 $
 */
public class SearchOperationTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;

  /** Entry created for ldap tests. */
  private static LdapEntry specialCharsLdapEntry;

  /** Entries for group tests. */
  private static Map<String, LdapEntry[]> groupEntries =
    new HashMap<String, LdapEntry[]>();

  /**
   * Initialize the map of group entries.
   */
  static {
    for (int i = 2; i <= 5; i++) {
      groupEntries.put(String.valueOf(i), new LdapEntry[2]);
    }
  }

  /** Connection instance for concurrency testing. */
  protected Connection singleConn;


  /**
   * Default constructor.
   *
   * @throws  Exception  On test failure.
   */
  public SearchOperationTest()
    throws Exception
  {
    singleConn = TestUtil.createConnection();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @BeforeClass(groups = {"search"})
  public void openConnection()
    throws Exception
  {
    singleConn.open();
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry2")
  @BeforeClass(groups = {"search"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /**
   * @param  ldifFile2  to create.
   * @param  ldifFile3  to create.
   * @param  ldifFile4  to create.
   * @param  ldifFile5  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "createGroup2",
      "createGroup3",
      "createGroup4",
      "createGroup5"
    }
  )
  @BeforeClass(groups = {"search"})
  public void createGroupEntry(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4,
    final String ldifFile5)
    throws Exception
  {
    // CheckStyle:Indentation OFF
    groupEntries.get("2")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile2)).getEntry();
    groupEntries.get("3")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile3)).getEntry();
    groupEntries.get("4")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile4)).getEntry();
    groupEntries.get("5")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile5)).getEntry();
    // CheckStyle:Indentation ON

    for (Map.Entry<String, LdapEntry[]> e : groupEntries.entrySet()) {
      super.createLdapEntry(e.getValue()[0]);
    }

    // setup group relationships
    final Connection conn = TestUtil.createSetupConnection();
    try {
      conn.open();
      final ModifyOperation modify = new ModifyOperation(conn);
      modify.execute(new ModifyRequest(
        groupEntries.get("2")[0].getDn(),
        new AttributeModification(
          AttributeModificationType.ADD,
          new LdapAttribute(
            "member",
            new String[]{"uugid=group3,ou=test,dc=vt,dc=edu"}))));
      modify.execute(new ModifyRequest(
        groupEntries.get("3")[0].getDn(),
        new AttributeModification(
          AttributeModificationType.ADD,
          new LdapAttribute(
            "member",
            new String[]{
              "uugid=group4,ou=test,dc=vt,dc=edu",
              "uugid=group5,ou=test,dc=vt,dc=edu", }))));
      modify.execute(new ModifyRequest(
        groupEntries.get("4")[0].getDn(),
        new AttributeModification(
          AttributeModificationType.ADD,
          new LdapAttribute(
            "member",
            new String[]{"uugid=group3,ou=test,dc=vt,dc=edu"}))));
    } finally {
      conn.close();
    }
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createSpecialCharsEntry")
  @BeforeClass(groups = {"search"})
  public void createSpecialCharsEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    specialCharsLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(specialCharsLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"search"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
    super.deleteLdapEntry(specialCharsLdapEntry.getDn());
    super.deleteLdapEntry(groupEntries.get("2")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("3")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("4")[0].getDn());
    super.deleteLdapEntry(groupEntries.get("5")[0].getDn());
  }


  /**
   * @param  createNew  whether to construct a new connection.
   *
   * @return  connection
   *
   * @throws  Exception  On connection failure.
   */
  public Connection createLdapConnection(final boolean createNew)
    throws Exception
  {
    if (createNew) {
      return TestUtil.createConnection();
    }
    return singleConn;
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterArgs  to replace args in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchDn",
      "searchFilter",
      "searchFilterArgs",
      "searchReturnAttrs",
      "searchResults"
    }
  )
  @Test(
    groups = {"search"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void search(
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));

    final String expected = TestUtil.readFileIntoString(ldifFile);

    final LdapResult entryDnResult = TestUtil.convertLdifToResult(expected);
    entryDnResult.getEntry().addAttribute(
      new LdapAttribute("entryDN", entryDnResult.getEntry().getDn()));

    // test searching
    LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);

    // test searching no attributes
    result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        new String[]{})).getResult();
    AssertJUnit.assertTrue(result.getEntry().getAttributes().isEmpty());

    // test searching without handler
    result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"),
        new LdapEntryHandler[0])).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);

    // test searching with multiple handlers
    final DnAttributeEntryHandler srh = new DnAttributeEntryHandler();
    result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"),
        new LdapEntryHandler[]{
          new NoOpEntryHandler(), srh, })).getResult();
    AssertJUnit.assertEquals(entryDnResult, result);

    // test that entry dn handler is no-op if attribute name conflicts
    srh.setDnAttributeName("givenName");
    result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"),
        new LdapEntryHandler[]{
          new NoOpEntryHandler(), srh, })).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "pagedSearchDn",
      "pagedSearchFilter",
      "pagedSearchResults"
    })
  @Test(groups = {"search"})
  public void pagedSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final PagedResultsControl prc = new PagedResultsControl(1, true);
    final Connection conn = TestUtil.createConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final String expected = TestUtil.readFileIntoString(ldifFile);

      // test searching
      final SearchRequest request = new SearchRequest(
        dn, new SearchFilter(filter));
      request.setControls(prc);
      final LdapResult result = search.execute(request).getResult();
      AssertJUnit.assertEquals(
        TestUtil.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "sortSearchDn",
      "sortSearchFilter"
    })
  @Test(groups = {"search"})
  public void sortedSearch(
    final String dn,
    final String filter)
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final SearchRequest request = new SearchRequest(
        dn, new SearchFilter(filter));
      request.setSortBehavior(SortBehavior.ORDERED);

      // test sort by uugid
      SortRequestControl src = new SortRequestControl(
        new SortKey[] {new SortKey("uugid", "caseExactMatch")}, true);
      request.setControls(src);
      LdapResult result = search.execute(request).getResult();

      // confirm sorted
      int i = 2;
      for (LdapEntry e : result.getEntries()) {
        AssertJUnit.assertEquals(
          String.valueOf(2000 + i), e.getAttribute("uid").getStringValue());
        i++;
      }

      // test sort by uid
      src = new SortRequestControl(
        new SortKey[] {new SortKey("uid", "integerMatch", true)}, true);
      request.setControls(src);
      result = search.execute(request).getResult();

      // confirm sorted
      i = 5;
      for (LdapEntry e : result.getEntries()) {
        AssertJUnit.assertEquals(
          String.valueOf(2000 + i), e.getAttribute("uid").getStringValue());
        i--;
      }
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterArgs  to replace args in filter with.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "recursiveSearchDn",
      "recursiveSearchFilter",
      "recursiveSearchFilterArgs",
      "recursiveHandlerResults"
    }
  )
  @Test(groups = {"search"})
  public void recursiveHandlerSearch(
    final String dn,
    final String filter,
    final String filterArgs,
    final String ldifFile)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));

    final String expected = TestUtil.readFileIntoString(ldifFile);

    // test recursive searching
    final RecursiveEntryHandler rsrh = new RecursiveEntryHandler(
      "member",
      new String[] {"uugid", "uid"});

    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        null,
        new LdapEntryHandler[]{rsrh})).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "mergeSearchDn",
      "mergeSearchFilter",
      "mergeSearchResults"
    })
  @Test(groups = {"search"})
  public void mergeSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final String expected = TestUtil.readFileIntoString(ldifFile);

      // test result merge
      final SearchRequest sr = new SearchRequest(dn, new SearchFilter(filter));
      sr.setSortBehavior(SortBehavior.SORTED);
      final LdapResult result = search.execute(sr).getResult();
      AssertJUnit.assertEquals(
        TestUtil.convertLdifToResult(expected),
        LdapResult.mergeResults(result));
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "mergeDuplicateSearchDn",
      "mergeDuplicateSearchFilter",
      "mergeDuplicateSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void mergeDuplicateSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final String expected = TestUtil.readFileIntoString(ldifFile);

      // test result merge
      final SearchRequest sr = new SearchRequest(dn, new SearchFilter(filter));
      sr.setSortBehavior(SortBehavior.SORTED);
      final LdapResult result = search.execute(sr).getResult();
      AssertJUnit.assertEquals(
        TestUtil.convertLdifToResult(expected),
        LdapResult.mergeResults(result));
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "mergeAttributeSearchDn",
      "mergeAttributeSearchFilter",
      "mergeAttributeReturnAttrs",
      "mergeAttributeSearchResults"
    })
  @Test(groups = {"search"})
  public void mergeAttributeSearch(
    final String dn,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);

      final String expected = TestUtil.readFileIntoString(ldifFile);

      // test merge searching
      final MergeAttributeEntryHandler handler =
        new MergeAttributeEntryHandler();
      handler.setMergeAttributeName("cn");
      handler.setAttributeNames(
        new String[] {"displayName", "givenName", "sn", });

      final SearchRequest sr = new SearchRequest(
        dn,
        new SearchFilter(filter),
        returnAttrs.split("\\|"),
        new LdapEntryHandler[]{handler});
      sr.setSortBehavior(SortBehavior.SORTED);
      final LdapResult result = search.execute(sr).getResult();
      AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  returnAttr  to return from search.
   * @param  base64Value  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "binarySearchDn",
      "binarySearchFilter",
      "binarySearchReturnAttr",
      "binarySearchResult"
    }
  )
  @Test(groups = {"search"})
  public void binarySearch(
    final String dn,
    final String filter,
    final String returnAttr,
    final String base64Value)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));

    // test binary searching
    SearchRequest request = new SearchRequest(
      dn,
      new SearchFilter(filter),
      new String[] {returnAttr});
    request.setBinaryAttributes(new String[]{returnAttr});
    LdapResult result = search.execute(request).getResult();
    AssertJUnit.assertNotSame(
      base64Value,
      result.getEntry().getAttribute().getStringValue());

    request = new SearchRequest(
      dn, new SearchFilter(filter), new String[] {returnAttr});
    request.setBinaryAttributes(new String[]{returnAttr});
    result = search.execute(request).getResult();
    AssertJUnit.assertEquals(
      base64Value,
      result.getEntry().getAttribute().getStringValue());
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterArgs  to replace args in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
      {
        "searchDn",
        "searchFilter",
        "searchFilterArgs",
        "searchReturnAttrs",
        "searchResults"
      }
    )
  @Test(groups = {"search"})
  public void caseChangeSearch(
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final CaseChangeEntryHandler srh =
        new CaseChangeEntryHandler();
      final String expected = TestUtil.readFileIntoString(ldifFile);

      // test no case change
      final LdapResult noChangeResult = TestUtil.convertLdifToResult(expected);
      LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterArgs.split("\\|")),
          returnAttrs.split("\\|"),
          new LdapEntryHandler[]{srh})).getResult();
      AssertJUnit.assertEquals(noChangeResult, result);

      // test lower case attribute values
      srh.setAttributeValueCaseChange(CaseChange.LOWER);
      final LdapResult lcValuesChangeResult = TestUtil.convertLdifToResult(
        expected);
      for (LdapAttribute la : lcValuesChangeResult.getEntry().getAttributes()) {
        final Set<String> s = new HashSet<String>();
        for (String value : la.getStringValues()) {
          s.add(value.toLowerCase());
        }
        la.clear();
        la.addStringValues(s);
      }
      result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterArgs.split("\\|")),
          returnAttrs.split("\\|"),
          new LdapEntryHandler[]{srh})).getResult();
      AssertJUnit.assertEquals(lcValuesChangeResult, result);

      // test upper case attribute names
      srh.setAttributeValueCaseChange(CaseChange.NONE);
      srh.setAttributeNameCaseChange(CaseChange.UPPER);
      final LdapResult ucNamesChangeResult = TestUtil.convertLdifToResult(
        expected);
      for (LdapAttribute la : ucNamesChangeResult.getEntry().getAttributes()) {
        la.setName(la.getName().toUpperCase());
      }
      result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterArgs.split("\\|")),
          returnAttrs.split("\\|"),
          new LdapEntryHandler[]{srh})).getResult();
      AssertJUnit.assertEquals(ucNamesChangeResult, result);

      // test lower case everything
      srh.setAttributeValueCaseChange(CaseChange.LOWER);
      srh.setAttributeNameCaseChange(CaseChange.LOWER);
      srh.setDnCaseChange(CaseChange.LOWER);
      final LdapResult lcAllChangeResult = TestUtil.convertLdifToResult(
        expected);
      for (LdapAttribute la : ucNamesChangeResult.getEntry().getAttributes()) {
        lcAllChangeResult.getEntry().setDn(
          lcAllChangeResult.getEntry().getDn().toLowerCase());
        la.setName(la.getName().toLowerCase());
        final Set<String> s = new HashSet<String>();
        for (String value : la.getStringValues()) {
          s.add(value.toLowerCase());
        }
        la.clear();
        la.addStringValues(s);
      }
      result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterArgs.split("\\|")),
          returnAttrs.split("\\|"),
          new LdapEntryHandler[]{srh})).getResult();
      AssertJUnit.assertEquals(ucNamesChangeResult, result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "specialCharSearchDn",
      "specialCharSearchFilter",
      "specialCharSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void specialCharsSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapResult specialCharsResult = TestUtil.convertLdifToResult(
      expected);

    // test special character searching
    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter))).getResult();
    // DNs returned from JNDI may have escaped characters
    result.getEntry().setDn(result.getEntry().getDn().replaceAll("\\\\", ""));
    AssertJUnit.assertEquals(specialCharsResult, result);
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "rewriteSearchDn",
      "rewriteSearchFilter",
      "rewriteSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void rewriteSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final SearchOperation search = new SearchOperation(
      createLdapConnection(false));
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final LdapResult specialCharsResult = TestUtil.convertLdifToResult(
      expected);
    specialCharsResult.getEntry().setDn(
      specialCharsResult.getEntry().getDn().replaceAll("\\\\", ""));

    // test special character searching
    final SearchRequest request = new SearchRequest(
      dn, new SearchFilter(filter));
    request.setReferralBehavior(ReferralBehavior.IGNORE);
    final LdapResult result = search.execute(request).getResult();
    AssertJUnit.assertEquals(specialCharsResult, result);
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  resultsSize  of search results.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchExceededDn",
      "searchExceededFilter",
      "searchExceededResultsSize"
    }
  )
  @Test(groups = {"search"})
  public void searchExceeded(
    final String dn,
    final String filter,
    final int resultsSize)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();

      final SearchOperation search = new SearchOperation(conn);
      final SearchRequest request = new SearchRequest();
      request.setBaseDn(dn);
      request.setSizeLimit(resultsSize);

      request.setSearchFilter(new SearchFilter("(uugid=*)"));
      Response<LdapResult> response = search.execute(request);
      AssertJUnit.assertEquals(resultsSize, response.getResult().size());
      AssertJUnit.assertEquals(
        ResultCode.SIZE_LIMIT_EXCEEDED, response.getResultCode());

      request.setSearchFilter(new SearchFilter(filter));
      response = search.execute(request);
      AssertJUnit.assertEquals(resultsSize, response.getResult().size());
      AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchReferralDn",
      "searchReferralFilter"
    }
  )
  @Test(groups = {"search"})
  public void searchReferral(final String dn, final String filter)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();

      // expects a referral on dc=vt,dc=edu that points to
      // ou=people,dc=vt,dc=edu
      final SearchOperation search = new SearchOperation(conn);
      final SearchRequest request = new SearchRequest();
      request.setBaseDn(dn);
      request.setSearchScope(SearchScope.ONELEVEL);
      request.setReturnAttributes(new String[0]);
      request.setSearchFilter(new SearchFilter(filter));

      Response<LdapResult> response = null;
      request.setReferralBehavior(ReferralBehavior.FOLLOW);
      try {
        response = search.execute(request);
        AssertJUnit.assertTrue(response.getResult().size() > 0);
        AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());
      } catch (UnsupportedOperationException e) {
        // ignore this test if not supported
        AssertJUnit.assertNotNull(e);
      }

      request.setReferralBehavior(ReferralBehavior.IGNORE);
      response = search.execute(request);
      AssertJUnit.assertTrue(response.getResult().size() > 0);
      AssertJUnit.assertEquals(ResultCode.SUCCESS, response.getResultCode());

      request.setReferralBehavior(ReferralBehavior.THROW);
      try {
        response = search.execute(request);
        AssertJUnit.fail("Should have thrown LdapException");
      } catch (LdapException e) {
        AssertJUnit.assertEquals(ResultCode.REFERRAL, e.getResultCode());
      }
    } finally {
      conn.close();
    }
  }


  /**
   * @param  resultCode  to retry operations on.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("searchRetryResultCode")
  @Test(groups = {"search"})
  public void searchWithRetry(final String resultCode)
    throws Exception
  {
    final ResultCode retryResultCode = ResultCode.valueOf(resultCode);
    final DefaultConnectionFactory cf = new DefaultConnectionFactory(
      TestUtil.readConnectionConfig(null));
    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(
      new ResultCode[] {retryResultCode, });

    Connection conn = cf.getConnection();
    RetrySearchOperation search = new RetrySearchOperation(conn);

    try {
      conn.open();

      // test defaults
      try {
        search.execute(
          new SearchRequest(
            "ou=dne,dc=vt,dc=edu", new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail("Should have thrown LdapException");
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(1, search.getRetryCount());
      AssertJUnit.assertTrue(search.getRunTime() > 0);

      // test no retry
      search.reset();
      search.setOperationRetry(0);

      try {
        search.execute(
          new SearchRequest(
            "ou=dne,dc=vt,dc=edu", new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail("Should have thrown LdapException");
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(0, search.getRetryCount());
      AssertJUnit.assertEquals(0, search.getRunTime());
    } finally {
      conn.close();
    }

    // test no exception
    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(null);
    conn = cf.getConnection();
    search = new RetrySearchOperation(conn);
    search.setOperationRetry(1);

    try {
      conn.open();
      try {
        final Response<LdapResult> response = search.execute(
          new SearchRequest(
            "ou=dne,dc=vt,dc=edu", new SearchFilter("(objectclass=*)")));
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, response.getResultCode());
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(0, search.getRetryCount());
      AssertJUnit.assertEquals(0, search.getRunTime());
    } finally {
      conn.close();
    }

    // test retry count and wait time
    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(
      new ResultCode[] {retryResultCode, });
    conn = cf.getConnection();
    search = new RetrySearchOperation(conn);
    search.setOperationRetry(3);
    search.setOperationRetryWait(1000);

    try {
      conn.open();
      try {
        search.execute(
          new SearchRequest(
            "ou=dne,dc=vt,dc=edu", new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail("Should have thrown LdapException");
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(3, search.getRetryCount());
      AssertJUnit.assertTrue(search.getRunTime() > 0);

      // test backoff interval
      search.reset();
      search.setOperationRetryBackoff(2);
      try {
        search.execute(
          new SearchRequest(
            "ou=dne,dc=vt,dc=edu", new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail("Should have thrown LdapException");
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(3, search.getRetryCount());
      AssertJUnit.assertTrue(search.getRunTime() > 0);

      // test infinite retries
      search.reset();
      search.setStopCount(10);
      search.setOperationRetry(-1);
      try {
        search.execute(
          new SearchRequest(
            "ou=dne,dc=vt,dc=edu", new SearchFilter("(objectclass=*)")));
        AssertJUnit.fail("Should have thrown LdapException");
      } catch (LdapException e) {
        AssertJUnit.assertEquals(
          ResultCode.NO_SUCH_OBJECT, e.getResultCode());
      }
      AssertJUnit.assertEquals(10, search.getRetryCount());
      AssertJUnit.assertTrue(search.getRunTime() > 0);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  returnAttrs  to return from search.
   * @param  results  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "getAttributesDn",
      "getAttributesReturnAttrs",
      "getAttributesResults"
    }
  )
  @Test(
    groups = {"search"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void getAttributes(
    final String dn,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Connection conn = createLdapConnection(false);
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      SearchRequest.newObjectScopeSearchRequest(
        dn, returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertStringToEntry(dn, results), result.getEntry());
  }


  /**
   * @param  dn  to search on.
   * @param  returnAttrs  to return from search.
   * @param  results  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "getAttributesBase64Dn",
      "getAttributesBase64ReturnAttrs",
      "getAttributesBase64Results"
    }
  )
  @Test(groups = {"search"})
  public void getAttributesBase64(
    final String dn,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final SearchRequest request = SearchRequest.newObjectScopeSearchRequest(
        dn, returnAttrs.split("\\|"));
      request.setBinaryAttributes(new String[]{"jpegPhoto"});
      final LdapResult result = search.execute(request).getResult();
      AssertJUnit.assertEquals(
        TestUtil.convertStringToEntry(
          dn, results).getAttribute("jpegPhoto").getStringValue(),
        result.getEntry().getAttribute("jpegPhoto").getStringValue());
    } finally {
      conn.close();
    }
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"search"})
  public void getSaslMechanisms()
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        SearchRequest.newObjectScopeSearchRequest(
          "", new String[] {"supportedSASLMechanisms"})).getResult();
      AssertJUnit.assertTrue(result.getEntry().getAttributes().size() > 0);
    } finally {
      conn.close();
    }
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"search"})
  public void getSupportedControls()
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        SearchRequest.newObjectScopeSearchRequest(
          "", new String[] {"supportedcontrol"})).getResult();
      AssertJUnit.assertTrue(result.getEntry().getAttributes().size() > 0);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterArgs  to replace args in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "digestMd5SearchDn",
      "digestMd5SearchFilter",
      "digestMd5SearchFilterArgs",
      "digestMd5SearchReturnAttrs",
      "digestMd5SearchResults"
    }
  )
  @Test(groups = {"search"})
  public void digestMd5Search(
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final Connection conn = TestUtil.createDigestMd5Connection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterArgs.split("\\|")),
          returnAttrs.split("\\|"))).getResult();
      AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterArgs  to replace args in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "cramMd5SearchDn",
      "cramMd5SearchFilter",
      "cramMd5SearchFilterArgs",
      "cramMd5SearchReturnAttrs",
      "cramMd5SearchResults"
    }
  )
  @Test(groups = {"search"})
  public void cramMd5Search(
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final Connection conn = TestUtil.createCramMd5Connection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterArgs.split("\\|")),
          returnAttrs.split("\\|"))).getResult();
      AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterArgs  to replace args in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "saslExternalSearchDn",
      "saslExternalSearchFilter",
      "saslExternalSearchFilterArgs",
      "saslExternalSearchReturnAttrs",
      "saslExternalSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void saslExternalSearch(
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtil.readFileIntoString(ldifFile);
    final Connection conn = TestUtil.createSaslExternalConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterArgs.split("\\|")),
          returnAttrs.split("\\|"))).getResult();
      AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    } finally {
      conn.close();
    }
  }


  /**
   * @param  krb5Realm  kerberos realm
   * @param  krb5Kdc  kerberos kdc
   * @param  dn  to search on.
   * @param  filter  to search with.
   * @param  filterArgs  to replace args in filter with.
   * @param  returnAttrs  to return from search.
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "krb5Realm",
      "ldapTestHost",
      "gssApiSearchDn",
      "gssApiSearchFilter",
      "gssApiSearchFilterArgs",
      "gssApiSearchReturnAttrs",
      "gssApiSearchResults"
    }
  )
  @Test(groups = {"search"})
  public void gssApiSearch(
    final String krb5Realm,
    final String krb5Kdc,
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    System.setProperty(
      "java.security.auth.login.config",
      "target/test-classes/ldap_jaas.config");
    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
    System.setProperty("java.security.krb5.realm", krb5Realm);
    System.setProperty(
      "java.security.krb5.kdc", TestUtil.getHostFromLdapUrl(krb5Kdc));

    final String expected = TestUtil.readFileIntoString(ldifFile);

    final Connection conn = TestUtil.createGssApiConnection();
    try {
      conn.open();
      final SearchOperation search = new SearchOperation(conn);
      final LdapResult result = search.execute(
        new SearchRequest(
          dn,
          new SearchFilter(filter, filterArgs.split("\\|")),
          returnAttrs.split("\\|"))).getResult();
      AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    } finally {
      System.clearProperty("java.security.auth.login.config");
      System.clearProperty("javax.security.auth.useSubjectCredsOnly");
      System.clearProperty("java.security.krb5.realm");
      System.clearProperty("java.security.krb5.kdc");
      conn.close();
    }
  }
}
