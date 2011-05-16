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
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
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

  /** Ldap connection to use for searching roles against the LDAP. */
  private LdapConnection ldapConn;

  /** Search request to use for searching roles. */
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
      if (key.equalsIgnoreCase("roleFilter")) {
        roleFilter = value;
      } else if (key.equalsIgnoreCase("roleAttribute")) {
        if ("*".equals(value)) {
          roleAttribute = null;
        } else {
          roleAttribute = value.split(",");
        }
      } else if (key.equalsIgnoreCase("noResultsIsError")) {
        noResultsIsError = Boolean.valueOf(value);
      }
    }

    logger.trace(
      "roleFilter = {}, roleAttribute = {}, noResultsIsError = {}",
      new Object[] {
        roleFilter,
        Arrays.toString(roleAttribute),
        noResultsIsError, });

    ldapConn = createLdapConnection(options);
    logger.debug("Created ldap connection: {}", ldapConn);

    searchRequest = createSearchRequest(options);
    searchRequest.setReturnAttributes(roleAttribute);
    logger.debug("Created search request: {}", searchRequest);
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

      if (roleFilter != null) {
        final Object[] filterArgs = new Object[] {loginDn, loginName, };
        ldapConn.open();
        final SearchOperation search = new SearchOperation(ldapConn);
        searchRequest.setSearchFilter(
          new SearchFilter(roleFilter, filterArgs));
        final LdapResult result = search.execute(
          searchRequest).getResult();
        if (result.size() == 0 && noResultsIsError) {
          loginSuccess = false;
          throw new LoginException(
            "Could not find roles using " + roleFilter);
        }
        for (LdapEntry le : result.getEntries()) {
          roles.addAll(entryToRoles(le));
        }
      }
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
    } finally {
      ldapConn.close();
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
