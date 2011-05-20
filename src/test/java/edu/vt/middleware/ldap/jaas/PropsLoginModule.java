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

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.auth.Authenticator;

/**
 * Login module for testing configuration properties.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class PropsLoginModule extends AbstractLoginModule
{

  /** Ldap connection to load propertie for. */
  private Connection conn;

  /** Search request to load properties for. */
  private SearchRequest sr;

  /** Authenticator to load propeties for. */
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
    conn = createConnection(options);
    sr = createSearchRequest(options);
    auth = createAuthenticator(options);
  }


  /** {@inheritDoc} */
  @Override
  public boolean login()
    throws LoginException
  {
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean commit()
    throws LoginException
  {
    subject.getPublicCredentials().add(conn);
    subject.getPublicCredentials().add(sr);
    subject.getPublicCredentials().add(auth);
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean abort()
  {
    loginSuccess = false;
    return true;
  }


  /** {@inheritDoc} */
  @Override
  public boolean logout()
  {
    return true;
  }
}
