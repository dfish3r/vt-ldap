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
import java.util.Iterator;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
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
   * This will attempt to find the dn for the supplied user. If {@link
   * AuthenticatorConfig#setConstructDn(boolean)} has been set to true, then the
   * dn will be created by combining the userField and the base dn. Otherwise,
   * {@link AuthenticatorConfig#getUserFilter()} or {@link
   * AuthenticatorConfig#getUserField()} is used to look up the dn. If a filter
   * is used, the user is provided as the {0} variable filter argument. If a
   * field is used, the filter is built by ORing the fields together. If more
   * than one entry matches the search, the result is controlled by {@link
   * AuthenticatorConfig#setAllowMultipleDns(boolean)}.
   *
   * @param  user  <code>String</code> to find dn for
   *
   * @return  <code>String</code> - user's dn
   *
   * @throws  NamingException  if the LDAP search fails
   */
  public String getDn(final String user)
    throws NamingException
  {
    String dn = null;
    if (user != null && !user.equals("")) {
      if (this.config.getConstructDn()) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Constructing DN from first userfield and base");
        }
        dn = this.config.getUserField()[0] + "=" + user + "," +
          this.config.getBase();
      } else {
        // create the search filter
        final SearchFilter filter = new SearchFilter();
        if (this.config.getUserFilter() != null) {
          if (this.logger.isDebugEnabled()) {
            this.logger.debug("Looking up DN using userFilter");
          }
          filter.setFilter(this.config.getUserFilter());
          filter.setFilterArgs(this.config.getUserFilterArgs());
        } else {
          if (this.logger.isDebugEnabled()) {
            this.logger.debug("Looking up DN using userField");
          }
          if (
            this.config.getUserField() == null ||
              this.config.getUserField().length == 0) {
            if (this.logger.isErrorEnabled()) {
              this.logger.error("Invalid userField, cannot be null or empty.");
            }
          } else {
            final StringBuffer searchFilter = new StringBuffer();
            if (this.config.getUserField().length > 1) {
              searchFilter.append("(|");
              for (int i = 0; i < this.config.getUserField().length; i++) {
                searchFilter.append("(").append(this.config.getUserField()[i])
                  .append("=").append(user).append(")");
              }
              searchFilter.append(")");
            } else {
              searchFilter.append("(").append(this.config.getUserField()[0])
                .append("=").append(user).append(")");
            }
            filter.setFilter(searchFilter.toString());
          }
        }

        if (filter.getFilter() != null) {
          // make user the first filter arg
          final List<Object> filterArgs = new ArrayList<Object>();
          filterArgs.add(user);
          filterArgs.addAll(filter.getFilterArgs());

          final Iterator<SearchResult> answer = this.search(
            this.config.getBase(),
            filter.getFilter(),
            filterArgs.toArray(),
            this.config.getSearchControls(new String[0]),
            this.config.getSearchResultHandlers());
          // return first match, otherwise user doesn't exist
          if (answer != null && answer.hasNext()) {
            final SearchResult sr = answer.next();
            dn = sr.getName();
            if (answer.hasNext()) {
              if (this.logger.isDebugEnabled()) {
                this.logger.debug(
                  "Multiple results found for user: " + user +
                  " using filter: " + filter);
              }
              if (!this.config.getAllowMultipleDns()) {
                throw new NamingException(
                  "Found more than (1) DN for: " + user);
              }
            }
          } else {
            if (this.logger.isInfoEnabled()) {
              this.logger.info(
                "Search for user: " + user + " failed using filter: " +
                filter.getFilter());
            }
          }
        } else {
          if (this.logger.isErrorEnabled()) {
            this.logger.error(
              "DN search filter not found, no search performed");
          }
        }
      }
    } else {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("User input was empty or null");
      }
    }
    return dn;
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
   * calling {@link #getDn(String)}. See {@link
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
   * calling {@link #getDn(String)}. See {@link
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
