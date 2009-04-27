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
package edu.vt.middleware.ldap.servlets.session;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.bean.LdapEntry;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SessionManager}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SessionManagerTest
{

  /** Entry created for tests */
  private static LdapEntry testLdapEntry;

  /** To test servlets with */
  private ServletRunner servletRunner;


  /**
   * @param  ldifFile  to create.
   * @param  webXml  web.xml for queries
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "createEntry10", "webXml" })
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
   * @param  user  to authenticate
   * @param  password  to authenticate with
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({
      "sessionManagerUser",
      "sessionManagerPassword"
    })
  @Test(groups = {"servlettest"})
  public void login(final String user, final String password)
    throws Exception
  {
    System.setProperty(
      "javax.net.ssl.trustStore",
      "src/test/resources/ed.truststore");
    System.setProperty("javax.net.ssl.trustStoreType", "BKS");
    System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

    final ServletUnitClient sc = this.servletRunner.newClient();
    // login
    WebRequest request = new PostMethodWebRequest(
      "http://servlets.ldap.middleware.vt.edu/Login");
    request.setParameter("user", user);
    request.setParameter("credential", password);
    request.setParameter(
      "url",
      "http://servlets.ldap.middleware.vt.edu/SessionCheck");

    WebResponse response = sc.getResponse(request);

    AssertJUnit.assertNotNull(response);

    final String[] sessionData = response.getText().trim().split(":");
    AssertJUnit.assertEquals(user, sessionData[1]);

    // logout
    request = new GetMethodWebRequest(
      "http://servlets.ldap.middleware.vt.edu/Logout");
    request.setParameter(
      "url",
      "http://servlets.ldap.middleware.vt.edu/SessionCheck");
    response = sc.getResponse(request);

    AssertJUnit.assertNotNull(response);
    AssertJUnit.assertEquals("", response.getText());

    System.clearProperty("javax.net.ssl.trustStore");
    System.clearProperty("javax.net.ssl.trustStoreType");
    System.clearProperty("javax.net.ssl.trustStorePassword");
  }
}
