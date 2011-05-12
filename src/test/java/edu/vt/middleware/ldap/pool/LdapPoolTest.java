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
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.TestUtil;
import edu.vt.middleware.ldap.ldif.LdifWriter;
import edu.vt.middleware.ldap.pool.commons.CommonsLdapPool;
import edu.vt.middleware.ldap.pool.commons.DefaultLdapPoolableObjectFactory;
import edu.vt.middleware.ldap.provider.ConnectionStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Load test for ldap pools.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class LdapPoolTest extends AbstractTest
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

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(getClass());

  /** LdapPool instance for concurrency testing. */
  private SoftLimitLdapPool softLimitPool;

  /** LdapPool instance for concurrency testing. */
  private BlockingLdapPool blockingPool;

  /** LdapPool instance for concurrency testing. */
  private BlockingLdapPool blockingTimeoutPool;

  /** LdapPool instance for concurrency testing. */
  private SharedLdapPool sharedPool;

  /** LdapPool instance for concurrency testing. */
  private BlockingLdapPool connStrategyPool;

  /** LdapPool instance for concurrency testing. */
  private BlockingLdapPool vtComparisonPool;

  /** Commons LdapPool for comparison testing. */
  private CommonsLdapPool commonsComparisonPool;

  /** Time in millis it takes the pool test to run. */
  private long softLimitRuntime;

  /** Time in millis it takes the pool test to run. */
  private long blockingRuntime;

  /** Time in millis it takes the pool test to run. */
  private long blockingTimeoutRuntime;

  /** Time in millis it takes the pool test to run. */
  private long sharedRuntime;

  /** Time in millis it takes the pool test to run. */
  private long vtPoolRuntime;

  /** Time in millis it takes the pool test to run. */
  private long commonsPoolRuntime;


  /**
   * Default constructor.
   *
   * @throws  Exception  On test failure.
   */
  public LdapPoolTest()
    throws Exception
  {
    final LdapConnectionConfig lcc =
      TestUtil.createLdapConnection().getLdapConnectionConfig();
    final DefaultLdapFactory factory = new DefaultLdapFactory(lcc);
    factory.setLdapValidator(
      new CompareLdapValidator(
        "ou=test,dc=vt,dc=edu",
        new SearchFilter("ou=test")));

    final LdapPoolConfig softLimitLpc = new LdapPoolConfig();
    softLimitLpc.setValidateOnCheckIn(true);
    softLimitLpc.setValidateOnCheckOut(true);
    softLimitLpc.setValidatePeriodically(true);
    softLimitLpc.setPruneTimerPeriod(5000L);
    softLimitLpc.setExpirationTime(1000L);
    softLimitLpc.setValidateTimerPeriod(5000L);
    softLimitPool = new SoftLimitLdapPool(softLimitLpc, factory);

    final LdapPoolConfig blockingLpc = new LdapPoolConfig();
    blockingLpc.setValidateOnCheckIn(true);
    blockingLpc.setValidateOnCheckOut(true);
    blockingLpc.setValidatePeriodically(true);
    blockingLpc.setPruneTimerPeriod(5000L);
    blockingLpc.setExpirationTime(1000L);
    blockingLpc.setValidateTimerPeriod(5000L);
    blockingPool = new BlockingLdapPool(blockingLpc, factory);

    final LdapPoolConfig blockingTimeoutLpc = new LdapPoolConfig();
    blockingTimeoutLpc.setValidateOnCheckIn(true);
    blockingTimeoutLpc.setValidateOnCheckOut(true);
    blockingTimeoutLpc.setValidatePeriodically(true);
    blockingTimeoutLpc.setPruneTimerPeriod(5000L);
    blockingTimeoutLpc.setExpirationTime(1000L);
    blockingTimeoutLpc.setValidateTimerPeriod(5000L);
    blockingTimeoutPool = new BlockingLdapPool(blockingLpc, factory);
    blockingTimeoutPool.setBlockWaitTime(1000L);

    final LdapPoolConfig sharedLpc = new LdapPoolConfig();
    sharedLpc.setValidateOnCheckIn(true);
    sharedLpc.setValidateOnCheckOut(true);
    sharedLpc.setValidatePeriodically(true);
    sharedLpc.setPruneTimerPeriod(5000L);
    sharedLpc.setExpirationTime(1000L);
    sharedLpc.setValidateTimerPeriod(5000L);
    sharedPool = new SharedLdapPool(sharedLpc, factory);

    final LdapConnectionConfig connStrategyLcc =
      TestUtil.createLdapConnection().getLdapConnectionConfig();
    connStrategyLcc.setLdapUrl(
      "ldap://ed-dev.middleware.vt.edu:14389 ldap://ed-dne.middleware.vt.edu");
    connStrategyLcc.setConnectionStrategy(ConnectionStrategy.ROUND_ROBIN);
    final DefaultLdapFactory connStrategyFactory = new DefaultLdapFactory(
      connStrategyLcc);
    connStrategyPool = new BlockingLdapPool(
      new LdapPoolConfig(), connStrategyFactory);

    // configure comparison pools
    final LdapPoolConfig vtComparisonLpc = new LdapPoolConfig();
    vtComparisonLpc.setValidateOnCheckIn(true);
    vtComparisonLpc.setValidateOnCheckOut(true);
    vtComparisonPool = new BlockingLdapPool(vtComparisonLpc, factory);

    final DefaultLdapPoolableObjectFactory commonsFactory =
      new DefaultLdapPoolableObjectFactory(lcc);
    commonsFactory.setLdapValidator(
      new CompareLdapValidator(
        "ou=test,dc=vt,dc=edu",
        new SearchFilter("ou=test")));
    commonsComparisonPool = new CommonsLdapPool(commonsFactory);
    commonsComparisonPool.setTestOnReturn(true);
    commonsComparisonPool.setTestOnBorrow(true);
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
      "queuepooltest",
      "softlimitpooltest",
      "blockingpooltest",
      "blockingtimeoutpooltest",
      "sharedpooltest",
      "connstrategypooltest",
      "comparisonpooltest"
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
    sharedPool.initialize();
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
      "queuepooltest",
      "softlimitpooltest",
      "blockingpooltest",
      "blockingtimeoutpooltest",
      "sharedpooltest",
      "connstrategypooltest",
      "comparisonpooltest"
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
      "queuepooltest",
      "softlimitpooltest",
      "blockingpooltest",
      "blockingtimeoutpooltest",
      "sharedpooltest",
      "connstrategypooltest",
      "comparisonpooltest"
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
    sharedPool.close();
    AssertJUnit.assertEquals(sharedPool.availableCount(), 0);
    AssertJUnit.assertEquals(sharedPool.activeCount(), 0);
    connStrategyPool.close();
    AssertJUnit.assertEquals(connStrategyPool.availableCount(), 0);
    AssertJUnit.assertEquals(connStrategyPool.activeCount(), 0);
    vtComparisonPool.close();
    AssertJUnit.assertEquals(vtComparisonPool.availableCount(), 0);
    AssertJUnit.assertEquals(vtComparisonPool.activeCount(), 0);
    commonsComparisonPool.clear();
    commonsComparisonPool.close();
    AssertJUnit.assertEquals(commonsComparisonPool.getNumActive(), 0);
    AssertJUnit.assertEquals(commonsComparisonPool.getNumIdle(), 0);
    // vt pool should be minimally faster
    AssertJUnit.assertEquals(
      vtPoolRuntime,
      Math.min(vtPoolRuntime, commonsPoolRuntime));
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
            new String[] {"departmentNumber", "givenName", "sn", }),
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
      softLimitPool.getLdapPoolConfig().setMinPoolSize(8);
      AssertJUnit.fail("Expected illegalstateexception to be thrown");
    } catch (IllegalStateException e) {
      AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
    }

    LdapConnection conn = null;
    try {
      conn = softLimitPool.checkOut();
      try {
        conn.setLdapConnectionConfig(new LdapConnectionConfig());
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
      try {
        conn.getLdapConnectionConfig().setTimeout(10000);
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
    } finally {
      softLimitPool.checkIn(conn);
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"queuepooltest", "softlimitpooltest"},
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
    groups = {"queuepooltest", "softlimitpooltest"},
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
    groups = {"queuepooltest", "softlimitpooltest"},
    dependsOnMethods = {"softLimitMediumSearch"}
  )
  public void softLimitMaxClean()
    throws Exception
  {
    Thread.sleep(10000);
    AssertJUnit.assertEquals(0, softLimitPool.activeCount());
    AssertJUnit.assertEquals(
      LdapPoolConfig.DEFAULT_MIN_POOL_SIZE,
      softLimitPool.availableCount());
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"blockingpooltest"})
  public void checkBlockingPoolImmutable()
    throws Exception
  {
    try {
      blockingPool.getLdapPoolConfig().setMinPoolSize(8);
      AssertJUnit.fail("Expected illegalstateexception to be thrown");
    } catch (IllegalStateException e) {
      AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
    }

    LdapConnection conn = null;
    try {
      conn = blockingPool.checkOut();
      try {
        conn.setLdapConnectionConfig(new LdapConnectionConfig());
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
      try {
        conn.getLdapConnectionConfig().setTimeout(10000);
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
    } finally {
      blockingPool.checkIn(conn);
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"queuepooltest", "blockingpooltest"},
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
    groups = {"queuepooltest", "blockingpooltest"},
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
    groups = {"queuepooltest", "blockingpooltest"},
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
    groups = {"queuepooltest", "blockingpooltest"},
    dependsOnMethods = {"blockingLargeSearch"}
  )
  public void blockingMaxClean()
    throws Exception
  {
    Thread.sleep(10000);
    AssertJUnit.assertEquals(0, blockingPool.activeCount());
    AssertJUnit.assertEquals(
      LdapPoolConfig.DEFAULT_MIN_POOL_SIZE,
      blockingPool.availableCount());
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"queuepooltest", "blockingtimeoutpooltest"},
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
      logger.info("block timeout exceeded");
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"queuepooltest", "blockingtimeoutpooltest"},
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
      logger.info("block timeout exceeded");
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"queuepooltest", "blockingtimeoutpooltest"},
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
      logger.info("block timeout exceeded");
    }
  }


  /** @throws  Exception  On test failure. */
  @Test(
    groups = {"queuepooltest", "blockingtimeoutpooltest"},
    dependsOnMethods = {"blockingTimeoutLargeSearch"}
  )
  public void blockingTimeoutMaxClean()
    throws Exception
  {
    Thread.sleep(10000);
    AssertJUnit.assertEquals(0, blockingTimeoutPool.activeCount());
    AssertJUnit.assertEquals(
      LdapPoolConfig.DEFAULT_MIN_POOL_SIZE,
      blockingTimeoutPool.availableCount());
  }


  /** @throws  Exception  On test failure. */
  @Test(groups = {"sharedpooltest"})
  public void checkSharedPoolImmutable()
    throws Exception
  {
    try {
      sharedPool.getLdapPoolConfig().setMinPoolSize(8);
      AssertJUnit.fail("Expected illegalstateexception to be thrown");
    } catch (IllegalStateException e) {
      AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
    }

    LdapConnection conn = null;
    try {
      conn = sharedPool.checkOut();
      try {
        conn.setLdapConnectionConfig(new LdapConnectionConfig());
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
      try {
        conn.getLdapConnectionConfig().setTimeout(10000);
        AssertJUnit.fail("Expected illegalstateexception to be thrown");
      } catch (IllegalStateException e) {
        AssertJUnit.assertEquals(IllegalStateException.class, e.getClass());
      }
    } finally {
      sharedPool.checkIn(conn);
    }
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"queuepooltest", "sharedpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 3,
    invocationCount = 50,
    timeOut = 60000
  )
  public void sharedSmallSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    sharedRuntime += search(
      sharedPool,
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
    groups = {"queuepooltest", "sharedpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 10,
    invocationCount = 100,
    timeOut = 60000,
    dependsOnMethods = {"sharedSmallSearch"}
  )
  public void sharedMediumSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    sharedRuntime += search(
      sharedPool,
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
    groups = {"queuepooltest", "sharedpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000,
    dependsOnMethods = {"sharedMediumSearch"}
  )
  public void sharedLargeSearch(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    sharedRuntime += search(
      sharedPool,
      request,
      results);
  }


  /** @throws  Exception  On test failure. */
  @Test(
    groups = {"queuepooltest", "sharedpooltest"},
    dependsOnMethods = {"sharedLargeSearch"}
  )
  public void sharedMaxClean()
    throws Exception
  {
    Thread.sleep(10000);
    AssertJUnit.assertEquals(0, sharedPool.activeCount());
    AssertJUnit.assertEquals(
      LdapPoolConfig.DEFAULT_MIN_POOL_SIZE,
      sharedPool.availableCount());
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"queuepooltest", "connstrategypooltest"},
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
    final LdapPool<LdapConnection> pool,
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    final long startTime = System.currentTimeMillis();
    LdapConnection conn = null;
    LdapResult result = null;
    try {
      if (logger.isTraceEnabled()) {
        logger.trace("waiting for pool checkout");
      }
      conn = pool.checkOut();
      if (logger.isTraceEnabled()) {
        logger.trace("performing search");
      }
      final SearchOperation search = new SearchOperation(conn);
      result = search.execute(request).getResult();
      if (logger.isTraceEnabled()) {
        logger.trace("search completed");
      }
    } finally {
      if (logger.isTraceEnabled()) {
        logger.trace("returning ldap to pool");
      }
      pool.checkIn(conn);
    }
    final StringWriter sw = new StringWriter();
    final LdifWriter lw = new LdifWriter(sw);
    lw.write(result);
    AssertJUnit.assertEquals(
      results,
      TestUtil.convertLdifToResult(sw.toString()));
    return System.currentTimeMillis() - startTime;
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"comparisonpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000
  )
  public void vtPoolComparison(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    final long startTime = System.currentTimeMillis();
    LdapConnection conn = null;
    try {
      if (logger.isTraceEnabled()) {
        logger.trace("waiting for pool checkout");
      }
      conn = vtComparisonPool.checkOut();
      if (logger.isTraceEnabled()) {
        logger.trace("performing search");
      }
      final SearchOperation search = new SearchOperation(conn);
      search.execute(request);
      if (logger.isTraceEnabled()) {
        logger.trace("search completed");
      }
    } finally {
      if (logger.isTraceEnabled()) {
        logger.trace("returning ldap to pool");
      }
      vtComparisonPool.checkIn(conn);
    }
    vtPoolRuntime += System.currentTimeMillis() - startTime;
  }


  /**
   * @param  request  to search with
   * @param  results  to expect from the search.
   *
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"comparisonpooltest"},
    dataProvider = "pool-data",
    threadPoolSize = 50,
    invocationCount = 1000,
    timeOut = 60000
  )
  public void commonsPoolComparison(
    final SearchRequest request,
    final LdapEntry results)
    throws Exception
  {
    final long startTime = System.currentTimeMillis();
    LdapConnection conn = null;
    try {
      if (logger.isTraceEnabled()) {
        logger.trace("waiting for pool checkout");
      }
      conn = (LdapConnection) commonsComparisonPool.borrowObject();
      if (logger.isTraceEnabled()) {
        logger.trace("performing search");
      }
      final SearchOperation search = new SearchOperation(conn);
      search.execute(request);
      if (logger.isTraceEnabled()) {
        logger.trace("search completed");
      }
    } finally {
      if (logger.isTraceEnabled()) {
        logger.trace("returning ldap to pool");
      }
      commonsComparisonPool.returnObject(conn);
    }
    commonsPoolRuntime += System.currentTimeMillis() - startTime;
  }
}
