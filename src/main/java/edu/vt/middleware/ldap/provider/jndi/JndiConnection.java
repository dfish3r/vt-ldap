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

import java.io.IOException;
import java.net.URI;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;
import edu.vt.middleware.ldap.AddRequest;
import edu.vt.middleware.ldap.CompareRequest;
import edu.vt.middleware.ldap.DeleteRequest;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.ModifyRequest;
import edu.vt.middleware.ldap.OperationException;
import edu.vt.middleware.ldap.PagedSearchRequest;
import edu.vt.middleware.ldap.RenameRequest;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.SearchScope;
import edu.vt.middleware.ldap.SortBehavior;
import edu.vt.middleware.ldap.provider.Connection;
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
  /**
   * The value of this property is a string of decimal digits that specifies the
   * batch size of search results returned by the server. The value of this
   * constant is {@value}.
   */
  public static final String BATCH_SIZE = "java.naming.batchsize";

  /**
   * The value of this property is a string that specifies additional binary
   * attributes. The value of this constant is {@value}.
   */
  public static final String BINARY_ATTRIBUTES =
    "java.naming.ldap.attributes.binary";

  /**
   * The value of this property is a string that specifies how aliases shall be
   * handled by the provider. The value of this constant is {@value}.
   */
  public static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";

  /**
   * The value of this property is a string that specifies how referrals shall
   * be handled by the provider. The value of this constant is {@value}.
   */
  public static final String REFERRAL = "java.naming.referral";

  /**
   * The value of this property is a string that specifies to only return
   * attribute type names, no values. The value of this constant is {@value}.
   */
  public static final String TYPES_ONLY = "java.naming.ldap.typesOnly";

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    this.context = lc;
  }


  /**
   * Returns whether the URL will be removed from any DNs which are not
   * relative. The default value is true.
   *
   * @return  whether the URL will be removed from DNs
   */
  public boolean getRemoveDnUrls()
  {
    return this.removeDnUrls;
  }


  /**
   * Sets whether the URL will be removed from any DNs which are not relative
   * The default value is true.
   *
   * @param  b  whether the URL will be removed from DNs
   */
  public void setRemoveDnUrls(final boolean b)
  {
    this.removeDnUrls = b;
  }


  /**
   * Returns the naming exceptions to retry operations on.
   *
   * @return  naming exceptions
   */
  public Class<?>[] getOperationRetryExceptions()
  {
    return this.operationRetryExceptions;
  }


  /**
   * Sets the naming exceptions to retry operations on.
   *
   * @param  exceptions  naming exceptions
   */
  public void setOperationRetryExceptions(final Class<?>[] exceptions)
  {
    this.operationRetryExceptions = exceptions;
  }


  /**
   * Returns the underlying ldap context.
   *
   * @return  ldap context
   */
  public LdapContext getLdapContext()
  {
    return this.context;
  }


  /** {@inheritDoc} */
  public void close()
    throws LdapException
  {
    try {
      if (this.context != null) {
        this.context.close();
      }
    } catch (NamingException e) {
      throw new LdapException(
        e, NamingExceptionUtil.getResultCode(e.getClass()));
    } finally {
      this.context = null;
    }
  }


  /** {@inheritDoc} */
  public void add(final AddRequest request)
    throws LdapException
  {
    try {
      LdapContext ctx = null;
      try {
        ctx = this.context.newInstance(null);
        final BeanUtil bu = new BeanUtil();
        ctx.createSubcontext(
          new LdapName(request.getDn()),
          bu.fromLdapAttributes(request.getLdapAttributes())).close();
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      this.throwOperationException(e);
    }
  }


  /** {@inheritDoc} */
  public boolean compare(final CompareRequest request)
    throws LdapException
  {
    boolean success = false;
    try {
      LdapContext ctx = null;
      NamingEnumeration<SearchResult> en = null;
      try {
        ctx = this.context.newInstance(null);
        en = ctx.search(
          new LdapName(request.getDn()),
          String.format("(%s={0})", request.getAttribute().getName()),
          new Object[] {request.getAttribute().getValue()},
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
      this.throwOperationException(e);
    }
    return success;
  }


  /** {@inheritDoc} */
  public void delete(final DeleteRequest request)
    throws LdapException
  {
    try {
      LdapContext ctx = null;
      try {
        ctx = this.context.newInstance(null);
        ctx.destroySubcontext(new LdapName(request.getDn()));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      this.throwOperationException(e);
    }
  }


  /** {@inheritDoc} */
  public void modify(final ModifyRequest request)
    throws LdapException
  {
    try {
      LdapContext ctx = null;
      try {
        ctx = this.context.newInstance(null);
        final BeanUtil bu = new BeanUtil();
        ctx.modifyAttributes(
          new LdapName(request.getDn()),
          bu.fromAttributeModification(request.getAttributeModifications()));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      this.throwOperationException(e);
    }
  }


  /** {@inheritDoc} */
  public void rename(final RenameRequest request)
    throws LdapException
  {
    try {
      LdapContext ctx = null;
      try {
        ctx = this.context.newInstance(null);
        ctx.rename(
          new LdapName(request.getDn()),
          new LdapName(request.getNewDn()));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      this.throwOperationException(e);
    }
  }


  /** {@inheritDoc} */
  public LdapResult pagedSearch(final PagedSearchRequest request)
    throws LdapException
  {
    LdapResult result = null;
    try {
      LdapContext ctx = null;
      NamingEnumeration<SearchResult> en = null;
      try {
        byte[] cookie = null;
        ctx = this.context.newInstance(null);
        ctx.setRequestControls(
          new Control[] {
            new PagedResultsControl(
              request.getPagedResultsSize(), Control.CRITICAL),
          });
        result = new LdapResult(request.getSortBehavior());
        final SearchControls controls = getSearchControls(request);
        do {
          en = ctx.search(
            request.getBaseDn(),
            request.getSearchFilter() != null ?
              request.getSearchFilter().getFilter() : null,
            request.getSearchFilter() != null ?
              request.getSearchFilter().getFilterArgs().toArray() : null,
            controls);

          request.setBaseDn(this.getSearchDn(request, ctx));
          final LdapResult pagedResults = this.readSearchResults(
            request.getBaseDn(),
            en,
            request.getSearchIgnoreResultCodes(),
            request.getSortBehavior());
          if (pagedResults != null) {
            for (LdapEntry le : pagedResults.getEntries()) {
              result.addEntry(le);
            }
          }

          final Control[] responseControls = ctx.getResponseControls();
          if (responseControls != null) {
            for (int j = 0; j < responseControls.length; j++) {
              if (responseControls[j] instanceof PagedResultsResponseControl) {
                final PagedResultsResponseControl prrc =
                  (PagedResultsResponseControl) responseControls[j];
                cookie = prrc.getCookie();
              }
            }
          }

          // re-activate paged results
          ctx.setRequestControls(
            new Control[] {
              new PagedResultsControl(
                request.getPagedResultsSize(), cookie, Control.CRITICAL),
            });

        } while (cookie != null);
      } catch (IOException e) {
        this.logger.error("Could not encode page size into control", e);
        throw new NamingException(e.getMessage());
      } finally {
        if (en != null) {
          en.close();
        }
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      this.throwOperationException(e);
    }
    return result;
  }


  /** {@inheritDoc} */
  public LdapResult search(final SearchRequest request)
    throws LdapException
  {
    LdapResult result = null;
    try {
      LdapContext ctx = null;
      NamingEnumeration<SearchResult> en = null;
      try {
        ctx = this.context.newInstance(null);
        this.initializeSearchContext(ctx, request);
        final SearchControls controls = getSearchControls(request);
        en = ctx.search(
          request.getBaseDn(),
          request.getSearchFilter() != null ?
            request.getSearchFilter().getFilter() : null,
          request.getSearchFilter() != null ?
            request.getSearchFilter().getFilterArgs().toArray() : null,
          controls);

        result = this.readSearchResults(
          this.getSearchDn(request, ctx),
          en,
          request.getSearchIgnoreResultCodes(),
          request.getSortBehavior());
      } finally {
        if (en != null) {
          en.close();
        }
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      this.throwOperationException(e);
    }
    return result;
  }


  /**
   * Determines whether to throw operation exception or ldap exception. If
   * operation exception is thrown, the operation will be retried. Otherwise
   * the exception is propagated out.
   *
   * @param  e  naming exception to examine
   * @throws  OperationException  if the operation should be retried
   * @throws  LdapException  to propagate the exception out
   */
  protected void throwOperationException(final NamingException e)
    throws LdapException
  {
    if (this.operationRetryExceptions != null &&
        this.operationRetryExceptions.length > 0) {
      for (Class<?> ne : this.operationRetryExceptions) {
        if (ne.isInstance(e)) {
          throw new OperationException(
            e, NamingExceptionUtil.getResultCode(e.getClass()));
        }
      }
    }
    throw new LdapException(e, NamingExceptionUtil.getResultCode(e.getClass()));
  }


  /**
   * Adds any additional environment properties found in the supplied request to
   * the supplied context.
   *
   * @param  ctx  to initialize for searching
   * @param  request  to read properties from
   * @throws  NamingException  if a property cannot be added to the context
   */
  protected void initializeSearchContext(
    final LdapContext ctx, final SearchRequest request)
    throws NamingException
  {
    if (request.getBatchSize() != -1) {
      ctx.addToEnvironment(
        BATCH_SIZE, Integer.toString(request.getBatchSize()));
    }
    if (request.getReferralBehavior() != null) {
      ctx.addToEnvironment(
        REFERRAL, request.getReferralBehavior().name().toLowerCase());
    }
    if (request.getDerefAliases() != null) {
      ctx.addToEnvironment(
        DEREF_ALIASES, request.getDerefAliases().name().toLowerCase());
    }
    if (request.getBinaryAttributes() != null) {
      final String[] a = request.getBinaryAttributes();
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < a.length; i++) {
        sb.append(a[i]);
        if (i < a.length - 1) {
          sb.append(" ");
        }
      }
      ctx.addToEnvironment(BINARY_ATTRIBUTES, sb.toString());
    }
    if (request.getTypesOnly()) {
      ctx.addToEnvironment(
        TYPES_ONLY, Boolean.valueOf(request.getTypesOnly()).toString());
    }
  }


  /**
   * Determines the DN of the supplied search request. Returns
   * {@link LdapContext#getNameInNamespace()} if it is available, otherwise
   * returns {@link SearchRequest#getBaseDn()}.
   *
   * @param  sr  search request
   * @param  ctx  ldap context the search was performed on
   * @return  DN
   * @throws  NamingException  if an error occurs
   */
  protected String getSearchDn(final SearchRequest sr, final LdapContext ctx)
    throws NamingException
  {
    if (ctx != null && !"".equals(ctx.getNameInNamespace())) {
      return ctx.getNameInNamespace();
    } else {
      return sr.getBaseDn();
    }
  }


  /**
   * Reads the supplied naming enumeration and returns an ldap result containing
   * that data.
   *
   * @param  baseDn  the search was performed on
   * @param  en  naming enumeration to read
   * @param  ignore  ldap result codes that should be ignored during the read
   * @param  sortBehavior  of the returned ldap result
   * @return  ldap result
   * @throws  NamingException  if the naming enumeration cannot be read
   */
  protected LdapResult readSearchResults(
    final String baseDn,
    final NamingEnumeration<SearchResult> en,
    final ResultCode[] ignore,
    final SortBehavior sortBehavior)
    throws NamingException
  {
    final BeanUtil bu = new BeanUtil(sortBehavior);
    final LdapResult ldapResult = new LdapResult(sortBehavior);
    if (en != null) {
      while (en.hasMore()) {
        try {
          final SearchResult sr = en.next();
          sr.setName(this.formatDn(sr, baseDn));
          ldapResult.addEntry(bu.toLdapEntry(sr));
        } catch (NamingException e) {
          boolean ignoreException = false;
          if (ignore != null && ignore.length > 0) {
            for (ResultCode rc : ignore) {
              if (NamingExceptionUtil.matches(e.getClass(), rc)) {
                this.logger.debug("Ignoring naming exception", e);
                ignoreException = true;
                break;
              }
            }
          }
          if (!ignoreException) {
            throw e;
          }
        }
      }
    }
    return ldapResult;
  }


  /**
   * Returns a fully-qualified DN for the supplied search result. If search
   * result is relative, the DN is created by concatenating the relative name
   * with the base DN. Otherwise {@link SearchResult#getName()} is used.
   *
   * @param  sr  to determine DN for
   * @param  baseDn  that search was performed on
   * @return  fully qualified DN
   * @throws  NamingException  if search result name cannot be formatted as a DN
   */
  protected String formatDn(final SearchResult sr, final String baseDn)
    throws NamingException
  {
    String newDn = null;
    final String resultName = sr.getName();
    if (resultName != null) {
      StringBuilder fqName = null;
      if (sr.isRelative()) {
        if (baseDn != null) {
          if (!"".equals(resultName)) {
            fqName = new StringBuilder(
              this.readCompositeName(resultName)).append(",").append(baseDn);
          } else {
            fqName = new StringBuilder(baseDn);
          }
        } else {
          fqName = new StringBuilder(this.readCompositeName(resultName));
        }
      } else {
        if (this.removeDnUrls) {
          fqName = new StringBuilder(
            URI.create(
              this.readCompositeName(resultName)).getPath().substring(1));
        } else {
          fqName = new StringBuilder(this.readCompositeName(resultName));
        }
      }
      newDn = fqName.toString();
    }
    return newDn;
  }


  /**
   * Uses a composite name to parse the supplied string.
   *
   * @param  s  composite name to read
   * @return  ldap name
   * @throws  InvalidNameException  if the supplied string is not a valid
   * composite name
   */
  private String readCompositeName(final String s)
    throws InvalidNameException
  {
    final CompositeName cName = new CompositeName(s);
    return cName.get(0);
  }


  /**
   * Returns a search controls object configured with the supplied search
   * request.
   *
   * @param  sr  search request containing configuration to create search
   * controls
   *
   * @return  search controls
   */
  public static SearchControls getSearchControls(final SearchRequest sr)
  {
    final SearchControls ctls = new SearchControls();
    ctls.setReturningAttributes(sr.getReturnAttributes());
    final int searchScope = getSearchScope(sr.getSearchScope());
    if (searchScope != -1) {
      ctls.setSearchScope(searchScope);
    }
    ctls.setTimeLimit(Long.valueOf(sr.getTimeLimit()).intValue());
    ctls.setCountLimit(sr.getCountLimit());
    ctls.setDerefLinkFlag(false);
    // note that if returning obj flag is set to true, object contexts on the
    // SearchResult must the explicitly closed:
    // ctx = (Context) SearchResult#getObject(); ctx.close();
    ctls.setReturningObjFlag(false);
    return ctls;
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


  /**
   * Returns the jndi integer constant for the supplied search scope.
   *
   * @param  ss  search scope
   * @return  integer constant
   */
  protected static int getSearchScope(final SearchScope ss)
  {
    int scope = -1;
    if (ss == SearchScope.OBJECT) {
      scope = SearchControls.OBJECT_SCOPE;
    } else if (ss == SearchScope.ONELEVEL) {
      scope = SearchControls.ONELEVEL_SCOPE;
    } else if (ss == SearchScope.SUBTREE) {
      scope = SearchControls.SUBTREE_SCOPE;
    }
    return scope;
  }
}
