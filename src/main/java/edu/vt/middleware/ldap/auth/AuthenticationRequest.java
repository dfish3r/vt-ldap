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
package edu.vt.middleware.ldap.auth;

import java.util.Arrays;

import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapRequest;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.auth.handler.AuthenticationResultHandler;
import edu.vt.middleware.ldap.auth.handler.AuthorizationHandler;

/**
 * Contains the data required to perform an ldap authentication.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class AuthenticationRequest implements LdapRequest
{
  /** User identifier. */
  protected String user;

  /** User credential. */
  protected Credential credential;

  /** User attributes to return. */
  protected String[] retAttrs;

  /** Filter to resolve user DN. */
  protected SearchFilter filter;

  /** Handlers to process authentication results. */
  protected AuthenticationResultHandler[] authHandler;

  /** Handlers to authorize the user. */
  protected AuthorizationHandler[] authzHandler;


  /** Default constructor. */
  public AuthenticationRequest() {}


  /**
   * Creates a new authentication request.
   *
   * @param  id  that identifies the user
   * @param  c  credential to authenticate the user
   */
  public AuthenticationRequest(final String id, final Credential c)
  {
    this.setUser(id);
    this.setCredential(c);
  }


  /**
   * Creates a new authentication request.
   *
   * @param  id  that identifies the user
   * @param  c  credential to authenticate the user
   * @param  attrs  attributes to return
   */
  public AuthenticationRequest(
    final String id, final Credential c, final String[] attrs)
  {
    this.setUser(id);
    this.setCredential(c);
    this.setReturnAttributes(attrs);
  }


  /**
   * Creates a new authentication request.
   *
   * @param  id  that identifies the user
   * @param  c  credential to authenticate the user
   * @param  sf  search filter to resolve the user DN
   */
  public AuthenticationRequest(
    final String id, final Credential c, final SearchFilter sf)
  {
    this.setUser(id);
    this.setCredential(c);
    this.setAuthorizationFilter(sf);
  }


  /**
   * Creates a new authentication request.
   *
   * @param  id  that identifies the user
   * @param  c  credential to authenticate the user
   * @param  attrs  attributes to return
   * @param  sf  search filter to resolve the user DN
   */
  public AuthenticationRequest(
    final String id,
    final Credential c,
    final String[] attrs,
    final SearchFilter sf)
  {
    this.setUser(id);
    this.setCredential(c);
    this.setReturnAttributes(attrs);
    this.setAuthorizationFilter(sf);
  }


  /**
   * Returns the user.
   *
   * @return  user identifier
   */
  public String getUser()
  {
    return this.user;
  }


  /**
   * Sets the user.
   * @param  id  of the user
   */
  public void setUser(final String id)
  {
    this.user = id;
  }


  /**
   * Returns the credential.
   *
   * @return  user credential
   */
  public Credential getCredential()
  {
    return this.credential;
  }


  /**
   * Sets the credential.
   *
   * @param  c  user credential
   */
  public void setCredential(final Credential c)
  {
    this.credential = c;
  }


  /**
   * Returns the return attributes.
   *
   * @return  attributes to return
   */
  public String[] getReturnAttributes()
  {
    return this.retAttrs;
  }


  /**
   * Sets the return attributes.
   *
   * @param  attrs  return attributes
   */
  public void setReturnAttributes(final String[] attrs)
  {
    this.retAttrs = attrs;
  }


  /**
   * Returns the authorization filter.
   *
   * @return  authorization filter
   */
  public SearchFilter getAuthorizationFilter()
  {
    return this.filter;
  }


  /**
   * Sets the authorization filter.
   *
   * @param  sf  authorization filter
   */
  public void setAuthorizationFilter(final SearchFilter sf)
  {
    this.filter = sf;
  }


  /**
   * Returns the authentication result handlers.
   *
   * @return  authentication result handlers
   */
  public AuthenticationResultHandler[] getAuthenticationResultHandler()
  {
    return this.authHandler;
  }


  /**
   * Sets the authentication result handlers.
   *
   * @param  arh  authentication result handlers
   */
  public void setAuthenticationResultHandler(
    final AuthenticationResultHandler[] arh)
  {
    this.authHandler = arh;
  }


  /**
   * Returns the authorization handlers.
   *
   * @return  authorization handlers
   */
  public AuthorizationHandler[] getAuthorizationHandler()
  {
    return this.authzHandler;
  }


  /**
   * Sets the authorization handlers.
   *
   * @param  ah  authorization handlers
   */
  public void setAuthorizationHandler(final AuthorizationHandler[] ah)
  {
    this.authzHandler = ah;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "%s@%d: user=%s, credential=%s, retAttrs=%s, filter=%s, " +
        "authHandler=%s, authzHandler=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.user,
        this.credential,
        this.retAttrs != null ? Arrays.asList(this.retAttrs) : null,
        this.filter,
        this.authHandler != null ? Arrays.asList(this.authHandler) : null,
        this.authzHandler != null ? Arrays.asList(this.authzHandler) : null);
  }
}
