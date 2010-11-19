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
package edu.vt.middleware.ldap.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.LdapConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AbstractConnectionHandler</code> provides a basic implementation for
 * other connection handlers to inherit.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public abstract class AbstractConnectionHandler implements ConnectionHandler
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Ldap configuration. */
  protected LdapConfig config;

  /** Ldap context. */
  protected LdapContext context;

  /** Ldap URL parser. */
  protected ConnectionStrategy connectionStrategy = ConnectionStrategy.DEFAULT;

  /** Exception types to retry connections on. */
  protected Class<?>[] connectionRetryExceptions = new Class[] {
    NamingException.class,
  };

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
  public Class<?>[] getConnectionRetryExceptions()
  {
    return this.connectionRetryExceptions;
  }


  /** {@inheritDoc} */
  public void setConnectionRetryExceptions(final Class<?>[] exceptions)
  {
    if (this.logger.isTraceEnabled()) {
      this.logger.trace(
        "setting connectionRetryExceptions: " + Arrays.toString(exceptions));
    }
    this.connectionRetryExceptions = exceptions;
  }


  /** {@inheritDoc} */
  public LdapConfig getLdapConfig()
  {
    return this.config;
  }


  /** {@inheritDoc} */
  public void setLdapConfig(final LdapConfig lc)
  {
    this.config = lc;
  }


  /** {@inheritDoc} */
  public LdapContext getLdapContext()
  {
    return this.context;
  }


  /** {@inheritDoc} */
  public void connect(final String dn, final Object credential)
    throws NamingException
  {
    final Hashtable<String, Object> env = new Hashtable<String, Object>(
      this.config.getEnvironment());
    NamingException lastThrown = null;
    final String[] urls = this.parseLdapUrl(
      this.config.getLdapUrl(),
      this.connectionStrategy);
    for (String url : urls) {
      env.put(LdapConstants.PROVIDER_URL, url);
      try {
        if (this.logger.isTraceEnabled()) {
          this.logger.trace(
            "{" + this.connectionCount + "} Attempting connection to " + url +
            " for strategy " + this.connectionStrategy);
        }
        this.connectInternal(this.config.getAuthtype(), dn, credential, env);
        this.connectionCount.incrementCount();
        lastThrown = null;
        break;
      } catch (NamingException e) {
        lastThrown = e;
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Error connecting to LDAP URL: " + url, e);
        }

        boolean ignoreException = false;
        if (
          this.connectionRetryExceptions != null &&
            this.connectionRetryExceptions.length > 0) {
          for (Class<?> ne : this.connectionRetryExceptions) {
            if (ne.isInstance(e)) {
              ignoreException = true;
              break;
            }
          }
        }
        if (!ignoreException) {
          break;
        }
      }
    }
    if (lastThrown != null) {
      throw lastThrown;
    }
  }


  /**
   * Create the initial ldap context and prepare the connection for use.
   *
   * @param  authtype  security mechanism to bind with
   * @param  dn  to bind as
   * @param  credential  to bind with in conjunction with dn
   * @param  env  to pass to the initial ldap context
   *
   * @throws  NamingException  if a connection cannot be established
   */
  protected abstract void connectInternal(
    final String authtype,
    final String dn,
    final Object credential,
    final Hashtable<String, Object> env)
    throws NamingException;


  /** {@inheritDoc} */
  public boolean isConnected()
  {
    return this.context != null;
  }


  /** {@inheritDoc} */
  public void close()
    throws NamingException
  {
    try {
      if (this.context != null) {
        this.context.close();
      }
    } finally {
      this.context = null;
    }
  }


  /** {@inheritDoc} */
  public abstract ConnectionHandler newInstance();


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
   * <code>ConnectionCount</code> provides an object to track the connection
   * count.
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
