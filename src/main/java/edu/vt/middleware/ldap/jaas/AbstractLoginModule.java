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

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides functionality common to ldap based JAAS login modules.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractLoginModule implements LoginModule
{

  /** Constant for login name stored in shared state. */
  public static final String LOGIN_NAME = "javax.security.auth.login.name";

  /** Constant for entryDn stored in shared state. */
  public static final String LOGIN_DN =
    "edu.vt.middleware.ldap.jaas.login.entryDn";

  /** Constant for login password stored in shared state. */
  public static final String LOGIN_PASSWORD =
    "javax.security.auth.login.password";

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Initialized subject. */
  protected Subject subject;

  /** Initialized callback handler. */
  protected CallbackHandler callbackHandler;

  /** Shared state from other login module. */
  protected Map sharedState;

  /** Whether credentials from the shared state should be used. */
  protected boolean useFirstPass;

  /**
   * Whether credentials from the shared state should be used if they are
   * available.
   */
  protected boolean tryFirstPass;

  /** Whether credentials should be stored in the shared state map. */
  protected boolean storePass;

  /** Whether credentials should be removed from the shared state map. */
  protected boolean clearPass;

  /** Whether ldap principal data should be set. */
  protected boolean setLdapPrincipal;

  /** Whether ldap dn principal data should be set. */
  protected boolean setLdapDnPrincipal;

  /** Whether ldap credential data should be set. */
  protected boolean setLdapCredential;

  /** Default roles. */
  protected List<LdapRole> defaultRole = new ArrayList<LdapRole>();

  /** Name of group to add all principals to. */
  protected String principalGroupName;

  /** Name of group to add all roles to. */
  protected String roleGroupName;

  /** Whether authentication was successful. */
  protected boolean loginSuccess;

  /** Whether commit was successful. */
  protected boolean commitSuccess;

  /** Principals to add to the subject. */
  protected Set<Principal> principals;

  /** Credentials to add to the subject. */
  protected Set<LdapCredential> credentials;

  /** Roles to add to the subject. */
  protected Set<Principal> roles;


  /** {@inheritDoc} */
  @Override
  public void initialize(
    final Subject subj,
    final CallbackHandler handler,
    final Map<String, ?> state,
    final Map<String, ?> options)
  {
    logger.trace("Begin initialize");
    subject = subj;
    callbackHandler = handler;
    sharedState = state;

    final Iterator<String> i = options.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      final String value = (String) options.get(key);
      if ("useFirstPass".equalsIgnoreCase(key)) {
        useFirstPass = Boolean.valueOf(value);
      } else if ("tryFirstPass".equalsIgnoreCase(key)) {
        tryFirstPass = Boolean.valueOf(value);
      } else if ("storePass".equalsIgnoreCase(key)) {
        storePass = Boolean.valueOf(value);
      } else if ("clearPass".equalsIgnoreCase(key)) {
        clearPass = Boolean.valueOf(value);
      } else if ("setLdapPrincipal".equalsIgnoreCase(key)) {
        setLdapPrincipal = Boolean.valueOf(value);
      } else if ("setLdapDnPrincipal".equalsIgnoreCase(key)) {
        setLdapDnPrincipal = Boolean.valueOf(value);
      } else if ("setLdapCredential".equalsIgnoreCase(key)) {
        setLdapCredential = Boolean.valueOf(value);
      } else if ("defaultRole".equalsIgnoreCase(key)) {
        for (String s : value.split(",")) {
          defaultRole.add(new LdapRole(s.trim()));
        }
      } else if ("principalGroupName".equalsIgnoreCase(key)) {
        principalGroupName = value;
      } else if ("roleGroupName".equalsIgnoreCase(key)) {
        roleGroupName = value;
      }
    }

    logger.trace(
      "useFirstPass = {}, tryFirstPass = {}, storePass = {}, clearPass = {}, " +
      "setLdapPrincipal = {}, setLdapDnPrincipal = {}, " +
      "setLdapCredential = {}, defaultRole = {}, principalGroupName = {}, " +
      "roleGroupName = {}",
      new Object[] {
        Boolean.toString(useFirstPass),
        Boolean.toString(tryFirstPass),
        Boolean.toString(storePass),
        Boolean.toString(clearPass),
        Boolean.toString(setLdapPrincipal),
        Boolean.toString(setLdapDnPrincipal),
        Boolean.toString(setLdapCredential),
        defaultRole,
        principalGroupName,
        roleGroupName, });

    principals = new TreeSet<Principal>();
    credentials = new HashSet<LdapCredential>();
    roles = new TreeSet<Principal>();
  }


  /** {@inheritDoc} */
  @Override
  public boolean commit()
    throws LoginException
  {
    logger.trace("Begin commit");
    if (!loginSuccess) {
      logger.debug("Login failed");
      return false;
    }

    if (subject.isReadOnly()) {
      clearState();
      throw new LoginException("Subject is read-only.");
    }
    subject.getPrincipals().addAll(principals);
    logger.debug(
      "Committed the following principals: {}", principals);
    subject.getPrivateCredentials().addAll(credentials);
    subject.getPrincipals().addAll(roles);
    logger.debug("Committed the following roles: {}", roles);
    if (principalGroupName != null) {
      final LdapGroup group = new LdapGroup(principalGroupName);
      for (Principal principal : principals) {
        group.addMember(principal);
      }
      subject.getPrincipals().add(group);
      logger.debug("Committed the following principal group: {}", group);
    }
    if (roleGroupName != null) {
      final LdapGroup group = new LdapGroup(roleGroupName);
      for (Principal role : roles) {
        group.addMember(role);
      }
      subject.getPrincipals().add(group);
      logger.debug("Committed the following role group: {}", group);
    }
    clearState();
    commitSuccess = true;
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean abort()
    throws LoginException
  {
    logger.trace("Begin abort");
    if (!loginSuccess) {
      return false;
    } else if (loginSuccess && !commitSuccess) {
      loginSuccess = false;
      clearState();
    } else {
      logout();
    }
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean logout()
    throws LoginException
  {
    logger.trace("Begin logout");
    if (subject.isReadOnly()) {
      clearState();
      throw new LoginException("Subject is read-only.");
    }

    final Iterator<LdapPrincipal> prinIter = subject.getPrincipals(
      LdapPrincipal.class).iterator();
    while (prinIter.hasNext()) {
      subject.getPrincipals().remove(prinIter.next());
    }

    final Iterator<LdapDnPrincipal> dnPrinIter = subject.getPrincipals(
      LdapDnPrincipal.class).iterator();
    while (dnPrinIter.hasNext()) {
      subject.getPrincipals().remove(dnPrinIter.next());
    }

    final Iterator<LdapRole> roleIter = subject.getPrincipals(
      LdapRole.class).iterator();
    while (roleIter.hasNext()) {
      subject.getPrincipals().remove(roleIter.next());
    }

    final Iterator<LdapGroup> groupIter = subject.getPrincipals(
      LdapGroup.class).iterator();
    while (groupIter.hasNext()) {
      subject.getPrincipals().remove(groupIter.next());
    }

    final Iterator<LdapCredential> credIter = subject
        .getPrivateCredentials(LdapCredential.class).iterator();
    while (credIter.hasNext()) {
      subject.getPrivateCredentials().remove(credIter.next());
    }

    clearState();
    loginSuccess = false;
    commitSuccess = false;
    return true;
  }


  /**
   * Removes any stateful principals, credentials, or roles stored by login.
   * Also removes shared state name, dn, and password if clearPass is set.
   */
  protected void clearState()
  {
    principals.clear();
    credentials.clear();
    roles.clear();
    if (clearPass) {
      sharedState.remove(LOGIN_NAME);
      sharedState.remove(LOGIN_PASSWORD);
      sharedState.remove(LOGIN_DN);
    }
  }


  /**
   * Attempts to retrieve credentials for the supplied name and password
   * callbacks. If useFirstPass or tryFirstPass is set, then name and password
   * data is retrieved from shared state. Otherwise a callback handler is used
   * to get the data. Set useCallback to force a callback handler to be used.
   *
   * @param  nameCb  to set name for
   * @param  passCb  to set password for
   * @param  useCallback  whether to force a callback handler
   *
   * @throws  LoginException  if the callback handler fails
   */
  protected void getCredentials(
    final NameCallback nameCb,
    final PasswordCallback passCb,
    final boolean useCallback)
    throws LoginException
  {
    logger.trace(
      "Begin getCredentials: useFistPass = {}, tryFistPass = {}, " +
      "useCallback = {}, callbackhandler class = {}, " +
      "name callback class = {}, password callback class = {}",
      new Object[] {
        Boolean.toString(useFirstPass),
        Boolean.toString(tryFirstPass),
        Boolean.toString(useCallback),
        callbackHandler.getClass().getName(),
        nameCb.getClass().getName(),
        passCb.getClass().getName(), });
    try {
      if ((useFirstPass || tryFirstPass) && !useCallback) {
        nameCb.setName((String) sharedState.get(LOGIN_NAME));
        passCb.setPassword((char[]) sharedState.get(LOGIN_PASSWORD));
      } else if (callbackHandler != null) {
        callbackHandler.handle(new Callback[] {nameCb, passCb});
      } else {
        throw new LoginException(
          "No CallbackHandler available. " +
          "Set useFirstPass, tryFirstPass, or provide a CallbackHandler");
      }
    } catch (IOException e) {
      logger.error("Error reading data from callback handler", e);
      loginSuccess = false;
      throw new LoginException(e.getMessage());
    } catch (UnsupportedCallbackException e) {
      logger.error("Unsupported callback", e);
      loginSuccess = false;
      throw new LoginException(e.getMessage());
    }
  }


  /**
   * Stores the supplied name, password, and entry dn in the stored state map.
   * storePass must be set for this method to have any affect.
   *
   * @param  nameCb  to store
   * @param  passCb  to store
   * @param  loginDn  to store
   */
  @SuppressWarnings("unchecked")
  protected void storeCredentials(
    final NameCallback nameCb,
    final PasswordCallback passCb,
    final String loginDn)
  {
    if (storePass) {
      if (nameCb != null && nameCb.getName() != null) {
        sharedState.put(LOGIN_NAME, nameCb.getName());
      }
      if (passCb != null && passCb.getPassword() != null) {
        sharedState.put(LOGIN_PASSWORD, passCb.getPassword());
      }
      if (loginDn != null) {
        sharedState.put(LOGIN_DN, loginDn);
      }
    }
  }
}
