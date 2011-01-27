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
package edu.vt.middleware.ldap.dsml;

import java.io.StringReader;
import java.io.StringWriter;
import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.SortBehavior;
import edu.vt.middleware.ldap.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Dsmlv1} and {@link Dsmlv2}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DsmlTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "createEntry13" })
  @BeforeClass(groups = {"dsmltest"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"dsmltest"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "dsmlSearchDn",
      "dsmlSearchFilter"
    })
  @Test(groups = {"dsmltest"})
  public void searchAndCompareDsmlv1(final String dn, final String filter)
    throws Exception
  {
    final LdapConnection conn = TestUtil.createLdapConnection();
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final Dsmlv1 dsml = new Dsmlv1();

    final LdapResult result1 = search.execute(
      new SearchRequest(dn, new SearchFilter(filter))).getResult();

    final StringWriter writer = new StringWriter();
    dsml.outputDsml(result1, writer);

    final StringReader reader = new StringReader(writer.toString());
    final LdapResult result2 = dsml.importDsml(reader);

    AssertJUnit.assertEquals(result1, result2);
    conn.close();
  }


  /**
   * @param  dsmlFile  to test with.
   * @param  dsmlSortedFile  to test with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "dsmlv1Entry",
      "dsmlv1SortedEntry"
    })
  @Test(groups = {"dsmltest"})
  public void readAndCompareDsmlv1(
    final String dsmlFile,
    final String dsmlSortedFile)
    throws Exception
  {
    final Dsmlv1 dsml = new Dsmlv1();
    dsml.setSortBehavior(SortBehavior.SORTED);

    final String dsmlStringSorted = TestUtil.readFileIntoString(dsmlSortedFile);
    final LdapResult result = dsml.importDsml(
      new StringReader(TestUtil.readFileIntoString(dsmlFile)));
    final StringWriter writer = new StringWriter();
    dsml.outputDsml(result, writer);

    AssertJUnit.assertEquals(dsmlStringSorted, writer.toString());
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "dsmlSearchDn",
      "dsmlSearchFilter"
    })
  @Test(groups = {"dsmltest"})
  public void searchAndCompareDsmlv2(final String dn, final String filter)
    throws Exception
  {
    final LdapConnection conn = TestUtil.createLdapConnection();
    conn.open();
    final SearchOperation search = new SearchOperation(conn);
    final Dsmlv2 dsml = new Dsmlv2();

    final LdapResult result1 = search.execute(
      new SearchRequest(dn, new SearchFilter(filter))).getResult();

    final StringWriter writer = new StringWriter();
    dsml.outputDsml(result1, writer);

    final StringReader reader = new StringReader(writer.toString());
    final LdapResult result2 = dsml.importDsml(reader);

    AssertJUnit.assertEquals(result1, result2);
    conn.close();
  }


  /**
   * @param  dsmlFile  to test with.
   * @param  dsmlSortedFile  to test with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "dsmlv2Entry",
      "dsmlv2SortedEntry"
    })
  @Test(groups = {"dsmltest"})
  public void readAndCompareDsmlv2(
    final String dsmlFile,
    final String dsmlSortedFile)
    throws Exception
  {
    final Dsmlv2 dsml = new Dsmlv2();
    dsml.setSortBehavior(SortBehavior.SORTED);

    final String dsmlStringSorted = TestUtil.readFileIntoString(dsmlSortedFile);
    final LdapResult result = dsml.importDsml(
      new StringReader(TestUtil.readFileIntoString(dsmlFile)));
    final StringWriter writer = new StringWriter();
    dsml.outputDsml(result, writer);

    AssertJUnit.assertEquals(dsmlStringSorted, writer.toString());
  }
}
