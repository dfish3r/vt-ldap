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
import edu.vt.middleware.ldap.control.SortControl;
import edu.vt.middleware.ldap.control.SortKey;
import edu.vt.middleware.ldap.handler.CaseChangeResultHandler;
import edu.vt.middleware.ldap.handler.CaseChangeResultHandler.CaseChange;
import edu.vt.middleware.ldap.handler.CopyLdapResultHandler;
import edu.vt.middleware.ldap.handler.DnAttributeResultHandler;
import edu.vt.middleware.ldap.handler.LdapAttributeHandler;
import edu.vt.middleware.ldap.handler.LdapResultHandler;
import edu.vt.middleware.ldap.handler.MergeAttributeResultHandler;
import edu.vt.middleware.ldap.handler.MergeResultHandler;
import edu.vt.middleware.ldap.handler.RecursiveAttributeHandler;
import edu.vt.middleware.ldap.handler.RecursiveResultHandler;
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
  @BeforeClass(groups = {"searchtest"})
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
  @BeforeClass(groups = {"searchtest"})
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
  @BeforeClass(groups = {"searchtest"})
  public void createGroupEntry(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4,
    final String ldifFile5)
    throws Exception
  {
    groupEntries.get("2")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile2)).getEntry();
    groupEntries.get("3")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile3)).getEntry();
    groupEntries.get("4")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile4)).getEntry();
    groupEntries.get("5")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile5)).getEntry();

    for (Map.Entry<String, LdapEntry[]> e : groupEntries.entrySet()) {
      super.createLdapEntry(e.getValue()[0]);
    }

    // setup group relationships
    final Connection conn = TestUtil.createSetupConnection();
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
    conn.close();
  }


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createSpecialCharsEntry")
  @BeforeClass(groups = {"searchtest"})
  public void createSpecialCharsEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    specialCharsLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(specialCharsLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"searchtest"})
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
    groups = {"searchtest"},
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
        new LdapResultHandler[0])).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);

    // test searching with multiple handlers
    final DnAttributeResultHandler srh = new DnAttributeResultHandler();
    result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"),
        new LdapResultHandler[]{
          new CopyLdapResultHandler(), srh, })).getResult();
    AssertJUnit.assertEquals(entryDnResult, result);

    // test that entry dn handler is no-op if attribute name conflicts
    srh.setDnAttributeName("givenName");
    result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"),
        new LdapResultHandler[]{
          new CopyLdapResultHandler(), srh, })).getResult();
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
  @Test(groups = {"searchtest"})
  public void pagedSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final String expected = TestUtil.readFileIntoString(ldifFile);

    // test searching
    final SearchRequest request = new SearchRequest(
      dn, new SearchFilter(filter));
    request.setPagedResultsControl(new PagedResultsControl(1, true));
    final LdapResult result = search.execute(request).getResult();
    AssertJUnit.assertEquals(
      TestUtil.convertLdifToResult(expected), result);

    conn.close();
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
  @Test(groups = {"searchtest"})
  public void sortedSearch(
    final String dn,
    final String filter)
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final SearchOperation search = new SearchOperation(conn);

    // test searching
    final SearchRequest request = new SearchRequest(
      dn, new SearchFilter(filter));
    request.setSortBehavior(SortBehavior.ORDERED);
    request.setSortControl(
      new SortControl(
        new SortKey[] {new SortKey("uid", "integerMatch", true)}, true));
    final LdapResult result = search.execute(request).getResult();

    // confirm sorted
    int i = 5;
    for (LdapEntry e : result.getEntries()) {
      AssertJUnit.assertEquals(
        String.valueOf(2000 + i), e.getAttribute("uid").getStringValue());
      i--;
    }

    conn.close();
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
      "recursiveAttributeHandlerResults"
    }
  )
  @Test(groups = {"searchtest"})
  public void recursiveAttributeHandlerSearch(
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
    final CopyLdapResultHandler handler = new CopyLdapResultHandler();
    handler.setAttributeHandler(
      new LdapAttributeHandler[] {new RecursiveAttributeHandler("member")});

    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        null,
        new LdapResultHandler[]{handler})).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
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
      "recursiveSearchResultHandlerResults"
    }
  )
  @Test(groups = {"searchtest"})
  public void recursiveSearchResultHandlerSearch(
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
    final RecursiveResultHandler rsrh = new RecursiveResultHandler(
      "member",
      new String[] {"uugid", "uid"});

    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        null,
        new LdapResultHandler[]{rsrh})).getResult();
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
  @Test(groups = {"searchtest"})
  public void mergeSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    conn.open();
    final SearchOperation search = new SearchOperation(conn);

    final String expected = TestUtil.readFileIntoString(ldifFile);

    // test merge searching
    final MergeResultHandler handler = new MergeResultHandler();

    final SearchRequest sr = new SearchRequest(
      dn,
      new SearchFilter(filter),
      null,
      new LdapResultHandler[]{handler});
    sr.setSortBehavior(SortBehavior.SORTED);
    final LdapResult result = search.execute(sr).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    conn.close();
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
  @Test(groups = {"searchtest"})
  public void mergeDuplicateSearch(
    final String dn,
    final String filter,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    conn.open();
    final SearchOperation search = new SearchOperation(conn);

    final String expected = TestUtil.readFileIntoString(ldifFile);

    // test merge searching
    final MergeResultHandler handler = new MergeResultHandler();

    final SearchRequest sr = new SearchRequest(
      dn,
      new SearchFilter(filter),
      null,
      new LdapResultHandler[]{handler});
    sr.setSortBehavior(SortBehavior.SORTED);
    final LdapResult result = search.execute(sr).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    conn.close();
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
  @Test(groups = {"searchtest"})
  public void mergeAttributeSearch(
    final String dn,
    final String filter,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    conn.open();
    final SearchOperation search = new SearchOperation(conn);

    final String expected = TestUtil.readFileIntoString(ldifFile);

    // test merge searching
    final MergeAttributeResultHandler handler =
      new MergeAttributeResultHandler();
    handler.setMergeAttributeName("cn");
    handler.setAttributeNames(
      new String[] {"displayName", "givenName", "sn", });

    final SearchRequest sr = new SearchRequest(
      dn,
      new SearchFilter(filter),
      returnAttrs.split("\\|"),
      new LdapResultHandler[]{handler});
    sr.setSortBehavior(SortBehavior.SORTED);
    final LdapResult result = search.execute(sr).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    conn.close();
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
  @Test(groups = {"searchtest"})
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
  @Test(groups = {"searchtest"})
  public void caseChangeSearch(
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final CaseChangeResultHandler srh =
      new CaseChangeResultHandler();
    final String expected = TestUtil.readFileIntoString(ldifFile);

    // test no case change
    final LdapResult noChangeResult = TestUtil.convertLdifToResult(expected);
    LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"),
        new LdapResultHandler[]{srh})).getResult();
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
        new LdapResultHandler[]{srh})).getResult();
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
        new LdapResultHandler[]{srh})).getResult();
    AssertJUnit.assertEquals(ucNamesChangeResult, result);

    // test lower case everything
    srh.setAttributeValueCaseChange(CaseChange.LOWER);
    srh.setAttributeNameCaseChange(CaseChange.LOWER);
    srh.setDnCaseChange(CaseChange.LOWER);
    final LdapResult lcAllChangeResult = TestUtil.convertLdifToResult(expected);
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
        new LdapResultHandler[]{srh})).getResult();
    AssertJUnit.assertEquals(ucNamesChangeResult, result);

    conn.close();
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
  @Test(groups = {"searchtest"})
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
  @Test(groups = {"searchtest"})
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
    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter))).getResult();
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
      "searchExceptionDn",
      "searchExceptionFilter",
      "searchExceptionResultsSize"
    }
  )
  @Test(groups = {"searchtest"})
  public void searchWithException(
    final String dn,
    final String filter,
    final int resultsSize)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final SearchRequest request = new SearchRequest();

    // test exception searching
    request.setBaseDn(dn);
    request.setSizeLimit(resultsSize);
    request.setSearchIgnoreResultCodes(null);

    request.setSearchFilter(new SearchFilter("(uugid=*)"));
    try {
      search.execute(request);
      AssertJUnit.fail("Should have thrown SizeLimitExceededException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(
        ResultCode.SIZE_LIMIT_EXCEEDED, e.getResultCode());
    }

    request.setSearchIgnoreResultCodes(
      new ResultCode[] {ResultCode.TIME_LIMIT_EXCEEDED, });
    try {
      search.execute(request);
      AssertJUnit.fail("Should have thrown SizeLimitExceededException");
    } catch (LdapException e) {
      AssertJUnit.assertEquals(
        ResultCode.SIZE_LIMIT_EXCEEDED, e.getResultCode());
    }

    request.setSearchIgnoreResultCodes(
      new ResultCode[] {
        ResultCode.TIME_LIMIT_EXCEEDED, ResultCode.SIZE_LIMIT_EXCEEDED, });
    request.setSearchFilter(new SearchFilter(filter));

    final LdapResult result = search.execute(request).getResult();
    AssertJUnit.assertEquals(resultsSize, result.size());
    conn.close();
  }


  /**
   * @param  resultCode  to retry operations on.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("searchRetryResultCode")
  @Test(groups = {"searchtest"})
  public void searchWithRetry(final String resultCode)
    throws Exception
  {
    final ResultCode retryResultCode = ResultCode.valueOf(resultCode);
    final ConnectionFactory cf = new ConnectionFactory(
      TestUtil.readConnectionConfig(null));
    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(
      new ResultCode[] {retryResultCode, });

    Connection conn = cf.getConnection();

    conn.open();
    RetrySearchOperation search = new RetrySearchOperation(conn);

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

    // test no exception
    conn.close();

    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(null);
    conn = cf.getConnection();
    search = new RetrySearchOperation(conn);
    search.setOperationRetry(1);

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
    AssertJUnit.assertEquals(0, search.getRetryCount());
    AssertJUnit.assertEquals(0, search.getRunTime());

    // test retry count and wait time
    conn.close();

    cf.getProvider().getProviderConfig().setOperationRetryResultCodes(
      new ResultCode[] {retryResultCode, });
    conn = cf.getConnection();
    search = new RetrySearchOperation(conn);
    search.setOperationRetry(3);
    search.setOperationRetryWait(1000);

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
    conn.close();
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
    groups = {"searchtest"},
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
  @Test(groups = {"searchtest"})
  public void getAttributesBase64(
    final String dn,
    final String returnAttrs,
    final String results)
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
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
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"searchtest"})
  public void getSaslMechanisms()
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      SearchRequest.newObjectScopeSearchRequest(
        "", new String[] {"supportedSASLMechanisms"})).getResult();
    AssertJUnit.assertTrue(result.getEntry().getAttributes().size() > 0);
    conn.close();
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"searchtest"})
  public void getSupportedControls()
    throws Exception
  {
    final Connection conn = createLdapConnection(true);
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      SearchRequest.newObjectScopeSearchRequest(
        "", new String[] {"supportedcontrol"})).getResult();
    AssertJUnit.assertTrue(result.getEntry().getAttributes().size() > 0);
    conn.close();
  }


  /**
   * @param  user  to bind as
   * @param  credential  to bind with
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
      "digestMd5User",
      "digestMd5Credential",
      "digestMd5SearchDn",
      "digestMd5SearchFilter",
      "digestMd5SearchFilterArgs",
      "digestMd5SearchReturnAttrs",
      "digestMd5SearchResults"
    }
  )
  @Test(groups = {"searchtest"})
  public void digestMd5Search(
    final String user,
    final String credential,
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtil.readFileIntoString(ldifFile);

    final Connection conn = TestUtil.createDigestMd5Connection();
    conn.getConnectionConfig().setBindDn(user);
    conn.getConnectionConfig().setBindCredential(new Credential(credential));
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    conn.close();
  }


  /**
   * @param  user  to bind as
   * @param  credential  to bind with
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
      "cramMd5User",
      "cramMd5Credential",
      "cramMd5SearchDn",
      "cramMd5SearchFilter",
      "cramMd5SearchFilterArgs",
      "cramMd5SearchReturnAttrs",
      "cramMd5SearchResults"
    }
  )
  @Test(groups = {"searchtest"})
  public void cramMd5Search(
    final String user,
    final String credential,
    final String dn,
    final String filter,
    final String filterArgs,
    final String returnAttrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtil.readFileIntoString(ldifFile);

    final Connection conn = TestUtil.createCramMd5Connection();
    conn.getConnectionConfig().setBindDn(user);
    conn.getConnectionConfig().setBindCredential(new Credential(credential));
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    conn.close();
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
  @Test(groups = {"searchtest"})
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
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    conn.close();
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
      "krb5Kdc",
      "gssApiSearchDn",
      "gssApiSearchFilter",
      "gssApiSearchFilterArgs",
      "gssApiSearchReturnAttrs",
      "gssApiSearchResults"
    }
  )
  @Test(groups = {"searchtest"})
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
    System.setProperty("java.security.krb5.kdc", krb5Kdc);

    final String expected = TestUtil.readFileIntoString(ldifFile);

    final Connection conn = TestUtil.createGssApiConnection();
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final LdapResult result = search.execute(
      new SearchRequest(
        dn,
        new SearchFilter(filter, filterArgs.split("\\|")),
        returnAttrs.split("\\|"))).getResult();
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
    conn.close();

    System.clearProperty("java.security.auth.login.config");
    System.clearProperty("javax.security.auth.useSubjectCredsOnly");
    System.clearProperty("java.security.krb5.realm");
    System.clearProperty("java.security.krb5.kdc");
  }
}
