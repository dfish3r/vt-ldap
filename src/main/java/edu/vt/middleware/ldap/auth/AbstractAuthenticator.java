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
package edu.vt.middleware.ldap.auth;

import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for authenticator implementations.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public abstract class AbstractAuthenticator
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** For finding user DNs. */
  private DnResolver dnResolver;

  /** Handler to process authentication. */
  private AuthenticationHandler authenticationHandler;

  /** For finding user entries. */
  private EntryResolver entryResolver;

  /** Handlers to process authentication results. */
  private AuthenticationResultHandler[] authenticationResultHandlers;


  /**
   * Returns the DN resolver.
   *
   * @return  DN resolver
   */
  public DnResolver getDnResolver()
  {
    return dnResolver;
  }


  /**
   * Sets the DN resolver.
   *
   * @param  resolver  for finding DNs
   */
  public void setDnResolver(final DnResolver resolver)
  {
    dnResolver = resolver;
  }


  /**
   * Returns the authentication handler.
   *
   * @return  authentication handler
   */
  public AuthenticationHandler getAuthenticationHandler()
  {
    return authenticationHandler;
  }


  /**
   * Sets the authentication handler.
   *
   * @param  handler  for performing authentication
   */
  public void setAuthenticationHandler(final AuthenticationHandler handler)
  {
    authenticationHandler = handler;
  }


  /**
   * Returns the entry resolver.
   *
   * @return  entry resolver
   */
  public EntryResolver getEntryResolver()
  {
    return entryResolver;
  }


  /**
   * Sets the entry resolver.
   *
   * @param  resolver  for finding entries
   */
  public void setEntryResolver(final EntryResolver resolver)
  {
    entryResolver = resolver;
  }


  /**
   * Returns the authentication result handlers.
   *
   * @return  authentication result handlers
   */
  public AuthenticationResultHandler[] getAuthenticationResultHandlers()
  {
    return authenticationResultHandlers;
  }


  /**
   * Sets the authentication result handlers.
   *
   * @param  handlers  authentication result handlers
   */
  public void setAuthenticationResultHandlers(
    final AuthenticationResultHandler[] handlers)
  {
    authenticationResultHandlers = handlers;
  }


  /**
   * This will attempt to find the DN for the supplied user. {@link
   * DnResolver#resolve(String)} is invoked to perform this operation.
   *
   * @param  user  to find DN for
   *
   * @return  user DN
   *
   * @throws  LdapException  if an LDAP error occurs during resolution
   */
  public String resolveDn(final String user)
    throws LdapException
  {
    return dnResolver.resolve(user);
  }


  /**
   * Attempts to find the ldap entry for the supplied DN. If an entry resolver
   * has been provided it is used, otherwise a {@link SearchEntryResolver} is
   * used if return attributes have been requested.
   *
   * @param  request  authentication request
   * @param  conn  that authentication occurred on
   * @param  criteria  needed by the entry resolver
   * @return  ldap entry
   * @throws LdapException  if an error occurs resolving the entry
   */
  protected LdapEntry resolveEntry(
    final AuthenticationRequest request,
    final Connection conn,
    final AuthenticationCriteria criteria)
    throws LdapException
  {
    LdapEntry entry = null;
    if (entryResolver != null) {
      entry = entryResolver.resolve(conn, criteria);
    } else {
      EntryResolver er = null;
      if (request.getReturnAttributes() == null ||
          request.getReturnAttributes().length > 0) {
        er = new SearchEntryResolver(request.getReturnAttributes());
      } else {
        er = new NoopEntryResolver();
      }
      entry = er.resolve(conn, criteria);
    }
    return entry;
  }
}
