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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.ldap.AbstractTest;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.ldif.LdifWriter;
import edu.vt.middleware.ldap.provider.ConnectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Load test for connection pools.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class ConnectionPoolTest extends AbstractTest
{

  /** Entries for pool tests. */
  private static Map<String, LdapEntry[]> entries =
    new HashMap<String, LdapEntry[]>();

  /**
   * Initialize the map of entries.
   */
  static {
    for (int i = 2; i <= 10; i++) {
      entries.put(String.valueOf(i), new LdapEntry[2]);
    }
  }

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** LdapPool instance for concurrency testing. */
  private SoftLimitConnectionPool softLimitPool;

  /** LdapPool instance for concurrency testing. */
  private BlockingConnectionPool blockingPool;

  /** LdapPool instance for concurrency testing. */
  private BlockingConnectionPool blockingTimeoutPool;

  /** LdapPool instance for concurrency testing. */
  private BlockingConnectionPool connStrategyPool;

  /** Time in millis it takes the pool test to run. */
  private long softLimitRuntime;

  /** Time in millis it takes the pool test to run. */
  private long blockingRuntime;

  /** Time in millis it takes the pool test to run. */
  private long blockingTimeoutRuntime;


  /**
   * Default constructor.
   *
   * @throws  Exception  On test failure.
   */
  public ConnectionPoolTest()
    throws Exception
  {
    final ConnectionConfig cc =
      TestUtil.createConnection().getConnectionConfig();

    final PoolConfig softLimitPc = new PoolConfig();
    softLimitPc.setValidateOnCheckIn(true);
    softLimitPc.setValidateOnCheckOut(true);
    softLimitPc.setValidatePeriodically(true);
    softLimitPc.setPrunePeriod(5L);
    softLimitPc.setExpirationTime(1L);
    softLimitPc.setValidatePeriod(5L);
    softLimitPool = new SoftLimitConnectionPool(softLimitPc, cc);
    softLimitPool.setValidator(new SearchValidator());

    final PoolConfig blockingPc = new PoolConfig();
    blockingPc.setValidateOnCheckIn(true);
    blockingPc.setValidateOnCheckOut(true);
    blockingPc.setValidatePeriodically(true);
    blockingPc.setPrunePeriod(5L);
    blockingPc.setExpirationTime(1L);
    blockingPc.setValidatePeriod(5L);
    blockingPool = new BlockingConnectionPool(blockingPc, cc);
    blockingPool.setValidator(new SearchValidator());

    final PoolConfig blockingTimeoutPc = new PoolConfig();
    blockingTimeoutPc.setValidateOnCheckIn(true);
    blockingTimeoutPc.setValidateOnCheckOut(true);
    blockingTimeoutPc.setValidatePeriodically(true);
    blockingTimeoutPc.setPrunePeriod(5L);
    blockingTimeoutPc.setExpirationTime(1L);
    blockingTimeoutPc.setValidatePeriod(5L);
    blockingTimeoutPool = new BlockingConnectionPool(blockingTimeoutPc, cc);
    blockingTimeoutPool.setBlockWaitTime(1000L);
    blockingTimeoutPool.setValidator(new SearchValidator());

    final ConnectionConfig connStrategyCc =
      TestUtil.createConnection().getConnectionConfig();
    connStrategyCc.setLdapUrl(
      "ldap://ed-dev.middleware.vt.edu:14389 ldap://ed-dne.middleware.vt.edu");
    connStrategyCc.setConnectionStrategy(ConnectionStrategy.ROUND_ROBIN);
    connStrategyPool = new BlockingConnectionPool(
      new PoolConfig(), connStrategyCc);
  }


  /**
   * @param  ldifFile2  to create.
   * @param  ldifFile3  to create.
   * @param  ldifFile4  to create.
   * @param  ldifFile5  to create.
   * @param  ldifFile6  to create.
   * @param  ldifFile7  to create.
   * @param  ldifFile8  to create.
   * @param  ldifFile9  to create.
   * @param  ldifFile10  to create.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "createEntry2",
      "createEntry3",
      "createEntry4",
      "createEntry5",
      "createEntry6",
      "createEntry7",
      "createEntry8",
      "createEntry9",
      "createEntry10"
    }
  )
  @BeforeClass(
    groups = {
      "softlimitpooltest",
      "blockingpooltest",
      "blockingtimeoutpooltest",
      "connstrategypooltest"
    }
  )
  public void createPoolEntry(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4,
    final String ldifFile5,
    final String ldifFile6,
    final String ldifFile7,
    final String ldifFile8,
    final String ldifFile9,
    final String ldifFile10)
    throws Exception
  {
    entries.get("2")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile2)).getEntry();
    entries.get("3")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile3)).getEntry();
    entries.get("4")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile4)).getEntry();
    entries.get("5")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile5)).getEntry();
    entries.get("6")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile6)).getEntry();
    entries.get("7")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile7)).getEntry();
    entries.get("8")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile8)).getEntry();
    entries.get("9")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile9)).getEntry();
    entries.get("10")[0] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile10)).getEntry();

    for (Map.Entry<String, LdapEntry[]> e : entries.entrySet()) {
      super.createLdapEntry(e.getValue()[0]);
    }

    softLimitPool.initialize();
    blockingPool.initialize();
    blockingTimeoutPool.initialize();
    connStrategyPool.initialize();
  }


  /**
   * @param  ldifFile2  to load.
   * @param  ldifFile3  to load.
   * @param  ldifFile4  to load.
   * @param  ldifFile5  to load.
   * @param  ldifFile6  to load.
   * @param  ldifFile7  to load.
   * @param  ldifFile8  to load.
   * @param  ldifFile9  to load.
   * @param  ldifFile10  to load.
   *
   * @throws  Exception  On test failure.
   */
  @Parameters(
    {
      "searchResults2",
      "searchResults3",
      "searchResults4",
      "searchResults5",
      "searchResults6",
      "searchResults7",
      "searchResults8",
      "searchResults9",
      "searchResults10"
    }
  )
  @BeforeClass(
    groups = {
      "softlimitpooltest",
      "blockingpooltest",
      "blockingtimeoutpooltest",
      "connstrategypooltest"
    }
  )
  public void loadPoolSearchResults(
    final String ldifFile2,
    final String ldifFile3,
    final String ldifFile4,
    final String ldifFile5,
    final String ldifFile6,
    final String ldifFile7,
    final String ldifFile8,
    final String ldifFile9,
    final String ldifFile10)
    throws Exception
  {
    entries.get("2")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile2)).getEntry();
    entries.get("3")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile3)).getEntry();
    entries.get("4")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile4)).getEntry();
    entries.get("5")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile5)).getEntry();
    entries.get("6")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile6)).getEntry();
    entries.get("7")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile7)).getEntry();
    entries.get("8")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile8)).getEntry();
    entries.get("9")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile9)).getEntry();
    entries.get("10")[1] = TestUtil.convertLdifToResult(
      TestUtil.readFileIntoString(ldifFile10)).getEntry();
  }


  /** @throws  Exception  On test failure. */
  @AfterClass(
    groups = {
      "softlimitpooltest",
      "blockingpooltest",
      "blockingtimeoutpooltest",
      "connstrategypooltest"
    }
  )
  public void deletePoolEntry()
    throws Exception
  {
    super.deleteLdapEntry(entries.get("2")[0].getDn());
    super.deleteLdapEntry(entries.get("3")[0].getDn());
    super.deleteLdapEntry(entries.get("4")[0].getDn());
    super.deleteLdapEntry(entries.get("5")[0].getDn());
    super.deleteLdapEntry(entries.get("6")[0].getDn());
    super.deleteLdapEntry(entries.get("7")[0].getDn());
    super.deleteLdapEntry(entries.get("8")[0].getDn());
    super.deleteLdapEntry(entries.get("9")[0].getDn());
    super.deleteLdapEntry(entries.get("10")[0].getDn());

    softLimitPool.close();
    AssertJUnit.assertEquals(softLimitPool.availableCount(), 0);
    AssertJUnit.assertEquals(softLimitPool.activeCount(), 0);
    blockingPool.close();
    AssertJUnit.assertEquals(blockingPool.availableCount(), 0);
    AssertJUnit.assertEquals(blockingPool.activeCount(), 0);
    blockingTimeoutPool.close();
    AssertJUnit.assertEquals(blockingTimeoutPool.availableCount(), 0);
    AssertJUnit.assertEquals(blockingTimeoutPool.activeCount(), 0);
    connStrategyPool.close();
    AssertJUnit.assertEquals(connStrategyPool.availableCount(), 0);
    AssertJUnit.assertEquals(connStrategyPool.activeCount(), 0);
  }


  /**
   * Sample user data.
   *
   * @return  user data
   */
  @DataProvider(name = "pool-data")
  public Object[][] createPoolData()
  {
    return
      new Object[][] {
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=jadams@vt.edu"),
            new String[] {"departmentNumber", "givenName", "sn", }),
          entries.get("2")[1],
        },
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=tjefferson@vt.edu"),
            new String[] {"departmentNumber", "givenName", "sn", }),
          entries.get("3")[1],
        },
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=jmadison@vt.edu"),
            new String[] {"departmentNumber", "givenName", "sn", }),
          entries.get("4")[1],
        },
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=jmonroe@vt.edu"),
            new String[] {"departmentNumber", "givenName", "sn", }),
          entries.get("5")[1],
        },
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=jqadams@vt.edu"),
            new String[] {"departmentNumber", "givenName", "sn", }),
          entries.get("6")[1],
        },
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=ajackson@vt.edu"),
            new String[] {"departmentNumber", "givenName", "sn", }),
          entries.get("7")[1],
        },
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=mvburen@vt.edu"),
            new String[] {
              "departmentNumber", "givenName", "sn", "jpegPhoto", }),
          entries.get("8")[1],
        },
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=whharrison@vt.edu"),
            new String[] {"departmentNumber", "givenName", "sn", }),
          entries.get("9")[1],
        },
        {
          new SearchRequest(
            "ou=test,dc=vt,dc=edu",
            new SearchFilter("mail=jtyler@vt.edu"),
            new String[] {"departmentNumber", "givenName", "sn", }),
          entries.get("10")[1],
        },
      };
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"softlimitpooltest"})
  public void checkSoftLimitPoolImmutable()
    throws Exception
  {
    try {
      softLimitPool.getPoolConfig().setMinPoolSize(8);
      AssertJUnit.fail("Expected illegalstateexception to be thrown");
    } catch (IllegalStateException e) {
      AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
    }

    Connection conn = null;
    try {
      conn = softLimitPool.getConnection();
      try {
        conn.setConnectionConfig(new ConnectionConfig());
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
      try {
        conn.getConnectionConfig().setTimeout(10000);
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
    } finally {
      conn.close();
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"softlimitpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 3,
    invocationCount = 50,
    timeOut = 60000
  )
  public void softLimitSmallSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    softLimitRuntime += search(
      softLimitPool,
      request,
      results);
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"softlimitpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000,
    dependsOnMethods = {"softLimitSmallSearch"}
  )
  public void softLimitMediumSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    softLimitRuntime += search(
      softLimitPool,
      request,
      results);
  }


  /** @throws  Exception  On test failure. */
  @Test(
    groups = {"softlimitpooltest"},
    dependsOnMethods = {"softLimitMediumSearch"}
  )
  public void softLimitMaxClean()
    throws Exception
  {
    Thread.sleep(10000);
    AssertJUnit.assertEquals(0, softLimitPool.activeCount());
    AssertJUnit.assertEquals(
      PoolConfig.DEFAULT_MIN_POOL_SIZE,
      softLimitPool.availableCount());
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"blockingpooltest"})
  public void checkBlockingPoolImmutable()
    throws Exception
  {
    try {
      blockingPool.getPoolConfig().setMinPoolSize(8);
      AssertJUnit.fail("Expected illegalstateexception to be thrown");
    } catch (IllegalStateException e) {
      AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
    }

    Connection conn = null;
    try {
      conn = blockingPool.getConnection();
      try {
        conn.setConnectionConfig(new ConnectionConfig());
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
      try {
        conn.getConnectionConfig().setTimeout(10000);
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
    } finally {
      conn.close();
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"blockingpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 3,
    invocationCount = 50,
    timeOut = 60000
  )
  public void blockingSmallSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    blockingRuntime += search(
      blockingPool,
      request,
      results);
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"blockingpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000,
    dependsOnMethods = {"blockingSmallSearch"}
  )
  public void blockingMediumSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    blockingRuntime += search(
      blockingPool,
      request,
      results);
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"blockingpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000,
    dependsOnMethods = {"blockingMediumSearch"}
  )
  public void blockingLargeSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    blockingRuntime += search(
      blockingPool,
      request,
      results);
  }


  /** @throws  Exception  On test failure. */
  @Test(
    groups = {"blockingpooltest"},
    dependsOnMethods = {"blockingLargeSearch"}
  )
  public void blockingMaxClean()
    throws Exception
  {
    Thread.sleep(10000);
    AssertJUnit.assertEquals(0, blockingPool.activeCount());
    AssertJUnit.assertEquals(
      PoolConfig.DEFAULT_MIN_POOL_SIZE,
      blockingPool.availableCount());
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"blockingtimeoutpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 3,
    invocationCount = 50,
    timeOut = 60000
  )
  public void blockingTimeoutSmallSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    try {
      blockingTimeoutRuntime += search(
        blockingTimeoutPool,
        request,
        results);
    } catch (BlockingTimeoutException e) {
      logger.warn("block timeout exceeded for small search", e);
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"blockingtimeoutpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000,
    dependsOnMethods = {"blockingTimeoutSmallSearch"}
  )
  public void blockingTimeoutMediumSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    try {
      blockingTimeoutRuntime += search(
        blockingTimeoutPool,
        request,
        results);
    } catch (BlockingTimeoutException e) {
      logger.warn("block timeout exceeded for medium search", e);
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"blockingtimeoutpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000,
    dependsOnMethods = {"blockingTimeoutMediumSearch"}
  )
  public void blockingTimeoutLargeSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    try {
      blockingTimeoutRuntime += search(
        blockingTimeoutPool,
        request,
        results);
    } catch (BlockingTimeoutException e) {
      logger.warn("block timeout exceeded for large search", e);
    }
  }


  /** @throws  Exception  On test failure. */
  @Test(
    groups = {"blockingtimeoutpooltest"},
    dependsOnMethods = {"blockingTimeoutLargeSearch"}
  )
  public void blockingTimeoutMaxClean()
    throws Exception
  {
    Thread.sleep(10000);
    AssertJUnit.assertEquals(0, blockingTimeoutPool.activeCount());
    AssertJUnit.assertEquals(
      PoolConfig.DEFAULT_MIN_POOL_SIZE,
      blockingTimeoutPool.availableCount());
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"connstrategypooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000
  )
  public void connStrategySearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    search(connStrategyPool, request, results);
  }


  /**
   * @param  pool  to get ldap object from.
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @return  time it takes to checkout/search/checkin from the pool
   *
   * @throws  Exception  On test failure.
   */
  private long search(
    final ConnectionPool pool,
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    final long startTime = System.currentTimeMillis();
    Connection conn = null;
    LdapResult result = null;
    try {
      logger.trace("waiting for pool checkout");
      conn = pool.getConnection();
      logger.trace("performing search: {}", request);
      final SearchOperation search = new SearchOperation(conn);
      result = search.execute(request).getResult();
      logger.trace("search completed: {}", result);
    } finally {
      logger.trace("returning ldap to pool");
      conn.close();
    }
    final StringWriter sw = new StringWriter();
    final LdifWriter lw = new LdifWriter(sw);
    lw.write(result);
    AssertJUnit.assertEquals(
      results,
      TestUtil.convertLdifToResult(sw.toString()).getEntry());
    return System.currentTimeMillis() - startTime;
  }
}
