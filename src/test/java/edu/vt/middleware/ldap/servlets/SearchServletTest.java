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
package edu.vt.middleware.ldap.servlets;

import java.io.File;
import java.io.StringWriter;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.dsml.Dsmlv1Writer;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SearchServlet}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SearchServletTest extends AbstractTest
{

  /** Entry created for tests. */
  private static LdapEntry testLdapEntry;

  /** To test servlets with. */
  private ServletRunner ldifServletRunner;

  /** To test servlets with. */
  private ServletRunner dsmlServletRunner;


  /**
   * @param  ldifFile  to create.
   * @param  webXml  web.xml for queries
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "createEntry11", "webXml" })
  @BeforeClass(groups = {"servlet"})
  public void createLdapEntry(final String ldifFile, final String webXml)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);

    ldifServletRunner = new ServletRunner(new File(webXml));
    dsmlServletRunner = new ServletRunner(new File(webXml));
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"servlet"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  query  to search for.
   * @param  attrs  attributes to return from search
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "ldifSearchServletQuery",
      "ldifSearchServletAttrs",
      "ldifSearchServletLdif"
    }
  )
  @Test(groups = {"servlet"})
  public void ldifSearchServlet(
    final String query,
    final String attrs,
    final String ldifFile)
    throws Exception
  {
    final String expected = TestUtil.readFileIntoString(ldifFile);

    final ServletUnitClient sc = ldifServletRunner.newClient();
    final WebRequest request = new PostMethodWebRequest(
      "http://servlets.ldap.middleware.vt.edu/LdifSearch");
    request.setParameter("query", query);
    request.setParameter("attrs", attrs.split("\\|"));

    final WebResponse response = sc.getResponse(request);

    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals("text/plain", response.getContentType());

    final LdapResult result = TestUtil.convertLdifToResult(response.getText());
    AssertJUnit.assertEquals(TestUtil.convertLdifToResult(expected), result);
  }


  /**
   * @param  query  to search for.
   * @param  attrs  attributes to return from search
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "dsmlSearchServletQuery",
      "dsmlSearchServletAttrs",
      "dsmlSearchServletLdif"
    }
  )
  @Test(groups = {"servlet"})
  public void dsmlSearchServlet(
    final String query,
    final String attrs,
    final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    final LdapResult result = TestUtil.convertLdifToResult(ldif);
    // convert ldif into dsmlv1
    final StringWriter s1w = new StringWriter();
    final Dsmlv1Writer d1w = new Dsmlv1Writer(s1w);
    d1w.write(result);
    final String dsmlv1 = s1w.toString();

    final ServletUnitClient sc = dsmlServletRunner.newClient();
    // test basic dsml query
    WebRequest request = new PostMethodWebRequest(
      "http://servlets.ldap.middleware.vt.edu/DsmlSearch");
    request.setParameter("query", query);
    request.setParameter("attrs", attrs.split("\\|"));

    WebResponse response = sc.getResponse(request);

    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals("text/xml", response.getContentType());
    AssertJUnit.assertEquals(dsmlv1, response.getText());

    // test plain text
    request = new PostMethodWebRequest(
      "http://servlets.ldap.middleware.vt.edu/DsmlSearch");
    request.setParameter("content-type", "text");
    request.setParameter("query", query);
    request.setParameter("attrs", attrs.split("\\|"));
    response = sc.getResponse(request);

    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals("text/plain", response.getContentType());
    AssertJUnit.assertEquals(dsmlv1, response.getText());
  }


  /** @throws  Exception  On test failure. */
  @Test(
    groups = {"servlet"},
    dependsOnMethods = {"ldifSearchServlet", "dsmlSearchServlet"}
  )
  public void prunePools()
    throws Exception
  {
    Thread.sleep(10000);
  }
}
