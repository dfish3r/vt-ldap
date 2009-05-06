/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import com.sun.security.auth.callback.TextCallbackHandler;
import edu.vt.middleware.ldap.Authenticator;
import edu.vt.middleware.ldap.AuthenticatorConfig;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.bean.LdapAttribute;
import edu.vt.middleware.ldap.bean.LdapAttributes;
import edu.vt.middleware.ldap.props.LdapProperties;

/**
 * <code>LdapLoginModule</code> provides a JAAS authentication hook into LDAP
 * authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapLoginModule implements LoginModule
{

  /** Initialized subject. */
  private Subject subject;

  /** Initialized callback handler. */
  private CallbackHandler callbackHandler;

  /** Initialized sharedState. */
  private Map<String, ?> sharedState;

  /** Initialized options. */
  private Map<String, ?> options;

  /** Whether authentication was successful. */
  private boolean success;

  /** Principals to add to the subject. */
  private Set<Principal> principals;

  /** Credentials to add to the subject. */
  private Set<LdapCredential> credentials;

  /** Authenticator to use against the LDAP. */
  private Authenticator auth;

  /** Ldap to use for searching roles against the LDAP. */
  private Ldap ldap;

  /** User attribute to add to role data. */
  private String[] userRoleAttribute;

  /** Ldap filter for role searches. */
  private String roleFilter;

  /** Role attribute to add to role data. */
  private String[] roleAttribute;


  /**
   * This initializes this <code>LoginMethod</code>.
   *
   * @param  subject  <code>Subject</code>
   * @param  callbackHandler  <code>CallbackHandler</code>
   * @param  sharedState  <code>Map</code>
   * @param  options  <code>Map</code>
   */
  public void initialize(
    final Subject subject,
    final CallbackHandler callbackHandler,
    final Map<String, ?> sharedState,
    final Map<String, ?> options)
  {
    this.subject = subject;
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
    this.options = options;

    this.principals = new TreeSet<Principal>();
    this.credentials = new HashSet<LdapCredential>();

    final Iterator<String> i = options.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      final String value = (String) options.get(key);
      if (key.equalsIgnoreCase("userRoleAttribute")) {
        this.userRoleAttribute = value.split(",");
      } else if (key.startsWith("role")) {
        if (key.equalsIgnoreCase("roleAttribute")) {
          this.roleAttribute = value.split(",");
        } else if (key.equalsIgnoreCase("roleFilter")) {
          this.roleFilter = value;
        }
      }
    }
    this.auth = this.createAuthenticator(options);
    this.ldap = this.createLdap(options);
  }


  /**
   * This submits a <code>NameCallback</code> and <code>PasswordCallback</code>
   * to the initalized <code>CallbackHandler</code>. The name and password are
   * then retrieved from the callbacks and submitted to the authenticator.
   *
   * @return  <code>boolean</code> whether authentication attempt succeeded
   *
   * @throws  LoginException  if a callback handler has not been initialized or
   * authentication fails
   */
  public boolean login()
    throws LoginException
  {
    try {
      final NameCallback nameCb = new NameCallback("Enter user: ");
      final PasswordCallback passCb = new PasswordCallback(
        "Enter user password: ",
        false);
      if (this.callbackHandler != null) {
        this.callbackHandler.handle(new Callback[] {nameCb, passCb});
      }

      final List<LdapRole> roles = new ArrayList<LdapRole>();
      if (this.userRoleAttribute != null) {
        try {
          final Attributes attrs = this.auth.authenticate(
            nameCb.getName(),
            passCb.getPassword(),
            this.userRoleAttribute);
          roles.addAll(this.attributesToRoles(attrs));
          this.success = true;
        } catch (AuthenticationException e) {
          this.success = false;
        }
      } else {
        this.success = this.auth.authenticate(
          nameCb.getName(),
          passCb.getPassword());
      }
      if (!this.success) {
        throw new LoginException("Authentication failed.");
      } else {
        if (this.roleAttribute != null && this.roleFilter != null) {
          try {
            final Object[] filterArgs = new Object[] {
              this.auth.getDn(nameCb.getName()),
              nameCb.getName(),
            };
            final Iterator<SearchResult> results = this.ldap.search(
              this.roleFilter,
              filterArgs,
              this.roleAttribute);
            while (results.hasNext()) {
              final SearchResult sr = results.next();
              roles.addAll(this.attributesToRoles(sr.getAttributes()));
            }
          } catch (NamingException e) {
            this.success = false;
            throw new LoginException(e.getMessage());
          }
        }
        this.principals.add(new LdapPrincipal(nameCb.getName()));
        if (!roles.isEmpty()) {
          final Iterator<LdapRole> i = roles.iterator();
          while (i.hasNext()) {
            this.principals.add(i.next());
          }
        }
        this.credentials.add(new LdapCredential(passCb.getPassword()));
      }
    } catch (IOException e) {
      throw new LoginException(e.toString());
    } catch (UnsupportedCallbackException e) {
      throw new LoginException(e.toString());
    } catch (NamingException e) {
      throw new LoginException(e.toString());
    } finally {
      this.auth.close();
      this.ldap.close();
    }
    return true;
  }


  /**
   * This adds the user and credential to the initialized <code>Subject</code>.
   * Internal credential data is always cleared before this method returns.
   *
   * @return  <code>boolean</code>
   *
   * @throws  LoginException  if the initialized subject is read-only
   */
  public boolean commit()
    throws LoginException
  {
    if (this.success) {
      if (this.subject.isReadOnly()) {
        throw new LoginException("Subject is read-only.");
      }
      this.subject.getPrincipals().addAll(this.principals);
      this.subject.getPrivateCredentials().addAll(this.credentials);
    }
    this.principals.clear();
    this.credentials.clear();
    return true;
  }


  /**
   * This aborts the authentication process and calls {@link #logout()}.
   *
   * @return  <code>boolean</code>
   */
  public boolean abort()
  {
    this.success = false;
    logout();
    return true;
  }


  /**
   * This removes any principals and credentials that were added to the
   * initialized <code>Subject</code> as part of the commit method.
   *
   * @return  <code>boolean</code>
   */
  public boolean logout()
  {
    this.principals.clear();
    this.credentials.clear();

    final Iterator<LdapPrincipal> prinIter = this.subject.getPrincipals(
      LdapPrincipal.class).iterator();
    while (prinIter.hasNext()) {
      final LdapPrincipal p = prinIter.next();
      this.subject.getPrincipals().remove(p);
    }

    final Iterator<LdapRole> roleIter = this.subject.getPrincipals(
      LdapRole.class).iterator();
    while (roleIter.hasNext()) {
      final LdapRole r = roleIter.next();
      this.subject.getPrincipals().remove(r);
    }

    final Iterator<LdapCredential> credIter = this.subject
        .getPrivateCredentials(LdapCredential.class).iterator();
    while (credIter.hasNext()) {
      final LdapCredential c = credIter.next();
      this.subject.getPrivateCredentials().remove(c);
    }

    return true;
  }


  /**
   * This provides command line access to a <code>LdapLoginModule</code>.
   *
   * @param  args  <code>String[]</code>
   *
   * @throws  Exception  if an error occurs
   */
  public static void main(final String[] args)
    throws Exception
  {
    final LoginContext lc = new LoginContext(
      "vt-ldap",
      new TextCallbackHandler());
    lc.login();
    System.out.println("Authentication/Authorization succeeded");

    final Set<Principal> principals = lc.getSubject().getPrincipals();
    System.out.println("Subject Principal(s): ");

    final Iterator<Principal> i = principals.iterator();
    while (i.hasNext()) {
      final Principal p = i.next();
      System.out.println("  " + p.getName());
    }
    lc.logout();
  }


  /**
   * This constructs a new <code>Ldap</code> with the supplied jaas options.
   *
   * @param  options  <code>Map</code>
   *
   * @return  <code>Ldap</code>
   */
  public static Ldap createLdap(final Map<String, ?> options)
  {
    final LdapConfig ldapConfig = new LdapConfig();
    final LdapProperties ldapProperties = new LdapProperties(ldapConfig);
    final Iterator<String> i = options.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      final String value = (String) options.get(key);
      if (key.startsWith("role")) {
        if (
          !key.equalsIgnoreCase("roleAttribute") &&
            !key.equalsIgnoreCase("roleFilter")) {
          final String propName = key.substring("role".length(), key.length());
          final StringBuffer newKey = new StringBuffer().append(
            Character.toLowerCase(propName.charAt(0))).append(
              propName.substring(1));
          ldapProperties.setProperty(newKey.toString(), value);
        }
      } else {
        if (!ldapProperties.isPropertySet(key)) {
          ldapProperties.setProperty(key, value);
        }
      }
    }
    ldapProperties.configure();
    return new Ldap(ldapConfig);
  }


  /**
   * This constructs a new <code>Authenticator</code> with the supplied jaas
   * options.
   *
   * @param  options  <code>Map</code>
   *
   * @return  <code>Authenticator</code>
   */
  public static Authenticator createAuthenticator(final Map<String, ?> options)
  {
    final AuthenticatorConfig authConfig = new AuthenticatorConfig();
    final LdapProperties authProperties = new LdapProperties(authConfig);
    final Iterator<String> i = options.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      final String value = (String) options.get(key);
      if (
        !key.equalsIgnoreCase("userRoleAttribute") &&
          !key.startsWith("role")) {
        authProperties.setProperty(key, value);
      }
    }
    authProperties.configure();
    return new Authenticator(authConfig);
  }


  /**
   * This parses the supplied attributes and returns them as a list of <code>
   * LdapRole</code>s.
   *
   * @param  attributes  <code>Attributes</code>
   *
   * @return  <code>List</code>
   *
   * @throws  NamingException  if the attributes cannot be parsed
   */
  private List<LdapRole> attributesToRoles(final Attributes attributes)
    throws NamingException
  {
    final List<LdapRole> roles = new ArrayList<LdapRole>();
    final LdapAttributes ldapAttrs = new LdapAttributes(attributes);
    for (LdapAttribute ldapAttr : ldapAttrs.getAttributes()) {
      for (String attrValue : ldapAttr.getStringValues()) {
        roles.add(new LdapRole(attrValue));
      }
    }
    return roles;
  }
}
