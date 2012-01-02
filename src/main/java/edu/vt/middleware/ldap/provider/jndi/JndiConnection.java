/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.provider.jndi;

import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import edu.vt.middleware.ldap.AddRequest;
import edu.vt.middleware.ldap.BindRequest;
import edu.vt.middleware.ldap.CompareRequest;
import edu.vt.middleware.ldap.DeleteRequest;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.ModifyRequest;
import edu.vt.middleware.ldap.RenameRequest;
import edu.vt.middleware.ldap.Response;
import edu.vt.middleware.ldap.ResultCode;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.SearchScope;
import edu.vt.middleware.ldap.provider.Connection;
import edu.vt.middleware.ldap.provider.ControlProcessor;
import edu.vt.middleware.ldap.provider.SearchIterator;
import edu.vt.middleware.ldap.sasl.DigestMd5Config;
import edu.vt.middleware.ldap.sasl.SaslConfig;
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
   * The value of this property is a string that specifies the authentication
   * mechanism(s) for the provider to use. The value of this constant is {@value
   * }.
   */
  public static final String AUTHENTICATION =
    "java.naming.security.authentication";

  /**
   * The value of this property is an object that specifies the credentials of
   * the principal to be authenticated. The value of this constant is {@value}.
   */
  public static final String CREDENTIALS = "java.naming.security.credentials";

  /**
   * The value of this property is a string that specifies the identity of the
   * principal to be authenticated. The value of this constant is {@value}.
   */
  public static final String PRINCIPAL = "java.naming.security.principal";

  /**
   * The value of this property is a string that specifies the sasl
   * authorization id. The value of this constant is {@value}.
   */
  public static final String SASL_AUTHZ_ID =
    "java.naming.security.sasl.authorizationId";

  /**
   * The value of this property is a string that specifies the sasl quality of
   * protection. The value of this constant is {@value}.
   */
  public static final String SASL_QOP = "javax.security.sasl.qop";

  /**
   * The value of this property is a string that specifies the sasl security
   * strength. The value of this constant is {@value}.
   */
  public static final String SASL_STRENGTH = "javax.security.sasl.strength";

  /**
   * The value of this property is a string that specifies the sasl mutual
   * authentication flag. The value of this constant is {@value}.
   */
  public static final String SASL_MUTUAL_AUTH =
    "javax.security.sasl.server.authentication";

  /**
   * The value of this property is a string that specifies the sasl realm. The
   * value of this constant is {@value}.
   */
  public static final String SASL_REALM = "java.naming.security.sasl.realm";

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Ldap context. */
  private LdapContext context;

  /** Whether to remove the URL from any DNs which are not relative. */
  private boolean removeDnUrls;

  /** Codes to retry operations on. */
  private ResultCode[] operationRetryResultCodes;

  /** Search result codes to ignore. */
  private ResultCode[] searchIgnoreResultCodes;

  /** Control processor. */
  private ControlProcessor<Control> controlProcessor;


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
   * Returns the control processor.
   *
   * @return  control processor
   */
  public ControlProcessor<Control> getControlProcessor()
  {
    return controlProcessor;
  }


  /**
   * Sets the control processor.
   *
   * @param  processor  control processor
   */
  public void setControlProcessor(final ControlProcessor<Control> processor)
  {
    controlProcessor = processor;
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
        e,
        NamingExceptionUtil.getResultCode(e.getClass()));
    } finally {
      context = null;
    }
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> bind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    if (request.getSaslConfig() != null) {
      response = saslBind(request);
    } else if (request.getDn() == null && request.getCredential() == null) {
      response = anonymousBind(request);
    } else {
      response = simpleBind(request);
    }
    return response;
  }


  /**
   * Performs an anonymous bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> anonymousBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      context.addToEnvironment(AUTHENTICATION, "none");
      context.removeFromEnvironment(PRINCIPAL);
      context.removeFromEnvironment(CREDENTIALS);
      context.reconnect(
        controlProcessor.processRequestControls(request.getControls()));
      response = new Response<Void>(
        null,
        ResultCode.SUCCESS,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          context));
    } catch (NamingException e) {
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          context));
    }
    return response;
  }


  /**
   * Performs a simple bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> simpleBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      context.addToEnvironment(AUTHENTICATION, "simple");
      context.addToEnvironment(PRINCIPAL, request.getDn());
      context.addToEnvironment(CREDENTIALS, request.getCredential().getBytes());
      context.reconnect(
        controlProcessor.processRequestControls(request.getControls()));
      response = new Response<Void>(
        null,
        ResultCode.SUCCESS,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          context));
    } catch (NamingException e) {
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          context));
    }
    return response;
  }


  /**
   * Performs a sasl bind.
   *
   * @param  request  to bind with
   *
   * @return  bind response
   *
   * @throws  LdapException  if an error occurs
   */
  protected Response<Void> saslBind(final BindRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    try {
      final String authenticationType = JndiUtil.getAuthenticationType(
        request.getSaslConfig().getMechanism());
      for (
        Map.Entry<String, Object> entry :
        getSaslProperties(request.getSaslConfig()).entrySet()) {
        context.addToEnvironment(entry.getKey(), entry.getValue());
      }
      context.addToEnvironment(AUTHENTICATION, authenticationType);
      if (request.getDn() != null) {
        context.addToEnvironment(PRINCIPAL, request.getDn());
        if (request.getCredential() != null) {
          context.addToEnvironment(
            CREDENTIALS,
            request.getCredential().getBytes());
        }
      }
      context.reconnect(
        controlProcessor.processRequestControls(request.getControls()));
      response = new Response<Void>(
        null,
        ResultCode.SUCCESS,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          context));
    } catch (NamingException e) {
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          context));
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> add(final AddRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    LdapContext ctx = null;
    try {
      try {
        ctx = context.newInstance(
          controlProcessor.processRequestControls(request.getControls()));

        final JndiUtil bu = new JndiUtil();
        ctx.createSubcontext(
          new LdapName(request.getDn()),
          bu.fromLdapAttributes(request.getLdapAttributes())).close();
        response = new Response<Void>(
          null,
          ResultCode.SUCCESS,
          JndiUtil.processResponseControls(
            controlProcessor,
            request.getControls(),
            ctx));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          ctx));
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Boolean> compare(final CompareRequest request)
    throws LdapException
  {
    Response<Boolean> response = null;
    LdapContext ctx = null;
    try {
      NamingEnumeration<SearchResult> en = null;
      try {
        ctx = context.newInstance(
          controlProcessor.processRequestControls(request.getControls()));
        en = ctx.search(
          new LdapName(request.getDn()),
          String.format("(%s={0})", request.getAttribute().getName()),
          request.getAttribute().isBinary() ?
            new Object[] {request.getAttribute().getBinaryValue()} :
            new Object[] {request.getAttribute().getStringValue()},
          getCompareSearchControls());

        final boolean success = en.hasMore() ? true : false;
        response = new Response<Boolean>(
          success,
          success ? ResultCode.COMPARE_TRUE : ResultCode.COMPARE_FALSE,
          JndiUtil.processResponseControls(
            controlProcessor,
            request.getControls(),
            ctx));
      } finally {
        if (en != null) {
          en.close();
        }
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          ctx));
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> delete(final DeleteRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    LdapContext ctx = null;
    try {
      try {
        ctx = context.newInstance(
          controlProcessor.processRequestControls(request.getControls()));
        ctx.destroySubcontext(new LdapName(request.getDn()));
        response = new Response<Void>(
          null,
          ResultCode.SUCCESS,
          JndiUtil.processResponseControls(
            controlProcessor,
            request.getControls(),
            ctx));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          ctx));
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> modify(final ModifyRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    LdapContext ctx = null;
    try {
      try {
        ctx = context.newInstance(
          controlProcessor.processRequestControls(request.getControls()));

        final JndiUtil bu = new JndiUtil();
        ctx.modifyAttributes(
          new LdapName(request.getDn()),
          bu.fromAttributeModification(request.getAttributeModifications()));
        response = new Response<Void>(
          null,
          ResultCode.SUCCESS,
          JndiUtil.processResponseControls(
            controlProcessor,
            request.getControls(),
            ctx));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          ctx));
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public Response<Void> rename(final RenameRequest request)
    throws LdapException
  {
    Response<Void> response = null;
    LdapContext ctx = null;
    try {
      try {
        ctx = context.newInstance(
          controlProcessor.processRequestControls(request.getControls()));
        ctx.rename(
          new LdapName(request.getDn()),
          new LdapName(request.getNewDn()));
        response = new Response<Void>(
          null,
          ResultCode.SUCCESS,
          JndiUtil.processResponseControls(
            controlProcessor,
            request.getControls(),
            ctx));
      } finally {
        if (ctx != null) {
          ctx.close();
        }
      }
    } catch (NamingException e) {
      JndiUtil.throwOperationException(
        operationRetryResultCodes,
        e,
        JndiUtil.processResponseControls(
          controlProcessor,
          request.getControls(),
          ctx));
    }
    return response;
  }


  /** {@inheritDoc} */
  @Override
  public SearchIterator search(final SearchRequest request)
    throws LdapException
  {
    final JndiSearchIterator i = new JndiSearchIterator(
      request,
      controlProcessor);
    i.setRemoveDnUrls(removeDnUrls);
    i.setOperationRetryResultCodes(operationRetryResultCodes);
    i.setSearchIgnoreResultCodes(searchIgnoreResultCodes);
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


  /**
   * Returns the JNDI properties for the supplied sasl configuration.
   *
   * @param  config  sasl configuration
   *
   * @return  JNDI properties for use in a context environment
   */
  protected static Map<String, Object> getSaslProperties(
    final SaslConfig config)
  {
    final Map<String, Object> env = new HashMap<String, Object>();
    if (config.getAuthorizationId() != null) {
      env.put(SASL_AUTHZ_ID, config.getAuthorizationId());
    }
    if (config.getQualityOfProtection() != null) {
      env.put(
        SASL_QOP,
        JndiUtil.getQualityOfProtection(config.getQualityOfProtection()));
    }
    if (config.getSecurityStrength() != null) {
      env.put(
        SASL_STRENGTH,
        JndiUtil.getSecurityStrength(config.getSecurityStrength()));
    }
    if (config.getMutualAuthentication() != null) {
      env.put(SASL_MUTUAL_AUTH, config.getMutualAuthentication().toString());
    }
    if (config instanceof DigestMd5Config) {
      if (((DigestMd5Config) config).getRealm() != null) {
        env.put(SASL_REALM, ((DigestMd5Config) config).getRealm());
      }
    }
    return env;
  }
}
