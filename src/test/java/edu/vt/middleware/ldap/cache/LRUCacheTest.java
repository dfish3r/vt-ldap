/*
  $Id: cachetest.java 1858 2011-03-03 22:35:03Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1858 $
  Updated: $Date: 2011-03-03 17:35:03 -0500 (Thu, 03 Mar 2011) $
*/
package edu.vt.middleware.ldap.cache;

import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchRequest;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test for {@link LRUCache}.
 *
 * @author  Middleware Services
 * @version  $Revision: 1858 $
 */
public class LRUCacheTest
{

  /** Cache for testing. */
  private LRUCache<SearchRequest> cache =
    new LRUCache<SearchRequest>(5, 15, 3);


  /**
   * @throws  Exception  On test failure.
   */
  @BeforeClass(groups = {"cachetest"})
  public void initialize()
    throws Exception
  {
    this.fillCache();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"cachetest"})
  public void clear()
    throws Exception
  {
    this.fillCache();
    AssertJUnit.assertEquals(5, this.cache.size());
    this.cache.clear();
    AssertJUnit.assertEquals(0, this.cache.size());
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"cachetest"},
    threadPoolSize = 5,
    invocationCount = 100,
    timeOut = 60000
  )
  public void get()
    throws Exception
  {
    LdapResult lr = this.cache.get(
      new SearchRequest(new SearchFilter("uid=3")));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=3,ou=test,dc=vt,dc=edu")), lr);
    lr = this.cache.get(new SearchRequest(new SearchFilter("uid=4")));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=4,ou=test,dc=vt,dc=edu")), lr);
    lr = this.cache.get(new SearchRequest(new SearchFilter("uid=5")));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=5,ou=test,dc=vt,dc=edu")), lr);
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"cachetest"})
  public void put()
    throws Exception
  {
    AssertJUnit.assertEquals(5, this.cache.size());
    this.cache.put(
      new SearchRequest(new SearchFilter("uid=%s", new Object[]{"101"})),
      new LdapResult(new LdapEntry("uid=101,ou=test,dc=vt,dc=edu")));
    this.cache.put(
      new SearchRequest(new SearchFilter("uid=102")),
      new LdapResult(new LdapEntry("uid=102,ou=test,dc=vt,dc=edu")));
    AssertJUnit.assertEquals(5, this.cache.size());

    LdapResult lr = this.cache.get(
      new SearchRequest(new SearchFilter("uid=%s", new Object[]{"101"})));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=101,ou=test,dc=vt,dc=edu")), lr);
    lr = this.cache.get(
      new SearchRequest(new SearchFilter("uid=102")));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=102,ou=test,dc=vt,dc=edu")), lr);
    AssertJUnit.assertNull(
      this.cache.get(new SearchRequest(new SearchFilter("uid=1"))));
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"cachetest"}, dependsOnMethods = {"get", "put"})
  public void interval()
    throws Exception
  {
    AssertJUnit.assertEquals(5, this.cache.size());
    Thread.sleep(20000);
    AssertJUnit.assertEquals(0, this.cache.size());
  }


  /**
   * Fills the cache with data.
   */
  private void fillCache()
  {
    this.cache.put(
      new SearchRequest(new SearchFilter("uid=1")),
      new LdapResult(new LdapEntry("uid=1,ou=test,dc=vt,dc=edu")));
    this.cache.put(
      new SearchRequest(new SearchFilter("uid=2")),
      new LdapResult(new LdapEntry("uid=2,ou=test,dc=vt,dc=edu")));
    this.cache.put(
      new SearchRequest(new SearchFilter("uid=3")),
      new LdapResult(new LdapEntry("uid=3,ou=test,dc=vt,dc=edu")));
    this.cache.put(
      new SearchRequest(new SearchFilter("uid=4")),
      new LdapResult(new LdapEntry("uid=4,ou=test,dc=vt,dc=edu")));
    this.cache.put(
      new SearchRequest(new SearchFilter("uid=5")),
      new LdapResult(new LdapEntry("uid=5,ou=test,dc=vt,dc=edu")));
  }
}
