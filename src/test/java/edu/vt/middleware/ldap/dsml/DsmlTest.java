/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.dsml;

import java.io.StringReader;
import java.util.Iterator;
import javax.naming.directory.SearchResult;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
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
public class DsmlTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "createEntry11" })
  @BeforeClass(groups = {"dsmltest"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToEntry(ldif);

    Ldap ldap = TestUtil.createSetupLdap();
    ldap.create(
      testLdapEntry.getDn(),
      testLdapEntry.getLdapAttributes().toAttributes());
    ldap.close();
    ldap = TestUtil.createLdap();
    while (
      !ldap.compare(
          testLdapEntry.getDn(),
          new SearchFilter(testLdapEntry.getDn().split(",")[0]))) {
      Thread.sleep(100);
    }
    ldap.close();
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"dsmltest"})
  public void deleteLdapEntry()
    throws Exception
  {
    final Ldap ldap = TestUtil.createSetupLdap();
    ldap.delete(testLdapEntry.getDn());
    ldap.close();
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "dsmlSearchDn",
      "dsmlSearchFilter"
    }
  )
  @Test(groups = {"dsmltest"})
  public void createDsmlv1(final String dn, final String filter)
    throws Exception
  {
    final Ldap ldap = TestUtil.createLdap();
    final Dsmlv1 dsml = new Dsmlv1();

    final Iterator<SearchResult> iter = ldap.search(
      dn,
      new SearchFilter(filter));

    final LdapResult result1 = new LdapResult(iter);
    final String s = dsml.outputDsmlToString(result1.toSearchResults().iterator());
    final LdapResult result2 = new LdapResult(
      dsml.createSearchResults(new StringReader(s)));

    AssertJUnit.assertEquals(result1, result2);

    ldap.close();
  }


  /**
   * @param  dn  to search on.
   * @param  filter  to search with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "dsmlSearchDn",
      "dsmlSearchFilter"
    }
  )
  @Test(groups = {"dsmltest"})
  public void createDsmlv2(final String dn, final String filter)
    throws Exception
  {
    final Ldap ldap = TestUtil.createLdap();
    final Dsmlv2 dsml = new Dsmlv2();

    final Iterator<SearchResult> iter = ldap.search(
      dn,
      new SearchFilter(filter));

    final LdapResult result1 = new LdapResult(iter);
    final String s = dsml.outputDsmlToString(result1.toSearchResults().iterator());
    final LdapResult result2 = new LdapResult(
      dsml.createSearchResults(new StringReader(s)));

    AssertJUnit.assertEquals(result1, result2);

    ldap.close();
  }
}
