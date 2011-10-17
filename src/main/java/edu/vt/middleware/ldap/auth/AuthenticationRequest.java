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

/**
 * Contains the data required to perform an ldap authentication.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class AuthenticationRequest
{

  /** User identifier. */
  private String user;

  /** User credential. */
  private Credential credential;

  /** User attributes to return. */
  private String[] retAttrs = new String[0];

  /** Filter for authorizing user. */
  private String authzFilter;

  /** Filter arguments for authorizing user. */
  private Object[] authzFilterArgs;

  /** Handlers to authorize the user. */
  private AuthorizationHandler[] authzHandlers;


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
    setUser(id);
    setCredential(c);
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
    setUser(id);
    setCredential(c);
    setReturnAttributes(attrs);
  }


  /**
   * Creates a new authentication request.
   *
   * @param  id  that identifies the user
   * @param  c  credential to authenticate the user
   * @param  handlers  authorization handlers
   */
  public AuthenticationRequest(
    final String id,
    final Credential c,
    final AuthorizationHandler[] handlers)
  {
    setUser(id);
    setCredential(c);
    setAuthorizationHandlers(handlers);
  }


  /**
   * Creates a new authentication request.
   *
   * @param  id  that identifies the user
   * @param  c  credential to authenticate the user
   * @param  attrs  attributes to return
   * @param  handlers  authorization handlers
   */
  public AuthenticationRequest(
    final String id,
    final Credential c,
    final String[] attrs,
    final AuthorizationHandler[] handlers)
  {
    setUser(id);
    setCredential(c);
    setReturnAttributes(attrs);
    setAuthorizationHandlers(handlers);
  }


  /**
   * Returns the user.
   *
   * @return  user identifier
   */
  public String getUser()
  {
    return user;
  }


  /**
   * Sets the user.
   * @param  id  of the user
   */
  public void setUser(final String id)
  {
    user = id;
  }


  /**
   * Returns the credential.
   *
   * @return  user credential
   */
  public Credential getCredential()
  {
    return credential;
  }


  /**
   * Sets the credential.
   *
   * @param  c  user credential
   */
  public void setCredential(final Credential c)
  {
    credential = c;
  }


  /**
   * Returns the return attributes.
   *
   * @return  attributes to return
   */
  public String[] getReturnAttributes()
  {
    return retAttrs;
  }


  /**
   * Sets the return attributes.
   *
   * @param  attrs  return attributes
   */
  public void setReturnAttributes(final String[] attrs)
  {
    retAttrs = attrs;
  }


  /**
   * Returns the filter used to authorize the user.
   *
   * @return  filter
   */
  public String getAuthorizationFilter()
  {
    return authzFilter;
  }


  /**
   * Sets the filter used to authorize the user. If not set, no authorization
   * is performed.
   *
   * @param  filter  for authorization
   */
  public void setAuthorizationFilter(final String filter)
  {
    authzFilter = filter;
  }


  /**
   * Returns the filter arguments used to authorize the user.
   *
   * @return  filter arguments
   */
  public Object[] getAuthorizationFilterArgs()
  {
    return authzFilterArgs;
  }


  /**
   * Sets the filter arguments used to authorize the user.
   *
   * @param  filterArgs  filter arguments
   */
  public void setAuthorizationFilterArgs(final Object[] filterArgs)
  {
    authzFilterArgs = filterArgs;
  }


  /**
   * Returns the authorization handlers.
   *
   * @return  authorization handlers
   */
  public AuthorizationHandler[] getAuthorizationHandlers()
  {
    return authzHandlers;
  }


  /**
   * Sets the authorization handlers.
   *
   * @param  handlers  authorization handlers
   */
  public void setAuthorizationHandlers(final AuthorizationHandler[] handlers)
  {
    authzHandlers = handlers;
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
        "[%s@%d::user=%s, retAttrs=%s, authzFilter=%s, " +
        "authzFilterArgs=%s, authzHandlers=%s]",
        getClass().getName(),
        hashCode(),
        user,
        retAttrs != null ? Arrays.asList(retAttrs) : null,
        authzFilter,
        authzFilterArgs != null ?
          Arrays.asList(authzFilterArgs) : null,
        authzHandlers != null ? Arrays.asList(authzHandlers) : null);
  }
}
