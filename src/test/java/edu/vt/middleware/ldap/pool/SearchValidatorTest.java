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
package edu.vt.middleware.ldap.pool;

import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.TestUtil;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

/**
 * Test for {@link SearchValidator}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SearchValidatorTest extends AbstractTest
{


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"validator"})
  public void defaultSettings()
    throws Exception
  {
    final Connection c = TestUtil.createConnection();
    c.open();
    final SearchValidator sv = new SearchValidator();
    AssertJUnit.assertTrue(sv.validate(c));
    c.close();
    AssertJUnit.assertFalse(sv.validate(c));
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"validator"})
  public void customSettings()
    throws Exception
  {
    final Connection c = TestUtil.createConnection();
    c.open();
    final SearchValidator sv = new SearchValidator(
      new SearchRequest("ou=test,dc=vt,dc=edu", new SearchFilter("uid=*")));
    AssertJUnit.assertTrue(sv.validate(c));
    sv.getSearchRequest().setSearchFilter(new SearchFilter("dne=*"));
    AssertJUnit.assertFalse(sv.validate(c));
    c.close();
    AssertJUnit.assertFalse(sv.validate(c));
  }
}
