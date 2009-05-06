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
package edu.vt.middleware.ldap.servlets;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.bean.LdapEntry;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link AttributeServlet}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class AttributeServletTest
{

  /** Entry created for tests. */
  private static LdapEntry testLdapEntry;

  /** To test servlets with. */
  private ServletRunner servletRunner;


  /**
   * @param  ldifFile  to create.
   * @param  webXml  web.xml for queries
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "createEntry9", "webXml" })
  @BeforeClass(groups = {"servlettest"})
  public void createLdapEntry(final String ldifFile, final String webXml)
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
          testLdapEntry.getDn().split(",")[0])) {
      Thread.currentThread().sleep(100);
    }
    ldap.close();

    this.servletRunner = new ServletRunner(webXml);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"servlettest"})
  public void deleteLdapEntry()
    throws Exception
  {
    final Ldap ldap = TestUtil.createSetupLdap();
    ldap.delete(testLdapEntry.getDn());
    ldap.close();
  }


  /**
   * @param  query  to search for.
   * @param  attr  attribute to return from search
   * @param  attributeValue  to compare
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "attributeServletQuery",
      "attributeServletAttr",
      "attributeServletValue"
    }
  )
  @Test(groups = {"servlettest"})
  public void attributeServlet(
    final String query,
    final String attr,
    final String attributeValue)
    throws Exception
  {
    final ServletUnitClient sc = this.servletRunner.newClient();
    final WebRequest request = new PostMethodWebRequest(
      "http://servlets.ldap.middleware.vt.edu/AttributeSearch");
    request.setParameter("query", query);
    request.setParameter("attr", attr);
    request.setParameter("content-type", "octet");

    final WebResponse response = sc.getResponse(request);

    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals(
      "application/octet-stream",
      response.getContentType());
    AssertJUnit.assertEquals(
      "attachment; filename=\"" + attr + ".bin\"",
      response.getHeaderField("Content-Disposition"));
    AssertJUnit.assertEquals(
      attributeValue,
      LdapUtil.base64Encode(response.getText().getBytes()));
  }


  /** @throws  Exception  On test failure. */
  @Test(
    groups = {"servlettest"},
    dependsOnMethods = {"attributeServlet"}
  )
  public void prunePools()
    throws Exception
  {
    Thread.currentThread().sleep(10000);
  }
}
