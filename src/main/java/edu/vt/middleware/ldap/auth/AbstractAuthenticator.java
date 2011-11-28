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

  /** NoOp entry resolver. */
  private static final EntryResolver NOOP_RESOLVER = new NoOpEntryResolver();

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** For finding user DNs. */
  private DnResolver dnResolver;

  /** Handler to process authentication. */
  private AuthenticationHandler authenticationHandler;

  /** For finding user entries. */
  private EntryResolver entryResolver;

  /** Handlers to process authentication responses. */
  private AuthenticationResponseHandler[] authenticationResponseHandlers;


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
   * Returns the authentication response handlers.
   *
   * @return  authentication response handlers
   */
  public AuthenticationResponseHandler[] getAuthenticationResponseHandlers()
  {
    return authenticationResponseHandlers;
  }


  /**
   * Sets the authentication response handlers.
   *
   * @param  handlers  authentication response handlers
   */
  public void setAuthenticationResponseHandlers(
    final AuthenticationResponseHandler[] handlers)
  {
    authenticationResponseHandlers = handlers;
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
   * Attempts to find the ldap entry for the supplied DN. If the supplied
   * connection is null, a {@link NoOpEntryResolver} is used. If an entry
   * resolver has been provided it is used, otherwise a
   * {@link SearchEntryResolver} is used if return attributes have been
   * requested.
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
    if (conn == null) {
      entry = NOOP_RESOLVER.resolve(conn, criteria);
    } else if (entryResolver != null) {
      entry = entryResolver.resolve(conn, criteria);
    } else {
      if (request.getReturnAttributes() == null ||
          request.getReturnAttributes().length > 0) {
        final EntryResolver er = new SearchEntryResolver(
          request.getReturnAttributes());
        entry = er.resolve(conn, criteria);
      } else {
        entry = NOOP_RESOLVER.resolve(conn, criteria);
      }
    }
    return entry;
  }
}
