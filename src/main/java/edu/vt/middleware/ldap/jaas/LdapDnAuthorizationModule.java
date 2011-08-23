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
import edu.vt.middleware.ldap.auth.Authenticator;

/**
 * Provides a JAAS authentication hook into LDAP DNs. No authentication is
 * performed by this module. The LDAP entry DN can be stored and shared with
 * other JAAS modules.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapDnAuthorizationModule extends AbstractLoginModule
  implements LoginModule
{

  /** Whether failing to find a DN should raise an exception. */
  private boolean noResultsIsError;

  /** Factory for creating authenticators with JAAS options. */
  private AuthenticatorFactory authenticatorFactory;

  /** Authenticator to use against the LDAP. */
  private Authenticator auth;


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
      if (key.equalsIgnoreCase("noResultsIsError")) {
        noResultsIsError = Boolean.valueOf(value);
      } else if (key.equalsIgnoreCase("authenticatorFactory")) {
        try {
          authenticatorFactory =
            (AuthenticatorFactory) Class.forName(value).newInstance();
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException(e);
        } catch (InstantiationException e) {
          throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
          throw new IllegalArgumentException(e);
        }
      }
    }

    if (authenticatorFactory == null) {
      authenticatorFactory = new PropertiesAuthenticatorFactory();
    }

    logger.trace(
      "authenticatorFactory = {}, noResultsIsError = {}",
      authenticatorFactory,
      noResultsIsError);

    auth = authenticatorFactory.createAuthenticator(options);
    logger.debug("Retrieved authenticator from factory: {}", auth);
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

      final String loginDn = auth.resolveDn(nameCb.getName());
      if (loginDn == null && noResultsIsError) {
        loginSuccess = false;
        throw new LoginException("Could not find DN for " + nameCb.getName());
      }
      if (loginDn != null && setLdapDnPrincipal) {
        principals.add(new LdapDnPrincipal(loginDn, null));
        loginSuccess = true;
      }
      if (defaultRole != null && !defaultRole.isEmpty()) {
        roles.addAll(defaultRole);
        loginSuccess = true;
      }
      storeCredentials(nameCb, passCb, loginDn);
    } catch (LdapException e) {
      logger.debug("Error occured attempting DN lookup", e);
      loginSuccess = false;
      throw new LoginException(
        e != null ? e.getMessage() : "DN resolution error");
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
    String name = "vt-ldap-dn";
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
