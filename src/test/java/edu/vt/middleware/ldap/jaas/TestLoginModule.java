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
 * Login module for testing.
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
  protected Map sharedState;

  /** Whether authentication was successful. */
  protected boolean success;


  /** {@inheritDoc} */
  @Override
  public void initialize(
    final Subject s,
    final CallbackHandler ch,
    final Map<String, ?> ss,
    final Map<String, ?> options)
  {
    subject = s;
    callbackHandler = ch;
    sharedState = ss;
  }


  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public boolean login()
    throws LoginException
  {
    try {
      final NameCallback nameCb = new NameCallback("Enter user: ");
      final PasswordCallback passCb = new PasswordCallback(
        "Enter user password: ",
        false);
      callbackHandler.handle(new Callback[] {nameCb, passCb});

      sharedState.put(LdapLoginModule.LOGIN_NAME, nameCb.getName());
      sharedState.put(
        LdapLoginModule.LOGIN_PASSWORD,
        passCb.getPassword());
      success = true;
    } catch (IOException e) {
      success = false;
      throw new LoginException(e.toString());
    } catch (UnsupportedCallbackException e) {
      success = false;
      throw new LoginException(e.toString());
    }
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean commit()
    throws LoginException
  {
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean abort()
  {
    success = false;
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean logout()
  {
    return true;
  }
}
