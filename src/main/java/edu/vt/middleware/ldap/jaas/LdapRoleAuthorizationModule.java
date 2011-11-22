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
package edu.vt.middleware.ldap.jaas;

import java.security.Principal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import com.sun.security.auth.callback.TextCallbackHandler;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Provides a JAAS authentication hook into LDAP roles. No authentication is
 * performed in this module. Role data is set for the login name in the shared
 * state or for the name returned by the CallbackHandler.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapRoleAuthorizationModule extends AbstractLoginModule
  implements LoginModule
{

  /** Ldap filter for role searches. */
  private String roleFilter;

  /** Role attribute to add to role data. */
  private String[] roleAttribute = new String[0];

  /** Whether failing to find any roles should raise an exception. */
  private boolean noResultsIsError;

  /** Factory for creating role resolvers with JAAS options. */
  private RoleResolverFactory roleResolverFactory;

  /** To search for roles. */
  private RoleResolver roleResolver;

  /** Search request to use for roles. */
  private SearchRequest searchRequest;


  /** {@inheritDoc} */
  @Override
  public void initialize(
    final Subject subject,
    final CallbackHandler callbackHandler,
    final Map<String, ?> sharedState,
    final Map<String, ?> options)
  {
    super.initialize(subject, callbackHandler, sharedState, options);

    final Iterator<String> i = options.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      final String value = (String) options.get(key);
      if ("roleFilter".equalsIgnoreCase(key)) {
        roleFilter = value;
      } else if ("roleAttribute".equalsIgnoreCase(key)) {
        if ("*".equals(value)) {
          roleAttribute = null;
        } else {
          roleAttribute = value.split(",");
        }
      } else if ("noResultsIsError".equalsIgnoreCase(key)) {
        noResultsIsError = Boolean.valueOf(value);
      } else if ("roleResolverFactory".equalsIgnoreCase(key)) {
        try {
          roleResolverFactory =
            (RoleResolverFactory) Class.forName(value).newInstance();
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException(e);
        } catch (InstantiationException e) {
          throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
          throw new IllegalArgumentException(e);
        }
      }
    }

    if (roleResolverFactory == null) {
      roleResolverFactory = new PropertiesRoleResolverFactory();
    }

    logger.trace(
      "roleResolverFactory = {}, roleFilter = {}, roleAttribute = {}, " +
      "noResultsIsError = {}",
      new Object[] {
        roleResolverFactory,
        roleFilter,
        Arrays.toString(roleAttribute),
        noResultsIsError, });

    roleResolver = roleResolverFactory.createRoleResolver(options);
    logger.debug("Retrieved role resolver from factory: {}", roleResolver);

    searchRequest = roleResolverFactory.createSearchRequest(options);
    searchRequest.setReturnAttributes(roleAttribute);
    logger.debug("Retrieved search request from factory: {}", searchRequest);
  }


  /** {@inheritDoc} */
  @Override
  public boolean login()
    throws LoginException
  {
    try {
      final NameCallback nameCb = new NameCallback("Enter user: ");
      final PasswordCallback passCb = new PasswordCallback(
        "Enter user password: ",
        false);
      getCredentials(nameCb, passCb, false);

      if (nameCb.getName() == null && tryFirstPass) {
        getCredentials(nameCb, passCb, true);
      }

      final String loginName = nameCb.getName();
      if (loginName != null && setLdapPrincipal) {
        principals.add(new LdapPrincipal(loginName, null));
        loginSuccess = true;
      }

      final String loginDn = (String) sharedState.get(LOGIN_DN);
      if (loginDn != null && setLdapDnPrincipal) {
        principals.add(new LdapDnPrincipal(loginDn, null));
        loginSuccess = true;
      }

      final Object[] filterArgs = new Object[] {loginDn, loginName, };
      searchRequest.setSearchFilter(new SearchFilter(roleFilter, filterArgs));
      final Set<LdapRole> lr = roleResolver.search(searchRequest);
      if (lr.size() == 0 && noResultsIsError) {
        loginSuccess = false;
        throw new LoginException("Could not find roles using " + roleFilter);
      }
      roles.addAll(lr);
      if (defaultRole != null && !defaultRole.isEmpty()) {
        roles.addAll(defaultRole);
      }
      if (!roles.isEmpty()) {
        loginSuccess = true;
      }
      storeCredentials(nameCb, passCb, null);
    } catch (LdapException e) {
      logger.debug("Error occured attempting role lookup", e);
      loginSuccess = false;
      throw new LoginException(e.getMessage());
    }
    return true;
  }


  /**
   * This provides command line access to this JAAS module.
   *
   * @param  args  command line arguments
   *
   * @throws  Exception  if an error occurs
   */
  public static void main(final String[] args)
    throws Exception
  {
    String name = "vt-ldap-role";
    if (args.length > 0) {
      name = args[0];
    }

    final LoginContext lc = new LoginContext(name, new TextCallbackHandler());
    lc.login();

    final Set<Principal> principals = lc.getSubject().getPrincipals();
    System.out.println("Subject Principal(s): ");

    final Iterator<Principal> i = principals.iterator();
    while (i.hasNext()) {
      final Principal p = i.next();
      System.out.println("  " + p.getName());
    }
    lc.logout();
  }
}
