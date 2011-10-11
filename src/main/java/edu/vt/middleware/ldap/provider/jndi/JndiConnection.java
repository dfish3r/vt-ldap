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
package edu.vt.middleware.ldap.provider.jndi;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import edu.vt.middleware.ldap.AddRequest;
import edu.vt.middleware.ldap.BindRequest;
import edu.vt.middleware.ldap.CompareRequest;
import edu.vt.middleware.ldap.DeleteRequest;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ModifyRequest;
import edu.vt.middleware.ldap.RenameRequest;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.SearchScope;
import edu.vt.middleware.ldap.auth.AuthenticationException;
import edu.vt.middleware.ldap.provider.Connection;
import edu.vt.middleware.ldap.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JNDI provider implementation of ldap operations.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class JndiConnection implements Connection
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap context. */
  protected LdapContext context;

  /** Whether to remove the URL from any DNs which are not relative. */
  private boolean removeDnUrls;

  /** Exceptions to retry operations on. */
  private Class<?>[] operationRetryExceptions;


  /**
   * Creates a new jndi connection.
   *
   * @param  lc  ldap context
   */
  public JndiConnection(final LdapContext lc)
  {
    context = lc;
  }


  /**
   * Returns whether the URL will be removed from any DNs which are not
   * relative. The default value is true.
   *
   * @return  whether the URL will be removed from DNs
   */
  public boolean getRemoveDnUrls()
  {
    return removeDnUrls;
  }


  /**
   * Sets whether the URL will be removed from any DNs which are not relative
   * The default value is true.
   *
   * @param  b  whether the URL will be removed from DNs
   */
  public void setRemoveDnUrls(final boolean b)
  {
    removeDnUrls = b;
  }


  /**
   * Returns the naming exceptions to retry operations on.
   *
   * @return  naming exceptions
   */
  public Class<?>[] getOperationRetryExceptions()
  {
    return operationRetryExceptions;
  }


  /**
   * Sets the naming exceptions to retry operations on.
   *
   * @param  exceptions  naming exceptions
   */
  public void setOperationRetryExceptions(final Class<?>[] exceptions)
  {
    operationRetryExceptions = exceptions;
  }


  /**
   * Returns the underlying ldap context.
   *
   * @return  ldap context
   */
  public LdapContext getLdapContext()
  {
    return context;
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    try {
      if (context != null) {
        context.close();
      }
    } catch (NamingException e) {
      throw new LdapException(
        e, NamingExceptionUtil.getResultCode(e.getClass()));
    } finally {
      context = null;
    }
  }


  /** {@inheritDoc} */
  @Override
  public void bind()
    throws LdapException
  {
    try {
      context.addToEnvironment(Context.SECURITY_AUTHENTICATION, "none");
      context.removeFromEnvironment(Context.SECURITY_PRINCIPAL);
      context.removeFromEnvironment(Context.SECURITY_CREDENTIALS);
      context.reconnect(context.getConnectControls());
    } catch (javax.naming.AuthenticationException e) {
      throw new AuthenticationException(e, ResultCode.INVALID_CREDENTIALS);
    } catch (NamingException e) {
      JndiUtil.throwOperationException(operationRetryExceptions, e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public void bind(final BindRequest request)
    throws LdapException
  {
    String authenticationType = "simple";
    if (request.isSaslRequest()) {
      authenticationType = JndiUtil.getAuthenticationType(
        request.getSaslConfig().getMechanism());
    }
    try {
      context.addToEnvironment(
        Context.SECURITY_AUTHENTICATION, authenticationType);
      context.addToEnvironment(Context.SECURITY_PRINCIPAL, request.getBindDn());
      context.addToEnvironment(
        Context.SECURITY_CREDENTIALS, request.getBindCredential().getBytes());
      context.reconnect(JndiUtil.fromControls(request.getControls()));
    } catch (javax.naming.AuthenticationException e) {
      throw new AuthenticationException(e, ResultCode.INVALID_CREDENTIALS);
    } catch (NamingException e) {
      JndiUtil.throwOperationException(operationRetryExceptions, e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public void add(final AddRequest request)
    throws LdapException
  {
    try {
      LdapContext ctx = null;
      try {
        ctx = context.newInstance(JndiUtil.fromControls(request.getControls()));
        final JndiUtil bu = new JndiUtil();
        ctx.createSubcontext(
          new LdapName(request.getDn()),
          bu.fromLdapAttributes(request.getLdapAttributes())).close();
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(operationRetryExceptions, e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public boolean compare(final CompareRequest request)
    throws LdapException
  {
    boolean success = false;
    try {
      LdapContext ctx = null;
      NamingEnumeration<SearchResult> en = null;
      try {
        ctx = context.newInstance(JndiUtil.fromControls(request.getControls()));
        en = ctx.search(
          new LdapName(request.getDn()),
          String.format("(%s={0})", request.getAttribute().getName()),
          request.getAttribute().isBinary() ?
            new Object[] {request.getAttribute().getBinaryValue()} :
            new Object[] {request.getAttribute().getStringValue()},
          getCompareSearchControls());

        if (en.hasMore()) {
          success = true;
        }
      } finally {
        if (en != null) {
          en.close();
        }
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(operationRetryExceptions, e);
    }
    return success;
  }


  /** {@inheritDoc} */
  @Override
  public void delete(final DeleteRequest request)
    throws LdapException
  {
    try {
      LdapContext ctx = null;
      try {
        ctx = context.newInstance(JndiUtil.fromControls(request.getControls()));
        ctx.destroySubcontext(new LdapName(request.getDn()));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(operationRetryExceptions, e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public void modify(final ModifyRequest request)
    throws LdapException
  {
    try {
      LdapContext ctx = null;
      try {
        ctx = context.newInstance(JndiUtil.fromControls(request.getControls()));
        final JndiUtil bu = new JndiUtil();
        ctx.modifyAttributes(
          new LdapName(request.getDn()),
          bu.fromAttributeModification(request.getAttributeModifications()));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(operationRetryExceptions, e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public void rename(final RenameRequest request)
    throws LdapException
  {
    try {
      LdapContext ctx = null;
      try {
        ctx = context.newInstance(JndiUtil.fromControls(request.getControls()));
        ctx.rename(
          new LdapName(request.getDn()),
          new LdapName(request.getNewDn()));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(operationRetryExceptions, e);
    }
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(final SearchRequest request)
    throws LdapException
  {
    final JndiSearchIterator i = new JndiSearchIterator(request);
    i.setRemoveDnUrls(removeDnUrls);
    i.setOperationRetryExceptions(operationRetryExceptions);
    i.initialize(context);
    return i;
  }


  /**
   * Returns a search controls object configured to perform an LDAP compare
   * operation.
   *
   * @return  search controls
   */
  public static SearchControls getCompareSearchControls()
  {
    final SearchControls ctls = new SearchControls();
    ctls.setReturningAttributes(new String[0]);
    ctls.setSearchScope(SearchScope.OBJECT.ordinal());
    return ctls;
  }
}
