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
package edu.vt.middleware.ldap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import edu.vt.middleware.ldap.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.handler.AuthorizationHandler;
import edu.vt.middleware.ldap.handler.CompareAuthorizationHandler;

/**
 * <code>DnAuthenticator</code> contains functions for authenticating a DN
 * against an LDAP.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DnAuthenticator extends AbstractAuthenticator
  implements Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -1682242101872623787L;


  /** Default constructor. */
  public DnAuthenticator() {}


  /**
   * This will create a new <code>DnAuthenticator</code> with the supplied
   * <code>AuthenticatorConfig</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public DnAuthenticator(final AuthenticatorConfig authConfig)
  {
    this.setAuthenticatorConfig(authConfig);
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied dn and
   * credential. If {@link AuthenticatorConfig#setAuthorizationFilter} has been
   * called, then it will be used to authorize the user by performing an ldap
   * compare. See {@link #authenticate(String, Object, SearchFilter)}.
   *
   * @param  dn  <code>String</code> dn for bind
   * @param  credential  <code>Object</code> credential for bind
   *
   * @return  <code>boolean</code> - whether the bind succeeded
   *
   * @throws  NamingException  if the authentication fails for any other reason
   * than invalid credentials
   */
  public boolean authenticate(final String dn, final Object credential)
    throws NamingException
  {
    return
      this.authenticate(
        dn,
        credential,
        new SearchFilter(
          this.config.getAuthorizationFilter(),
          this.config.getAuthorizationFilterArgs()));
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied dn and
   * credential. If the supplied filter is not null it will be injected into a
   * new instance of CompareAuthorizationHandler and set as the first
   * AuthorizationHandler to execute. If {@link
   * AuthenticatorConfig#setAuthenticationResultHandlers(
   * AuthenticationResultHandler[])} has been called, then it will be used to
   * post process authentication results. See {@link #authenticate(String,
   * Object, AuthenticationResultHandler[], AuthorizationHandler[])}
   *
   * @param  dn  <code>String</code> dn for bind
   * @param  credential  <code>Object</code> credential for bind
   * @param  filter  <code>SearchFilter</code> to authorize user
   *
   * @return  <code>boolean</code> - whether the bind succeeded
   *
   * @throws  NamingException  if the authentication fails for any other reason
   * than invalid credentials
   */
  public boolean authenticate(
    final String dn,
    final Object credential,
    final SearchFilter filter)
    throws NamingException
  {
    final List<AuthorizationHandler> authzHandler =
      new ArrayList<AuthorizationHandler>();
    if (filter != null && filter.getFilter() != null) {
      authzHandler.add(new CompareAuthorizationHandler(filter));
    }
    if (this.config.getAuthorizationHandlers() != null) {
      authzHandler.addAll(
        Arrays.asList(this.config.getAuthorizationHandlers()));
    }
    return
      this.authenticate(
        dn,
        credential,
        this.config.getAuthenticationResultHandlers(),
        authzHandler.toArray(new AuthorizationHandler[0]));
  }


  /**
   * This will authenticate credentials by binding to the LDAP with the supplied
   * dn and credential. See {@link #authenticateAndAuthorize(String, Object,
   * AuthenticationResultHandler[], AuthorizationHandler[])}
   *
   * @param  dn  <code>String</code> dn for bind
   * @param  credential  <code>Object</code> credential for bind
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
  public boolean authenticate(
    final String dn,
    final Object credential,
    final AuthenticationResultHandler[] authHandler,
    final AuthorizationHandler[] authzHandler)
    throws NamingException
  {
    return
      super.authenticateAndAuthorize(dn, credential, authHandler, authzHandler);
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied dn and
   * credential. If {@link AuthenticatorConfig#setAuthorizationFilter} has been
   * called, then it will be used to authorize the user by performing an ldap
   * compare. See {@link #authenticate(String, Object, SearchFilter, String[])}
   *
   * @param  dn  <code>String</code> dn for bind
   * @param  credential  <code>Object</code> credential for bind
   * @param  retAttrs  <code>String[]</code> to return
   *
   * @return  <code>Attributes</code> - of authenticated user
   *
   * @throws  NamingException  if any of the ldap operations fail
   */
  public Attributes authenticate(
    final String dn,
    final Object credential,
    final String[] retAttrs)
    throws NamingException
  {
    return
      this.authenticate(
        dn,
        credential,
        new SearchFilter(
          this.config.getAuthorizationFilter(),
          this.config.getAuthorizationFilterArgs()),
        retAttrs);
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied dn and
   * credential. If the supplied filter is not null it will be injected into a
   * new instance of CompareAuthorizationHandler and set as the first
   * AuthorizationHandler to execute. See {@link #authenticate(String, Object,
   * String[], AuthenticationResultHandler[], AuthorizationHandler[])}
   *
   * @param  dn  <code>String</code> dn for bind
   * @param  credential  <code>Object</code> credential for bind
   * @param  filter  <code>SearchFilter</code> to authorize user
   * @param  retAttrs  <code>String[]</code> to return
   *
   * @return  <code>Attributes</code> - of authenticated user
   *
   * @throws  NamingException  if any of the ldap operations fail
   */
  public Attributes authenticate(
    final String dn,
    final Object credential,
    final SearchFilter filter,
    final String[] retAttrs)
    throws NamingException
  {
    final List<AuthorizationHandler> authzHandler =
      new ArrayList<AuthorizationHandler>();
    if (filter != null && filter.getFilter() != null) {
      authzHandler.add(new CompareAuthorizationHandler(filter));
    }
    if (this.config.getAuthorizationHandlers() != null) {
      authzHandler.addAll(
        Arrays.asList(this.config.getAuthorizationHandlers()));
    }
    return
      this.authenticate(
        dn,
        credential,
        retAttrs,
        this.config.getAuthenticationResultHandlers(),
        authzHandler.toArray(new AuthorizationHandler[0]));
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied dn and
   * credential. See {@link #authenticateAndAuthorize(String, Object, boolean,
   * String[], AuthenticationResultHandler[], AuthorizationHandler[])}.
   *
   * @param  dn  <code>String</code> dn for bind
   * @param  credential  <code>Object</code> credential for bind
   * @param  retAttrs  <code>String[]</code> to return
   * @param  authHandler  <code>AuthenticationResultHandler[]</code> to post
   * process authentication results
   * @param  authzHandler  <code>AuthorizationHandler[]</code> to process
   * authorization after authentication
   *
   * @return  <code>Attributes</code> - of authenticated user
   *
   * @throws  NamingException  if any of the ldap operations fail
   */
  public Attributes authenticate(
    final String dn,
    final Object credential,
    final String[] retAttrs,
    final AuthenticationResultHandler[] authHandler,
    final AuthorizationHandler[] authzHandler)
    throws NamingException
  {
    return
      super.authenticateAndAuthorize(
        dn,
        credential,
        true,
        retAttrs,
        authHandler,
        authzHandler);
  }
}
