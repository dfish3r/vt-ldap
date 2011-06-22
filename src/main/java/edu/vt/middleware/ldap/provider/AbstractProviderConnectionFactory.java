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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import edu.vt.middleware.ldap.AuthenticationType;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a basic implementation for other connection handlers to inherit.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractProviderConnectionFactory
  implements ProviderConnectionFactory
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap connection strategy. */
  protected ConnectionStrategy connectionStrategy = ConnectionStrategy.DEFAULT;

  /** Result codes indicating that an operation should be retried. */
  protected ResultCode[] operationRetryResultCodes;

  /** LDAP URL for connections. */
  protected String ldapUrl;

  /** Authentication type. */
  protected AuthenticationType authenticationType;

  /** Whether to log authentication credentials. */
  protected boolean logCredentials;

  /** Number of connections made. */
  private ConnectionCount connectionCount = new ConnectionCount();


  /**
   * Returns the connection count.
   *
   * @return  connection count
   */
  protected ConnectionCount getConnectionCount()
  {
    return connectionCount;
  }


  /**
   * Sets the connection count.
   *
   * @param  cc  connection count
   */
  protected void setConnectionCount(final ConnectionCount cc)
  {
    connectionCount = cc;
  }


  /** {@inheritDoc} */
  @Override
  public ConnectionStrategy getConnectionStrategy()
  {
    return connectionStrategy;
  }


  /** {@inheritDoc} */
  @Override
  public void setConnectionStrategy(final ConnectionStrategy strategy)
  {
    logger.trace("setting connectionStrategy: {}", strategy);
    connectionStrategy = strategy;
  }


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
    operationRetryResultCodes = codes;
  }


  /** {@inheritDoc} */
  @Override
  public AuthenticationType getAuthenticationType()
  {
    return authenticationType;
  }


  /** {@inheritDoc} */
  @Override
  public void setAuthenticationType(final AuthenticationType type)
  {
    authenticationType = type;
  }


  /** {@inheritDoc} */
  @Override
  public boolean getLogCredentials()
  {
    return logCredentials;
  }


  /** {@inheritDoc} */
  @Override
  public void setLogCredentials(final boolean b)
  {
    logCredentials = b;
  }


  /** {@inheritDoc} */
  @Override
  public ProviderConnection create(final String dn, final Credential credential)
    throws LdapException
  {
    LdapException lastThrown = null;
    final String[] urls = parseLdapUrl(
      ldapUrl, connectionStrategy);
    ProviderConnection conn = null;
    for (String url : urls) {
      try {
        logger.trace(
          "[{}] Attempting connection to {} for strategy {}",
          new Object[] {
            connectionCount,
            url,
            connectionStrategy, });
        conn = createInternal(url, dn, credential);
        connectionCount.incrementCount();
        lastThrown = null;
        break;
      } catch (ConnectionException e) {
        lastThrown = e;
        logger.debug("Error connecting to LDAP URL: {}", url, e);
      }
    }
    if (lastThrown != null) {
      throw lastThrown;
    }
    return conn;
  }


  /**
   * Create the provider connection and prepare the connection for use.
   *
   * @param  url  to connect to
   * @param  dn  to bind as
   * @param  credential  to bind with in conjunction with dn
   * @return  ldap connection
   *
   * @throws  LdapException  if a connection cannot be established
   */
  protected abstract ProviderConnection createInternal(
    final String url, final String dn, final Credential credential)
    throws LdapException;


  /**
   * Parses the supplied ldap url and splits it into separate URLs if it is
   * space delimited.
   *
   * @param  url  to parse
   * @param  strategy  of ordered array to return
   *
   * @return  array of ldap URLs
   */
  protected String[] parseLdapUrl(
    final String url, final ConnectionStrategy strategy)
  {
    String[] urls = null;
    if (strategy == ConnectionStrategy.DEFAULT) {
      urls = new String[] {url};
    } else if (strategy == ConnectionStrategy.ACTIVE_PASSIVE) {
      final List<String> l = splitLdapUrl(url);
      urls = l.toArray(new String[l.size()]);
    } else if (strategy == ConnectionStrategy.ROUND_ROBIN) {
      final List<String> l = splitLdapUrl(url);
      for (int i = 0; i < connectionCount.getCount() % l.size(); i++) {
        l.add(l.remove(0));
      }
      urls = l.toArray(new String[l.size()]);
    } else if (strategy == ConnectionStrategy.RANDOM) {
      final List<String> l = splitLdapUrl(url);
      Collections.shuffle(l);
      urls = l.toArray(new String[l.size()]);
    }
    return urls;
  }


  /**
   * Takes a space delimited string of URLs and returns a list of URLs.
   *
   * @param  url  to split
   *
   * @return  list of URLs
   */
  private List<String> splitLdapUrl(final String url)
  {
    final List<String> urls = new ArrayList<String>();
    if (url != null) {
      final StringTokenizer st = new StringTokenizer(url);
      while (st.hasMoreTokens()) {
        urls.add(st.nextToken());
      }
    } else {
      urls.add(null);
    }
    return urls;
  }


  /**
   * Provides an object to track the connection count.
   */
  private class ConnectionCount
  {

    /** connection count. */
    private int count;


    /**
     * Returns the connection count.
     *
     * @return  count
     */
    public int getCount()
    {
      return count;
    }


    /** Increments the connection count. */
    public void incrementCount()
    {
      count++;
      // reset the count if it exceeds the size of an integer
      if (count < 0) {
        count = 0;
      }
    }


    /**
     * Returns a string representation of this object.
     *
     * @return  count as a string
     */
    @Override
    public String toString()
    {
      return Integer.toString(count);
    }
  }
}
