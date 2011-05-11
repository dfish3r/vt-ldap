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

  /** Authenticator to use against the LDAP. */
  private Authenticator auth;


  /** {@inheritDoc} */
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
        this.noResultsIsError = Boolean.valueOf(value);
      }
    }

    this.logger.trace("noResultsIsError = {}", this.noResultsIsError);

    this.auth = createAuthenticator(options);
    this.logger.debug("Created authenticator: {}", this.auth);
  }


  /** {@inheritDoc} */
  public boolean login()
    throws LoginException
  {
    try {
      final NameCallback nameCb = new NameCallback("Enter user: ");
      final PasswordCallback passCb = new PasswordCallback(
        "Enter user password: ",
        false);
      this.getCredentials(nameCb, passCb, false);

      if (nameCb.getName() == null && this.tryFirstPass) {
        this.getCredentials(nameCb, passCb, true);
      }

      final String loginName = nameCb.getName();
      if (loginName != null && this.setLdapPrincipal) {
        this.principals.add(new LdapPrincipal(loginName));
        this.loginSuccess = true;
      }

      final String loginDn = auth.resolveDn(nameCb.getName());
      if (loginDn == null && this.noResultsIsError) {
        this.loginSuccess = false;
        throw new LoginException("Could not find DN for " + nameCb.getName());
      }
      if (loginDn != null && this.setLdapDnPrincipal) {
        this.principals.add(new LdapDnPrincipal(loginDn));
        this.loginSuccess = true;
      }
      if (this.defaultRole != null && !this.defaultRole.isEmpty()) {
        this.roles.addAll(this.defaultRole);
        this.loginSuccess = true;
      }
      this.storeCredentials(nameCb, passCb, loginDn);
    } catch (LdapException e) {
      this.logger.debug("Error occured attempting DN lookup", e);
      this.loginSuccess = false;
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
