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
package edu.vt.middleware.ldap.provider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.ldap.AbstractConfig;
import edu.vt.middleware.ldap.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains configuration data common to providers.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class ProviderConfig extends AbstractConfig
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Result codes indicating that an operation should be retried. */
  protected ResultCode[] operationRetryResultCodes;

  /** Additional provider properties. */
  protected Map<String, Object> properties = new HashMap<String, Object>();

  /** Connection strategy. */
  protected ConnectionStrategy connectionStrategy = ConnectionStrategy.DEFAULT;

  /** Whether to log authentication credentials. */
  protected boolean logCredentials;


  /** Default constructor. */
  public ProviderConfig() {}


  /**
   * Returns the result codes that trigger an operation retry.
   *
   * @return  ldap result codes
   */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /**
   * Sets the result codes that trigger an operation retry.
   *
   * @param  codes  ldap result codes
   */
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    logger.trace(
      "setting operationRetryResultCodes: {}",
      codes != null ? Arrays.asList(codes) : null);
    operationRetryResultCodes = codes;
  }


  /**
   * Returns provider specific properties.
   *
   * @return  map of additional provider properties
   */
  public Map<String, Object> getProperties()
  {
    return properties;
  }


  /**
   * Sets provider specific properties.
   *
   * @param  props  map of additional provider properties
   */
  public void setProperties(final Map<String, Object> props)
  {
    logger.trace("setting properties: {}", props);
    properties = props;
  }


  /**
   * Returns the connection strategy.
   *
   * @return  strategy for making connections
   */
  public ConnectionStrategy getConnectionStrategy()
  {
    return connectionStrategy;
  }


  /**
   * Sets the connection strategy.
   *
   * @param  strategy  for making connections
   */
  public void setConnectionStrategy(final ConnectionStrategy strategy)
  {
    logger.trace("setting connectionStrategy: {}", strategy);
    connectionStrategy = strategy;
  }


  /**
   * Returns whether authentication credentials will be logged.
   *
   * @return  whether authentication credentials will be logged
   */
  public boolean getLogCredentials()
  {
    return logCredentials;
  }


  /**
   * Sets whether authentication credentials will be logged.
   *
   * @param  b  whether authentication credentials will be logged
   */
  public void setLogCredentials(final boolean b)
  {
    logger.trace("setting logCredentials: {}", b);
    logCredentials = b;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::operationRetryResultCodes=%s, properties=%s, " +
        "connectionStrategy=%s, logCredentials=%s]",
        getClass().getName(),
        hashCode(),
        operationRetryResultCodes != null ?
          Arrays.asList(operationRetryResultCodes) : null,
        properties,
        connectionStrategy,
        logCredentials);
  }
}
