/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import java.io.InputStream;
import java.util.Arrays;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsResponse;
import edu.vt.middleware.ldap.handler.AuthenticationCriteria;
import edu.vt.middleware.ldap.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.handler.AuthorizationHandler;

/**
 * <code>AbstractAuthenticator</code> provides basic functionality for
 * authenticating against an LDAP.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractAuthenticator
  extends AbstractLdap<AuthenticatorConfig>
{


  /**
   * This will set the config parameters of this <code>Authenticator</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public void setAuthenticatorConfig(final AuthenticatorConfig authConfig)
  {
    super.setLdapConfig(authConfig);
  }


  /**
   * This returns the <code>AuthenticatorConfig</code> of the <code>
   * Authenticator</code>.
   *
   * @return  <code>AuthenticatorConfig</code>
   */
  public AuthenticatorConfig getAuthenticatorConfig()
  {
    return this.config;
  }


  /**
   * This will set the config parameters of this <code>Authenticator</code>
   * using the default properties file, which must be located in your classpath.
   */
  public void loadFromProperties()
  {
    this.setAuthenticatorConfig(AuthenticatorConfig.createFromProperties(null));
  }


  /**
   * This will set the config parameters of this <code>Authenticator</code>
   * using the supplied input stream.
   *
   * @param  is  <code>InputStream</code>
   */
  public void loadFromProperties(final InputStream is)
  {
    this.setAuthenticatorConfig(AuthenticatorConfig.createFromProperties(is));
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied
   * dn and credential. See {@link #authenticateAndAuthorize(
   * String, Object, boolean, String[], AuthenticationResultHandler[],
   * AuthorizationHandler[])}.
   *
   * @param  dn  <code>String</code> for bind
   * @param  credential  <code>Object</code> for bind
   * @param  authHandler  <code>AuthenticationResultHandler[]</code> to post
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
    final AuthenticationResultHandler[] authHandler,
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
        authHandler,
        authzHandler);
      success = true;
    } catch (AuthenticationException e) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Authentication failed for dn: " + dn, e);
      }
    }
    return success;
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied
   * dn and credential. Authentication will never succeed if {@link
   * LdapConfig#getAuthtype()} is set to 'none'. If retAttrs is null and
   * searchAttrs is true then all user attributes will be returned. If retAttrs
   * is an empty array and searchAttrs is true then no attributes will be
   * returned. This method throws AuthenticationException if
   * authentication or authorization fails.
   *
   * @param  dn  <code>String</code> for bind
   * @param  credential  <code>Object</code> for bind
   * @param  searchAttrs  <code>boolean</code> whether to perform attribute
   * search
   * @param  retAttrs  <code>String[]</code> user attributes to return
   * @param  authHandler  <code>AuthenticationResultHandler[]</code> to post
   * process authentication results
   * @param  authzHandler  <code>AuthorizationHandler[]</code> to process
   * authorization after authentication
   *
   * @return  <code>Attribute</code> - belonging to the supplied user, returns
   * null if searchAttrs is false
   *
   * @throws  NamingException  if any of the ldap operations fail
   * @throws  AuthenticationException  if authentication or authorization fails
   */
  protected Attributes authenticateAndAuthorize(
    final String dn,
    final Object credential,
    final boolean searchAttrs,
    final String[] retAttrs,
    final AuthenticationResultHandler[] authHandler,
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
    if (dn == null || dn.equals("")) {
      throw new AuthenticationException("Cannot authenticate dn, invalid dn");
    }

    Attributes userAttributes = null;

    // attempt to bind as this dn
    final StartTlsResponse tls = null;
    LdapContext ctx = null;
    for (int i = 0; i <= LdapConstants.OPERATION_RETRY; i++) {
      try {
        final AuthenticationCriteria ac = new AuthenticationCriteria(dn);
        ac.setCredential(credential);
        try {
          ctx = this.bind(dn, credential, tls);
          if (this.logger.isInfoEnabled()) {
            this.logger.info("Authentication succeeded for dn: " + dn);
          }
        } catch (AuthenticationException e) {
          if (this.logger.isInfoEnabled()) {
            this.logger.info("Authentication failed for dn: " + dn);
          }
          if (authHandler != null && authHandler.length > 0) {
            for (AuthenticationResultHandler ah : authHandler) {
              ah.process(ac, false);
            }
          }
          throw e;
        }
        // authentication succeeded, perform authorization if supplied
        if (authzHandler != null && authzHandler.length > 0) {
          for (AuthorizationHandler azh : authzHandler) {
            try {
              azh.process(ac, ctx);
              if (this.logger.isInfoEnabled()) {
                this.logger.info(
                  "Authorization succeeded for dn: " + dn + " with handler: " +
                  azh);
              }
            } catch (AuthenticationException e) {
              if (this.logger.isInfoEnabled()) {
                this.logger.info(
                  "Authorization failed for dn: " + dn + " with handler: " +
                  azh);
              }
              if (authHandler != null && authHandler.length > 0) {
                for (AuthenticationResultHandler ah : authHandler) {
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
              (retAttrs == null ? "all attributes" : Arrays.asList(retAttrs)));
          }
          userAttributes = ctx.getAttributes(dn, retAttrs);
        }
        if (authHandler != null && authHandler.length > 0) {
          for (AuthenticationResultHandler ah : authHandler) {
            ah.process(ac, true);
          }
        }
        break;
      } catch (CommunicationException e) {
        if (i == LdapConstants.OPERATION_RETRY) {
          throw e;
        }
        if (this.logger.isWarnEnabled()) {
          this.logger.warn(
            "Error while communicating with the LDAP, retrying",
            e);
        }
      } finally {
        this.stopTls(tls);
        if (ctx != null) {
          ctx.close();
        }
      }
    }

    return userAttributes;
  }
}
