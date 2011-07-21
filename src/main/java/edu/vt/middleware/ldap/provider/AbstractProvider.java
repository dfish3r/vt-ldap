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

import java.util.HashMap;
import java.util.Map;
import edu.vt.middleware.ldap.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a basic implementation for other providers to inherit.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractProvider implements Provider
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Result codes indicating that an operation should be retried. */
  protected ResultCode[] operationRetryResultCodes;

  /** Additional provider properties. */
  protected Map<String, Object> properties = new HashMap<String, Object>();


  /** {@inheritDoc} */
  @Override
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /** {@inheritDoc} */
  @Override
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    logger.trace("setting operationRetryResultCodes: {}", codes);
    operationRetryResultCodes = codes;
  }


  /** {@inheritDoc} */
  @Override
  public Map<String, Object> getProperties()
  {
    return properties;
  }


  /** {@inheritDoc} */
  @Override
  public void setProperties(final Map<String, Object> props)
  {
    logger.trace("setting properties: {}", props);
    properties = props;
  }
}
