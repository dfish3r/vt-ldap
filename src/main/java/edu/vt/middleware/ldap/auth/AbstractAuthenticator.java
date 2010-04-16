/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.auth;

import java.util.Arrays;
import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import edu.vt.middleware.ldap.LdapConstants;
import edu.vt.middleware.ldap.LdapUtil;
import edu.vt.middleware.ldap.auth.handler.AuthenticationCriteria;
import edu.vt.middleware.ldap.auth.handler.AuthenticationHandler;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.handler.ConnectionHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AbstractAuthenticator</code> provides basic functionality for
 * authenticating against an LDAP.
 *
 * @param  <T>  type of AuthenticatorConfig
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractAuthenticator<T extends AuthenticatorConfig>
{
  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** Authenticator configuration environment. */
  protected T config;


  /**
   * This will set the config parameters of this <code>Authenticator</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public void setAuthenticatorConfig(final T authConfig)
  {
    if (this.config != null) {
      this.config.checkImmutable();
    }
    this.config = authConfig;
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied dn and
   * credential. See {@link #authenticateAndAuthorize( String, Object, boolean,
   * String[], AuthenticationResultHandler[], AuthorizationHandler[])}.
   *
   * @param  dn  <code>String</code> for bind
   * @param  credential  <code>Object</code> for bind
   * @param  authResultHandler  <code>AuthenticationResultHandler[]</code>
   * to post
   * process authentication results
   * @param  authzHandler  <code>AuthorizationHandler[]</code> to process
   * authorization after authentication
   *
   * @return  <code>boolean</code> - whether the bind succeeded
   *
   * @throws  NamingException  if the authentication fails for any other reason
   * than invalid credentials
   */
  protected boolean authenticateAndAuthorize(
    final String dn,
    final Object credential,
    final AuthenticationResultHandler[] authResultHandler,
    final AuthorizationHandler[] authzHandler)
    throws NamingException
  {
    boolean success = false;
    try {
      this.authenticateAndAuthorize(
        dn,
        credential,
        false,
        null,
        authResultHandler,
        authzHandler);
      success = true;
    } catch (AuthenticationException e) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Authentication failed for dn: " + dn, e);
      }
    } catch (AuthorizationException e) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Authorization failed for dn: " + dn, e);
      }
    }
    return success;
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied dn and
   * credential. Authentication will never succeed if {@link
   * AuthenticatorConfig#getAuthtype()} is set to 'none'. If retAttrs is null
   * and searchAttrs is true then all user attributes will be returned. If
   * retAttrs is an empty array and searchAttrs is true then no attributes will
   * be returned. This method throws AuthenticationException if authentication
   * fails and AuthorizationException if authorization fails.
   *
   * @param  dn  <code>String</code> for bind
   * @param  credential  <code>Object</code> for bind
   * @param  searchAttrs  <code>boolean</code> whether to perform attribute
   * search
   * @param  retAttrs  <code>String[]</code> user attributes to return
   * @param  authResultHandler  <code>AuthenticationResultHandler[]</code> to
   * post process authentication results
   * @param  authzHandler  <code>AuthorizationHandler[]</code> to process
   * authorization after authentication
   *
   * @return  <code>Attribute</code> - belonging to the supplied user, returns
   * null if searchAttrs is false
   *
   * @throws  NamingException  if any of the ldap operations fail
   * @throws  AuthenticationException  if authentication fails
   * @throws  AuthorizationException  if authorization fails
   */
  protected Attributes authenticateAndAuthorize(
    final String dn,
    final Object credential,
    final boolean searchAttrs,
    final String[] retAttrs,
    final AuthenticationResultHandler[] authResultHandler,
    final AuthorizationHandler[] authzHandler)
    throws NamingException
  {
    // check the authentication type
    final String authtype = this.config.getAuthtype();
    if (authtype.equalsIgnoreCase(LdapConstants.NONE_AUTHTYPE)) {
      throw new AuthenticationException(
        "Cannot authenticate dn, authtype is 'none'");
    }

    // check the credential
    if (!LdapUtil.checkCredential(credential)) {
      throw new AuthenticationException(
        "Cannot authenticate dn, invalid credential");
    }

    // check the dn
    if (dn == null || "".equals(dn)) {
      throw new AuthenticationException("Cannot authenticate dn, invalid dn");
    }

    Attributes userAttributes = null;

    // attempt to bind as this dn
    final ConnectionHandler ch =
      this.config.getConnectionHandler().newInstance();
    try {
      final AuthenticationCriteria ac = new AuthenticationCriteria(dn);
      ac.setCredential(credential);
      try {
        final AuthenticationHandler authHandler =
          this.config.getAuthenticationHandler().newInstance();
        authHandler.authenticate(ch, ac);
        if (this.logger.isInfoEnabled()) {
          this.logger.info("Authentication succeeded for dn: " + dn);
        }
      } catch (AuthenticationException e) {
        if (this.logger.isInfoEnabled()) {
          this.logger.info("Authentication failed for dn: " + dn);
        }
        if (authResultHandler != null && authResultHandler.length > 0) {
          for (AuthenticationResultHandler ah : authResultHandler) {
            ah.process(ac, false);
          }
        }
        throw e;
      }
      // authentication succeeded, perform authorization if supplied
      if (authzHandler != null && authzHandler.length > 0) {
        for (AuthorizationHandler azh : authzHandler) {
          try {
            azh.process(ac, ch.getLdapContext());
            if (this.logger.isInfoEnabled()) {
              this.logger.info(
                "Authorization succeeded for dn: " + dn +
                " with handler: " + azh);
            }
          } catch (AuthenticationException e) {
            if (this.logger.isInfoEnabled()) {
              this.logger.info(
                "Authorization failed for dn: " + dn +
                " with handler: " +azh);
            }
            if (authResultHandler != null && authResultHandler.length > 0) {
              for (AuthenticationResultHandler ah : authResultHandler) {
                ah.process(ac, false);
              }
            }
            throw e;
          }
        }
      }
      if (searchAttrs) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Returning attributes: ");
          this.logger.debug(
            "    " +
            (retAttrs == null ?
              "all attributes" : Arrays.toString(retAttrs)));
        }
        userAttributes = ch.getLdapContext().getAttributes(dn, retAttrs);
      }
      if (authResultHandler != null && authResultHandler.length > 0) {
        for (AuthenticationResultHandler ah : authResultHandler) {
          ah.process(ac, true);
        }
      }
    } finally {
      ch.close();
    }

    return userAttributes;
  }


  /** This will close the connection on the underlyng DN resolver. */
  public synchronized void close()
  {
    if (this.config.getDnResolver() != null) {
      this.config.getDnResolver().close();
    }
  }
}
