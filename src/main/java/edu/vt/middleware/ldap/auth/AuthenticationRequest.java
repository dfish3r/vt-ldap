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
  protected String[] retAttrs = new String[0];

  /** Handlers to authorize the user. */
  protected AuthorizationHandler[] authzHandlers;


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
   * @param  ah  authorization handlers
   */
  public AuthenticationRequest(
    final String id,
    final Credential c,
    final AuthorizationHandler[] ah)
  {
    this.setUser(id);
    this.setCredential(c);
    this.setAuthorizationHandler(ah);
  }


  /**
   * Creates a new authentication request.
   *
   * @param  id  that identifies the user
   * @param  c  credential to authenticate the user
   * @param  attrs  attributes to return
   * @param  ah  authorization handlers
   */
  public AuthenticationRequest(
    final String id,
    final Credential c,
    final String[] attrs,
    final AuthorizationHandler[] ah)
  {
    this.setUser(id);
    this.setCredential(c);
    this.setReturnAttributes(attrs);
    this.setAuthorizationHandler(ah);
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
   * Returns the authorization handlers.
   *
   * @return  authorization handlers
   */
  public AuthorizationHandler[] getAuthorizationHandler()
  {
    return this.authzHandlers;
  }


  /**
   * Sets the authorization handlers.
   *
   * @param  ah  authorization handlers
   */
  public void setAuthorizationHandler(final AuthorizationHandler[] ah)
  {
    this.authzHandlers = ah;
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
        "%s@%d: user=%s, credential=%s, retAttrs=%s, authzHandler=%s",
        this.getClass().getName(),
        this.hashCode(),
        this.user,
        this.credential,
        this.retAttrs != null ? Arrays.asList(this.retAttrs) : null,
        this.authzHandlers != null ? Arrays.asList(this.authzHandlers) : null);
  }
}
