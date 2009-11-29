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
 * <code>Authenticator</code> contains functions for authenticating a user
 * against an LDAP.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Authenticator extends AbstractAuthenticator implements Serializable
{

  /** serial version uid. */
  private static final long serialVersionUID = -444519681288987247L;


  /** Default constructor. */
  public Authenticator() {}


  /**
   * This will create a new <code>Authenticator</code> with the supplied <code>
   * AuthenticatorConfig</code>.
   *
   * @param  authConfig  <code>AuthenticatorConfig</code>
   */
  public Authenticator(final AuthenticatorConfig authConfig)
  {
    this.setAuthenticatorConfig(authConfig);
  }


  /**
   * This will attempt to find the LDAP DN for the supplied user.
   * {@link AuthenticatorConfig#dnResolver} is invoked to perform this
   * operation.
   *
   * @param  user  <code>String</code> to find dn for
   *
   * @return  <code>String</code> - user's dn
   *
   * @throws  NamingException  an LDAP error occurs
   */
  public String getDn(final String user)
    throws NamingException
  {
    return this.config.getDnResolver().resolve(user);
  }


  /**
   * This will authenticate by binding to the LDAP using parameters given by
   * {@link AuthenticatorConfig#setUser} and {@link
   * AuthenticatorConfig#setCredential}. See {@link #authenticate(String,
   * Object)}.
   *
   * @return  <code>boolean</code> - whether the bind succeeded
   *
   * @throws  NamingException  if the authentication fails for any other reason
   * than invalid credentials
   */
  public boolean authenticate()
    throws NamingException
  {
    return
      this.authenticate(this.config.getUser(), this.config.getCredential());
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied user and
   * credential. If {@link AuthenticatorConfig#setAuthorizationFilter} has been
   * called, then it will be used to authorize the user by performing an ldap
   * compare. See {@link #authenticate(String, Object, SearchFilter)}.
   *
   * @param  user  <code>String</code> username for bind
   * @param  credential  <code>Object</code> credential for bind
   *
   * @return  <code>boolean</code> - whether the bind succeeded
   *
   * @throws  NamingException  if the authentication fails for any other reason
   * than invalid credentials
   */
  public boolean authenticate(final String user, final Object credential)
    throws NamingException
  {
    return
      this.authenticate(
        user,
        credential,
        new SearchFilter(
          this.config.getAuthorizationFilter(),
          this.config.getAuthorizationFilterArgs()));
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied user and
   * credential. If the supplied filter is not null it will be injected into a
   * new instance of CompareAuthorizationHandler and set as the first
   * AuthorizationHandler to execute. If {@link
   * AuthenticatorConfig#setAuthenticationResultHandlers(
   * AuthenticationResultHandler[])} has been called, then it will be used to
   * post process authentication results. See {@link #authenticate(String,
   * Object, AuthenticationResultHandler[], AuthorizationHandler[])}.
   *
   * @param  user  <code>String</code> username for bind
   * @param  credential  <code>Object</code> credential for bind
   * @param  filter  <code>SearchFilter</code> to authorize user
   *
   * @return  <code>boolean</code> - whether the bind succeeded
   *
   * @throws  NamingException  if the authentication fails for any other reason
   * than invalid credentials
   */
  public boolean authenticate(
    final String user,
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
        user,
        credential,
        this.config.getAuthenticationResultHandlers(),
        authzHandler.toArray(new AuthorizationHandler[0]));
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied user and
   * credential. The user's DN will be looked up before performing the bind by
   * calling {@link DnResolver#resolve(String)}. See {@link
   * #authenticateAndAuthorize(String, Object, AuthenticationResultHandler[],
   * AuthorizationHandler[])}.
   *
   * @param  user  <code>String</code> username for bind
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
    final String user,
    final Object credential,
    final AuthenticationResultHandler[] authHandler,
    final AuthorizationHandler[] authzHandler)
    throws NamingException
  {
    return
      super.authenticateAndAuthorize(
        this.getDn(user),
        credential,
        authHandler,
        authzHandler);
  }


  /**
   * This will authenticate by binding to the LDAP using parameters given by
   * {@link AuthenticatorConfig#setUser} and {@link
   * AuthenticatorConfig#setCredential}. See {@link
   * #authenticate(String,Object,String[])}
   *
   * @param  retAttrs  <code>String[]</code> attributes to return
   *
   * @return  <code>Attributes</code> - of authenticated user
   *
   * @throws  NamingException  if any of the ldap operations fail
   */
  public Attributes authenticate(final String[] retAttrs)
    throws NamingException
  {
    return
      this.authenticate(
        this.config.getUser(),
        this.config.getCredential(),
        retAttrs);
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied user and
   * credential. If {@link AuthenticatorConfig#setAuthorizationFilter} has been
   * called, then it will be used to authorize the user by performing an ldap
   * compare. See {@link #authenticate(String, Object, SearchFilter, String[])}
   *
   * @param  user  <code>String</code> username for bind
   * @param  credential  <code>Object</code> credential for bind
   * @param  retAttrs  <code>String[]</code> to return
   *
   * @return  <code>Attributes</code> - of authenticated user
   *
   * @throws  NamingException  if any of the ldap operations fail
   */
  public Attributes authenticate(
    final String user,
    final Object credential,
    final String[] retAttrs)
    throws NamingException
  {
    return
      this.authenticate(
        user,
        credential,
        new SearchFilter(
          this.config.getAuthorizationFilter(),
          this.config.getAuthorizationFilterArgs()),
        retAttrs);
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied user and
   * credential. If the supplied filter is not null it will be injected into a
   * new instance of CompareAuthorizationHandler and set as the first
   * AuthorizationHandler to execute. See {@link #authenticate(String, Object,
   * String[], AuthenticationResultHandler[], AuthorizationHandler[])}.
   *
   * @param  user  <code>String</code> username for bind
   * @param  credential  <code>Object</code> credential for bind
   * @param  filter  <code>SearchFilter</code> to authorize user
   * @param  retAttrs  <code>String[]</code> to return
   *
   * @return  <code>Attributes</code> - of authenticated user
   *
   * @throws  NamingException  if any of the ldap operations fail
   */
  public Attributes authenticate(
    final String user,
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
        user,
        credential,
        retAttrs,
        this.config.getAuthenticationResultHandlers(),
        authzHandler.toArray(new AuthorizationHandler[0]));
  }


  /**
   * This will authenticate by binding to the LDAP with the supplied user and
   * credential. The user's DN will be looked up before performing the bind by
   * calling {@link DnResolver#resolve(String)}. See {@link
   * #authenticateAndAuthorize(String, Object, boolean, String[],
   * AuthenticationResultHandler[], AuthorizationHandler[])}.
   *
   * @param  user  <code>String</code> username for bind
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
    final String user,
    final Object credential,
    final String[] retAttrs,
    final AuthenticationResultHandler[] authHandler,
    final AuthorizationHandler[] authzHandler)
    throws NamingException
  {
    return
      this.authenticateAndAuthorize(
        this.getDn(user),
        credential,
        true,
        retAttrs,
        authHandler,
        authzHandler);
  }
}
