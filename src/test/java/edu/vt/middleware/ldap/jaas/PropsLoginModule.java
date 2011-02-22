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
import edu.vt.middleware.ldap.LdapConnection;
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
  private LdapConnection conn;

  /** Search request to load properties for. */
  private SearchRequest sr;

  /** Authenticator to load propeties for. */
  private Authenticator auth;


  /** {@inheritDoc} */
  public void initialize(
    final Subject subject,
    final CallbackHandler callbackHandler,
    final Map<String, ?> sharedState,
    final Map<String, ?> options)
  {
    super.initialize(subject, callbackHandler, sharedState, options);
    this.conn = createLdapConnection(options);
    this.sr = createSearchRequest(options);
    this.auth = createAuthenticator(options);
  }


  /** {@inheritDoc} */
  public boolean login()
    throws LoginException
  {
    return true;
  }


  /** {@inheritDoc} */
  public boolean commit()
    throws LoginException
  {
    this.subject.getPublicCredentials().add(this.conn);
    this.subject.getPublicCredentials().add(this.sr);
    this.subject.getPublicCredentials().add(this.auth);
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
