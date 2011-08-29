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

/**
 * Provides a wrapper class for testing {@link #operationRetry()}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RetrySearchOperation extends SearchOperation
{

  /** serial version uid. */
  private static final long serialVersionUID = 4247614583961731974L;

  /** retry counter. */
  private int retryCount;

  /** run time counter. */
  private long runTime;

  /** stop counter. */
  private int stopCount;


  /**
   * Creates a new retry search operation.
   *
   * @param  c  connection
   */
  public RetrySearchOperation(final Connection c)
  {
    super(c);
  }


  /**
   * Returns the retry count.
   *
   * @return  retry count
   */
  public int getRetryCount()
  {
    return retryCount;
  }


  /**
   * Returns the run time counter.
   *
   * @return  run time
   */
  public long getRunTime()
  {
    return runTime;
  }


  /**
   * Sets the count at which to stop retries.
   *
   * @param  i  stop count
   */
  public void setStopCount(final int i)
  {
    stopCount = i;
  }


  /** Resets all the counters. */
  public void reset()
  {
    retryCount = 0;
    runTime = 0;
    stopCount = 0;
  }


  /** {@inheritDoc} */
  @Override
  protected void operationRetry(
    final LdapException e,
    final int count)
    throws LdapException
  {
    retryCount = count;

    final long t = System.currentTimeMillis();
    super.operationRetry(e, count);
    runTime += System.currentTimeMillis() - t;
    if (stopCount > 0 && retryCount == stopCount) {
      throw e;
    }
  }
}
