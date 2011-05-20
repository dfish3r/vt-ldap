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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides common implementation to other ldap operations
 *
 * @param  <Q>  type of ldap request
 * @param  <S>  type of ldap response
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public abstract class AbstractOperation<Q extends Request, S>
  implements Operation<Q, S>
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection to perform operation. */
  protected Connection connection;

  /** Number of times to retry ldap operations. */
  protected int operationRetry;

  /** Amount of time in milliseconds to wait before retrying. */
  protected long operationRetryWait;

  /** Factor to multiply operation retry wait by. */
  protected int operationRetryBackoff;


  /**
   * Sets the properties of this operation with values from the supplied ldap
   * connection configuration.
   *
   * @param  lcc  ldap connection configuration to read properties from
   */
  protected void initialize(final ConnectionConfig lcc)
  {
    operationRetry = lcc.getOperationRetry();
    operationRetryWait = lcc.getOperationRetryWait();
    operationRetryBackoff = lcc.getOperationRetryBackoff();
  }


  /**
   * Returns the operation retry. This is the number of times an operation
   * will be retried when it throws OperationException. What constitutes an
   * OperationException is configured per each provider implementation.
   *
   * @return  operation retry
   */
  public int getOperationRetry()
  {
    return operationRetry;
  }


  /**
   * Sets the operation retry.
   *
   * @param  retry  to set
   */
  public void setOperationRetry(final int retry)
  {
    operationRetry = retry;
  }


  /**
   * Returns the operation retry wait. This is the amount of time in
   * milliseconds that the executing thread will sleep before attempting the
   * operation again.
   *
   * @return  operation retry wait
   */
  public long getOperationRetryWait()
  {
    return operationRetryWait;
  }


  /**
   * Sets the operation retry wait.
   *
   * @param  wait  to set
   */
  public void setOperationRetryWait(final long wait)
  {
    operationRetryWait = wait;
  }


  /**
   * Returns the operation retry backoff. This is the factor by which the retry
   * wait will be multiplied in order to progressively delay the amount time
   * between each operation retry.
   *
   * @return  operation retry backoff
   */
  public int getOperationRetryBackoff()
  {
    return operationRetryBackoff;
  }


  /**
   * Sets the operation retry backoff.
   *
   * @param  backoff  to set
   */
  public void setOperationRetryBackoff(final int backoff)
  {
    operationRetryBackoff = backoff;
  }


  /**
   * Call the provider specific implementation of this ldap operation.
   *
   * @param  request  ldap request
   * @return  ldap response
   * @throws  LdapException  if the invocation fails
   */
  protected abstract Response<S> invoke(final Q request)
    throws LdapException;


  /**
   * Performs any initialization on the supplied request to prepare it for use
   * in this operation.
   *
   * @param  request  to initialize
   * @param  lcc  ldap connection configuration to initialize request with
   */
  protected abstract void initializeRequest(
    final Q request, final ConnectionConfig lcc);


  /** {@inheritDoc} */
  @Override
  public Response<S> execute(final Q request)
    throws LdapException
  {
    logger.debug(
      "request={}, connection={}", request, connection);

    Response<S> response = null;
    initializeRequest(
      request, connection.getConnectionConfig());
    for (int i = 0;
         i <= operationRetry || operationRetry == -1;
         i++) {
      try {
        response = invoke(request);
        break;
      } catch (OperationException e) {
        operationRetry(e, i);
      }
    }
    return response;
  }


  /**
   * Called in response to an operation failure. This method determines whether
   * that operation should be attempted again. If so, the underlying ldap
   * connection is closed and re-opened in preparation for that event. If not,
   * the supplied exception is thrown.
   *
   * @param  e  exception that was thrown
   * @param  count  number of operation attempts
   *
   * @throws  LdapException  if the operation won't be retried
   */
  protected void operationRetry(final LdapException e, final int count)
    throws LdapException
  {
    if (count < operationRetry || operationRetry == -1) {
      logger.warn(
        "Error performing LDAP operation, retrying (attempt {})", count, e);
      connection.close();
      if (operationRetryWait > 0) {
        long sleepTime = operationRetryWait;
        if (operationRetryBackoff > 0 && count > 0) {
          sleepTime = sleepTime * operationRetryBackoff * count;
        }
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
          logger.debug("Operation retry wait interrupted", ie);
        }
      }
      connection.open();
    } else {
      throw e;
    }
  }
}
