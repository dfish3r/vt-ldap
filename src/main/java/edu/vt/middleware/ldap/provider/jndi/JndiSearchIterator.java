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

import java.net.URI;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.Response;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.SearchScope;
import edu.vt.middleware.ldap.control.ResponseControl;
import edu.vt.middleware.ldap.provider.ControlProcessor;
import edu.vt.middleware.ldap.provider.SearchIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search iterator for JNDI naming enumeration.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JndiSearchIterator implements SearchIterator
{

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
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Search request. */
  protected SearchRequest request;

  /** Control processor. */
  protected ControlProcessor<javax.naming.ldap.Control> controlProcessor;

  /** Response data. */
  protected Response<Void> response;

  /** Response result code. */
  protected ResultCode responseResultCode;

  /** Ldap context to search with. */
  protected LdapContext context;

  /** Results read from the search operation. */
  protected NamingEnumeration<SearchResult> results;

  /** Codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;

  /** Whether to remove the URL from any DNs which are not relative. */
  private boolean removeDnUrls;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;


  /**
   * Creates a new jndi search iterator.
   *
   * @param  sr  search request
   * @param  processor  control processor
   */
  public JndiSearchIterator(
    final SearchRequest sr,
    final ControlProcessor<javax.naming.ldap.Control> processor)
  {
    request = sr;
    controlProcessor = processor;
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
   * Returns the ldap result codes to retry operations on.
   *
   * @return  result codes
   */
  public ResultCode[] getOperationRetryResultCodes()
  {
    return operationRetryResultCodes;
  }


  /**
   * Sets the ldap result codes to retry operations on.
   *
   * @param  codes  result codes
   */
  public void setOperationRetryResultCodes(final ResultCode[] codes)
  {
    operationRetryResultCodes = codes;
  }


  /**
   * Returns the search ignore result codes.
   *
   * @return  result codes to ignore
   */
  public ResultCode[] getSearchIgnoreResultCodes()
  {
    return searchIgnoreResultCodes;
  }


  /**
   * Sets the search ignore result codes.
   *
   * @param  codes  to ignore
   */
  public void setSearchIgnoreResultCodes(final ResultCode[] codes)
  {
    searchIgnoreResultCodes = codes;
  }


  /**
   * Initializes this jndi search iterator.
   *
   * @param  ctx  to call {@link LdapContext#newInstance(Control[])} on
   * @throws  LdapException  if an error occurs
   */
  public void initialize(final LdapContext ctx)
    throws LdapException
  {
    boolean closeContext = false;
    try {
      context = ctx.newInstance(
        controlProcessor.processRequestControls(request.getControls()));
      initializeSearchContext(context, request);
      results = search(context, request);
    } catch (NamingException e) {
      closeContext = true;
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor, request.getControls(), context));
    } finally {
      if (closeContext) {
        try {
          if (context != null) {
            context.close();
          }
        } catch (NamingException e) {
          logger.debug("Problem closing context", e);
        }
      }
    }
  }


  /**
   * Adds any additional environment properties found in the supplied request to
   * the supplied context.
   *
   * @param  ctx  to initialize for searching
   * @param  sr  to read properties from
   * @throws  NamingException  if a property cannot be added to the context
   */
  protected void initializeSearchContext(
    final LdapContext ctx, final SearchRequest sr)
    throws NamingException
  {
    if (sr.getReferralBehavior() != null) {
      ctx.addToEnvironment(
        REFERRAL, sr.getReferralBehavior().name().toLowerCase());
    }
    if (sr.getDerefAliases() != null) {
      ctx.addToEnvironment(
        DEREF_ALIASES, sr.getDerefAliases().name().toLowerCase());
    }
    if (sr.getBinaryAttributes() != null) {
      final String[] a = sr.getBinaryAttributes();
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < a.length; i++) {
        sb.append(a[i]);
        if (i < a.length - 1) {
          sb.append(" ");
        }
      }
      ctx.addToEnvironment(BINARY_ATTRIBUTES, sb.toString());
    }
    if (sr.getTypesOnly()) {
      ctx.addToEnvironment(
        TYPES_ONLY, Boolean.valueOf(sr.getTypesOnly()).toString());
    }
  }


  /**
   * Executes {@link LdapContext#search(
   * javax.naming.Name, String, Object[], SearchControls)}.
   *
   * @param  ctx  to search
   * @param  sr  to read properties from
   * @return  naming enumeration of search results
   * @throws NamingException  if an error occurs
   */
  protected NamingEnumeration<SearchResult> search(
    final LdapContext ctx, final SearchRequest sr)
    throws NamingException
  {
    return ctx.search(
      sr.getBaseDn(),
      sr.getSearchFilter() != null ?
        sr.getSearchFilter().getFilter() : null,
      sr.getSearchFilter() != null ?
        sr.getSearchFilter().getFilterArgs().toArray() : null,
      getSearchControls(sr));
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
  protected static SearchControls getSearchControls(final SearchRequest sr)
  {
    final SearchControls ctls = new SearchControls();
    ctls.setReturningAttributes(sr.getReturnAttributes());
    final int searchScope = getSearchScope(sr.getSearchScope());
    if (searchScope != -1) {
      ctls.setSearchScope(searchScope);
    }
    ctls.setTimeLimit(Long.valueOf(sr.getTimeLimit()).intValue());
    ctls.setCountLimit(sr.getSizeLimit());
    ctls.setDerefLinkFlag(false);
    // note that if returning obj flag is set to true, object contexts on the
    // SearchResult must the explicitly closed:
    // ctx = (Context) SearchResult#getObject(); ctx.close();
    ctls.setReturningObjFlag(false);
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


  /** {@inheritDoc} */
  @Override
  public boolean hasNext()
    throws LdapException
  {
    if (results == null || response != null) {
      return false;
    }
    boolean more = false;
    try {
      more = results.hasMore();
      if (!more) {
        final ResponseControl[] respControls =
          JndiUtil.processResponseControls(
            controlProcessor, request.getControls(), context);
        final boolean searchAgain = ControlProcessor.searchAgain(respControls);
        if (searchAgain) {
          context.setRequestControls(
            controlProcessor.processRequestControls(request.getControls()));
          results = search(context, request);
          more = results.hasMore();
        } else {
          response = new Response<Void>(
            null,
            responseResultCode != null ?
              responseResultCode : ResultCode.SUCCESS,
            respControls);
        }
      }
    } catch (NamingException e) {
      final ResultCode rc = ignoreSearchException(searchIgnoreResultCodes, e);
      if (rc == null) {
        JndiUtil.throwOperationException(
          operationRetryResultCodes,
          e,
          JndiUtil.processResponseControls(
            controlProcessor, request.getControls(), context));
      }
      response = new Response<Void>(
        null,
        rc,
        JndiUtil.processResponseControls(
          controlProcessor, request.getControls(), context));
    }
    return more;
  }


  /** {@inheritDoc} */
  @Override
  public LdapEntry next()
    throws LdapException
  {
    final JndiUtil bu = new JndiUtil(request.getSortBehavior());
    LdapEntry le = null;
    try {
      final SearchResult result = results.next();
      logger.trace("reading search result: {}", request);
      result.setName(formatDn(result, getSearchDn(context, request)));
      le = bu.toLdapEntry(result);
    } catch (NamingException e) {
      final ResultCode rc = ignoreSearchException(searchIgnoreResultCodes, e);
      if (rc == null) {
        JndiUtil.throwOperationException(
          operationRetryResultCodes,
          e,
          JndiUtil.processResponseControls(
            controlProcessor, request.getControls(), context));
      }
      responseResultCode = rc;
    }
    return le;
  }


  /**
   * Determines whether the supplied naming exception should be ignored.
   *
   * @param  ignoreResultCodes  to match against the exception
   * @param  e  naming exception to match
   * @return  result code that should be ignored or null
   */
  protected ResultCode ignoreSearchException(
    final ResultCode[] ignoreResultCodes, final NamingException e)
  {
    ResultCode ignore = null;
    if (ignoreResultCodes != null && ignoreResultCodes.length > 0) {
      for (ResultCode rc : ignoreResultCodes) {
        if (NamingExceptionUtil.matches(e.getClass(), rc)) {
          logger.debug("Ignoring naming exception", e);
          ignore = rc;
          break;
        }
      }
    }
    return ignore;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> getResponse()
  {
    return response;
  }


  /**
   * Determines the DN of the supplied search request. Returns
   * {@link LdapContext#getNameInNamespace()} if it is available, otherwise
   * returns {@link SearchRequest#getBaseDn()}.
   *
   * @param  ctx  ldap context the search was performed on
   * @param  sr  search request
   * @return  DN
   * @throws  NamingException  if an error occurs
   */
  protected String getSearchDn(final LdapContext ctx, final SearchRequest sr)
    throws NamingException
  {
    if (ctx != null && !"".equals(ctx.getNameInNamespace())) {
      return ctx.getNameInNamespace();
    } else {
      return sr.getBaseDn();
    }
  }


  /**
   * Returns a fully-qualified DN for the supplied search result. If search
   * result is relative, the DN is created by concatenating the relative name
   * with the base DN. Otherwise the behavior is controlled by {@link
   * #removeDnUrls}.
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
        logger.trace("formatting relative dn {}", resultName);
        if (baseDn != null) {
          if (!"".equals(resultName)) {
            fqName = new StringBuilder(
              readCompositeName(resultName)).append(",").append(baseDn);
          } else {
            fqName = new StringBuilder(baseDn);
          }
        } else {
          fqName = new StringBuilder(readCompositeName(resultName));
        }
      } else {
        logger.trace("formatting non-relative dn {}", resultName);
        if (removeDnUrls) {
          fqName = new StringBuilder(
            readCompositeName(URI.create(resultName).getPath().substring(1)));
        } else {
          fqName = new StringBuilder(readCompositeName(resultName));
        }
      }
      newDn = fqName.toString();
    }
    logger.trace("formatted dn {} as {}", resultName, newDn);
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
  protected String readCompositeName(final String s)
    throws InvalidNameException
  {
    final StringBuffer name = new StringBuffer();
    final CompositeName cName = new CompositeName(s);
    for (int i = 0; i < cName.size(); i++) {
      name.append(cName.get(i));
      if (i + 1 < cName.size()) {
        name.append("/");
      }
    }
    return name.toString();
  }


  /** {@inheritDoc} */
  @Override
  public void close()
    throws LdapException
  {
    try {
      if (results != null) {
        results.close();
      }
    } catch (NamingException e) {
      logger.error("Error closing naming enumeration", e);
    }
    try {
      if (context != null) {
        context.close();
      }
    } catch (NamingException e) {
      logger.error("Error closing ldap context", e);
    }
  }
}
