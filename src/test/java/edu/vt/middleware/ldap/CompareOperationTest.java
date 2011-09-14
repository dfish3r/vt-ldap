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
 * Unit test for {@link CompareOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1633 $
 */
public class CompareOperationTest extends AbstractTest
{

  /** Entry created for ldap tests. */
  private static LdapEntry testLdapEntry;


  /**
   * @param  ldifFile  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("createEntry3")
  @BeforeClass(groups = {"compare"})
  public void createLdapEntry(final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtil.readFileIntoString(ldifFile);
    testLdapEntry = TestUtil.convertLdifToResult(ldif).getEntry();
    super.createLdapEntry(testLdapEntry);
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(groups = {"compare"})
  public void deleteLdapEntry()
    throws Exception
  {
    super.deleteLdapEntry(testLdapEntry.getDn());
  }


  /**
   * @param  dn  to compare.
   * @param  attrName  to compare with.
   * @param  attrValue  to compare with.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters({ "compareDn", "compareAttrName", "compareAttrValue" })
  @Test(
    groups = {"compare"},
    threadPoolSize = TEST_THREAD_POOL_SIZE,
    invocationCount = TEST_INVOCATION_COUNT,
    timeOut = TEST_TIME_OUT
  )
  public void compare(
    final String dn,
    final String attrName,
    final String attrValue)
    throws Exception
  {
    final Connection conn = TestUtil.createConnection();
    conn.open();
    final CompareOperation compare = new CompareOperation(conn);
    LdapAttribute la = new LdapAttribute();
    la.setName("cn");
    la.addStringValue("not-a-name");
    AssertJUnit.assertFalse(
      compare.execute(new CompareRequest(dn, la)).getResult());

    la = new LdapAttribute();
    la.setName(attrName);
    la.addStringValue(attrValue);
    AssertJUnit.assertTrue(
      compare.execute(new CompareRequest(dn, la)).getResult());
    conn.close();
  }
}
