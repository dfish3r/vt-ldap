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
package edu.vt.middleware.ldap.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Least-Recently-Used cache implementation. Leverages a {@link LinkedHashMap}.
 *
 * @param  <Q>  type of search request
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class LRUCache<Q extends SearchRequest> implements Cache<Q>
{

  /** Initial capacity of the hash map. */
  private static final int INITIAL_CAPACITY = 16;

  /** Load factor of the hash map. */
  private static final float LOAD_FACTOR = 0.75f;

  /** Map to cache search results. */
  private Map<Q, Item> cache;

  /** Executor for performing eviction. */
  private ScheduledExecutorService executor =
    Executors.newSingleThreadScheduledExecutor(
      new ThreadFactory() {
        public Thread newThread(final Runnable r)
        {
          final Thread t = new Thread(r);
          t.setDaemon(true);
          return t;
        }
      });


  /**
   * Creates a new LRU cache.
   *
   * @param  size  number of results to cache
   * @param  timeToLive  in seconds that results should stay in the cache
   * @param interval  in seconds to enforce timeToLive
   */
  public LRUCache(final int size, final long timeToLive, final long interval)
  {
    cache = new LinkedHashMap<Q, Item>(INITIAL_CAPACITY, LOAD_FACTOR, true)
    {
      /** serialVersionUID. */
      private static final long serialVersionUID = -4082551016104288539L;


      /** {@inheritDoc} */
      @Override
      protected boolean removeEldestEntry(final Map.Entry<Q, Item> entry)
      {
        return size() > size;
      }
    };

    final Runnable expire = new Runnable() {
      public void run()
      {
        synchronized (cache) {
          final Iterator<Item> i = cache.values().iterator();
          final long t = System.currentTimeMillis();
          while (i.hasNext()) {
            final Item item = i.next();
            if (t - item.creationTime > TimeUnit.SECONDS.toMillis(timeToLive)) {
              i.remove();
            }
          }
        }
      }
    };
    executor.scheduleAtFixedRate(expire, interval, interval, TimeUnit.SECONDS);
  }


  /**
   * Removes all data from this cache.
   */
  public void clear()
  {
    synchronized (cache) {
      cache.clear();
    }
  }


  /** {@inheritDoc} */
  @Override
  public LdapResult get(final Q request)
  {
    synchronized (cache) {
      if (cache.containsKey(request)) {
        return cache.get(request).result;
      } else {
        return null;
      }
    }
  }


  /** {@inheritDoc} */
  @Override
  public void put(final Q request, final LdapResult lr)
  {
    synchronized (cache) {
      cache.put(request, new Item(lr));
    }
  }


  /**
   * Returns the number of items in this cache.
   *
   * @return  size of this cache
   */
  public int size()
  {
    synchronized (cache) {
      return cache.size();
    }
  }


  /**
   * Frees any resources associated with this cache.
   */
  public void close()
  {
    executor.shutdown();
    cache = null;
  }


  /**
   * Container for data related to cached ldap results.
   */
  private class Item
  {

    /** Ldap result. */
    private LdapResult result;

    /** Timestamp when this item is created. */
    private long creationTime;


    /**
     * Creates a new item.
     *
     * @param  lr  ldap result
     */
    public Item(final LdapResult lr)
    {
      result = lr;
      creationTime = System.currentTimeMillis();
    }
  }
}
