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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a basic implementation for other connection handlers to inherit.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractConnectionFactory implements ConnectionFactory
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

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
    return this.connectionCount;
  }


  /**
   * Sets the connection count.
   *
   * @param  cc  connection count
   */
  protected void setConnectionCount(final ConnectionCount cc)
  {
    this.connectionCount = cc;
  }


  /** {@inheritDoc} */
  public ConnectionStrategy getConnectionStrategy()
  {
    return this.connectionStrategy;
  }


  /** {@inheritDoc} */
  public void setConnectionStrategy(final ConnectionStrategy strategy)
  {
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("setting connectionStrategy: " + strategy);
    }
    this.connectionStrategy = strategy;
  }


  /** {@inheritDoc} */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return this.operationRetryResultCodes;
  }


  /** {@inheritDoc} */
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    this.operationRetryResultCodes = codes;
  }


  /**
   * Returns the authentication type.
   *
   * @return  authentication type
   */
  public AuthenticationType getAuthenticationType()
  {
    return this.authenticationType;
  }


  /**
   * Sets the authentication type.
   *
   * @param  type  authentication type
   */
  public void setAuthenticationType(final AuthenticationType type)
  {
    this.authenticationType = type;
  }


  /**
   * Returns whether authentication credentials will be logged.
   *
   * @return  whether authentication credentials will be logged
   */
  public boolean getLogCredentials()
  {
    return this.logCredentials;
  }


  /**
   * Sets whether authentication credentials will be logged.
   *
   * @param  b  whether authentication credentials will be logged
   */
  public void setLogCredentials(final boolean b)
  {
    this.logCredentials = b;
  }


  /** {@inheritDoc} */
  public Connection create(final String dn, final Credential credential)
    throws LdapException
  {
    LdapException lastThrown = null;
    final String[] urls = this.parseLdapUrl(
      this.ldapUrl, this.connectionStrategy);
    Connection conn = null;
    for (String url : urls) {
      try {
        if (this.logger.isTraceEnabled()) {
          this.logger.trace(
            "{" + this.connectionCount + "} Attempting connection to " + url +
            " for strategy " + this.connectionStrategy);
        }
        conn = this.createInternal(url, dn, credential);
        this.connectionCount.incrementCount();
        lastThrown = null;
        break;
      } catch (ConnectionException e) {
        lastThrown = e;
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Error connecting to LDAP URL: " + url, e);
        }
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
  protected abstract Connection createInternal(
    final String url, final String dn, final Credential credential)
    throws LdapException;


  /**
   * Parses the supplied ldap url and splits it into separate URLs if it is
   * space delimited.
   *
   * @param  ldapUrl  to parse
   * @param  strategy  of ordered array to return
   *
   * @return  array of ldap URLs
   */
  protected String[] parseLdapUrl(
    final String ldapUrl,
    final ConnectionStrategy strategy)
  {
    String[] urls = null;
    if (strategy == ConnectionStrategy.DEFAULT) {
      urls = new String[] {ldapUrl};
    } else if (strategy == ConnectionStrategy.ACTIVE_PASSIVE) {
      final List<String> l = this.splitLdapUrl(ldapUrl);
      urls = l.toArray(new String[l.size()]);
    } else if (strategy == ConnectionStrategy.ROUND_ROBIN) {
      final List<String> l = this.splitLdapUrl(ldapUrl);
      for (int i = 0; i < this.connectionCount.getCount() % l.size(); i++) {
        l.add(l.remove(0));
      }
      urls = l.toArray(new String[l.size()]);
    } else if (strategy == ConnectionStrategy.RANDOM) {
      final List<String> l = this.splitLdapUrl(ldapUrl);
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
      return this.count;
    }


    /** Increments the connection count. */
    public void incrementCount()
    {
      this.count++;
      // reset the count if it exceeds the size of an integer
      if (this.count < 0) {
        this.count = 0;
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
      return Integer.toString(this.count);
    }
  }
}
