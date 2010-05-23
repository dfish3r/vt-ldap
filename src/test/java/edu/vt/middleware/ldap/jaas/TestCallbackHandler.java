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
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * Class that implements a callback handler to help with jaas testing.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class TestCallbackHandler implements CallbackHandler
{

  /** test name. */
  private String name;

  /** test password. */
  private String password;


  /** @param  s  to set name with */
  public void setName(final String s)
  {
    this.name = s;
  }


  /** @param  s  to set password with */
  public void setPassword(final String s)
  {
    this.password = s;
  }


  /**
   * @param  callbacks  to handle
   *
   * @throws  IOException  if an input or output error occurs
   * @throws  UnsupportedCallbackException  if a supplied callback cannot be
   * handled
   */
  public void handle(final Callback[] callbacks)
    throws IOException, UnsupportedCallbackException
  {
    for (int i = 0; i < callbacks.length; i++) {
      if (callbacks[i] instanceof NameCallback) {
        final NameCallback nc = (NameCallback) callbacks[i];
        nc.setName(name);
      } else if (callbacks[i] instanceof PasswordCallback) {
        final PasswordCallback pc = (PasswordCallback) callbacks[i];
        if (password != null) {
          pc.setPassword(password.toCharArray());
        }
      } else {
        throw new UnsupportedCallbackException(callbacks[i], "Unsupported");
      }
    }
  }
}
