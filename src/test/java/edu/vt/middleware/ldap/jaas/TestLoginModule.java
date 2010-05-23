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
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * <code>TestLoginModule</code> is a test login module.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class TestLoginModule implements LoginModule
{

  /** Initialized subject. */
  protected Subject subject;

  /** Initialized callback handler. */
  protected CallbackHandler callbackHandler;

  /** Shared state from other login module. */
  @SuppressWarnings("unchecked")
  protected Map sharedState;

  /** Whether authentication was successful. */
  protected boolean success;


  /** {@inheritDoc} */
  public void initialize(
    final Subject subject,
    final CallbackHandler callbackHandler,
    final Map<String, ?> sharedState,
    final Map<String, ?> options)
  {
    this.subject = subject;
    this.callbackHandler = callbackHandler;
    this.sharedState = sharedState;
  }


  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public boolean login()
    throws LoginException
  {
    try {
      final NameCallback nameCb = new NameCallback("Enter user: ");
      final PasswordCallback passCb = new PasswordCallback(
        "Enter user password: ",
        false);
      this.callbackHandler.handle(new Callback[] {nameCb, passCb});

      this.sharedState.put(LdapLoginModule.LOGIN_NAME, nameCb.getName());
      this.sharedState.put(
        LdapLoginModule.LOGIN_PASSWORD,
        passCb.getPassword());
      this.success = true;
    } catch (IOException e) {
      this.success = false;
      throw new LoginException(e.toString());
    } catch (UnsupportedCallbackException e) {
      this.success = false;
      throw new LoginException(e.toString());
    }
    return true;
  }


  /** {@inheritDoc} */
  public boolean commit()
    throws LoginException
  {
    return true;
  }


  /** {@inheritDoc} */
  public boolean abort()
  {
    this.success = false;
    return true;
  }


  /** {@inheritDoc} */
  public boolean logout()
  {
    return true;
  }
}
