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
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.auth.AuthenticationException;
import edu.vt.middleware.ldap.auth.AuthenticationRequest;
import edu.vt.middleware.ldap.auth.Authenticator;

/**
 * Provides a JAAS authentication hook for LDAP authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapLoginModule extends AbstractLoginModule implements LoginModule
{

  /** User attribute to add to role data. */
  private String[] userRoleAttribute = new String[0];

  /** Authenticator to use against the LDAP. */
  private Authenticator auth;

  /** Authentication request to use for authentication. */
  private AuthenticationRequest authRequest;


  /** {@inheritDoc} */
  @Override
  public void initialize(
    final Subject subject,
    final CallbackHandler callbackHandler,
    final Map<String, ?> sharedState,
    final Map<String, ?> options)
  {
    setLdapPrincipal = true;
    setLdapCredential = true;

    super.initialize(subject, callbackHandler, sharedState, options);

    final Iterator<String> i = options.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      final String value = (String) options.get(key);
      if (key.equalsIgnoreCase("userRoleAttribute")) {
        if ("*".equals(value)) {
          userRoleAttribute = null;
        } else {
          userRoleAttribute = value.split(",");
        }
      }
    }

    logger.trace(
      "userRoleAttribute = {}", Arrays.toString(userRoleAttribute));

    auth = createAuthenticator(options);
    logger.debug("Created authenticator: {}", auth);

    authRequest = createAuthenticationRequest(options);
    authRequest.setReturnAttributes(userRoleAttribute);
    logger.debug("Created authentication request: {}", authRequest);
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
      authRequest.setUser(nameCb.getName());
      authRequest.setCredential(new Credential(passCb.getPassword()));

      LdapException authEx = null;
      LdapEntry entry = null;
      try {
        entry = auth.authenticate(authRequest).getResult();
        if (entry != null) {
          roles.addAll(
            attributesToRoles(entry.getLdapAttributes()));
          if (defaultRole != null && !defaultRole.isEmpty()) {
            roles.addAll(defaultRole);
          }
        }
        loginSuccess = true;
      } catch (AuthenticationException e) {
        if (tryFirstPass) {
          getCredentials(nameCb, passCb, true);
          try {
            entry = auth.authenticate(authRequest).getResult();
            if (entry != null) {
              roles.addAll(
                attributesToRoles(entry.getLdapAttributes()));
            }
            if (defaultRole != null && !defaultRole.isEmpty()) {
              roles.addAll(defaultRole);
            }
            loginSuccess = true;
          } catch (AuthenticationException e2) {
            authEx = e;
            loginSuccess = false;
          }
        } else {
          authEx = e;
          loginSuccess = false;
        }
      }
      if (!loginSuccess) {
        logger.debug("Authentication failed", authEx);
        throw new LoginException(
          authEx != null ? authEx.getMessage() : "Authentication failed");
      } else {
        if (setLdapPrincipal) {
          final LdapPrincipal lp = new LdapPrincipal(nameCb.getName());
          if (entry != null) {
            lp.getLdapAttributes().addAttributes(
              entry.getLdapAttributes().getAttributes());
          }
          principals.add(lp);
        }

        final String loginDn = auth.resolveDn(nameCb.getName());
        if (loginDn != null && setLdapDnPrincipal) {
          final LdapDnPrincipal lp = new LdapDnPrincipal(loginDn);
          if (entry != null) {
            lp.getLdapAttributes().addAttributes(
              entry.getLdapAttributes().getAttributes());
          }
          principals.add(lp);
        }
        if (setLdapCredential) {
          credentials.add(new LdapCredential(passCb.getPassword()));
        }
        storeCredentials(nameCb, passCb, loginDn);
      }
    } catch (LdapException e) {
      logger.debug("Error occured attempting authentication", e);
      loginSuccess = false;
      throw new LoginException(
        e != null ? e.getMessage() : "Authentication Error");
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
    String name = "vt-ldap";
    if (args.length > 0) {
      name = args[0];
    }

    final LoginContext lc = new LoginContext(name, new TextCallbackHandler());
    lc.login();
    System.out.println("Authentication/Authorization succeeded");

    final Set<Principal> principals = lc.getSubject().getPrincipals();
    System.out.println("Subject Principal(s): ");

    final Iterator<Principal> i = principals.iterator();
    while (i.hasNext()) {
      final Principal p = i.next();
      System.out.println("  " + p);
    }
    lc.logout();
  }
}
