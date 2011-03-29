/*
  $Id: SearchDnResolver.java 1634 2010-09-29 20:03:09Z dfisher $

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision: 1634 $
  Updated: $Date: 2010-09-29 16:03:09 -0400 (Wed, 29 Sep 2010) $
*/
package edu.vt.middleware.ldap.auth;

import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Looks up a user's DN using an LDAP search and keeps the LDAP connection open.
 *
 * @author  Middleware Services
 * @version  $Revision: 1634 $ $Date: 2010-09-29 16:03:09 -0400 (Wed, 29 Sep 2010) $
 */
public class PersistentSearchDnResolver extends SearchDnResolver
{

  /** serial version uid. */
  private static final long serialVersionUID = -7275676180831565373L;

  /** Ldap connection. */
  protected LdapConnection ldapConnection = new LdapConnection();


  /** Default constructor. */
  public PersistentSearchDnResolver() {}


  /**
   * Creates a new persistent search dn resolver.
   *
   * @param  lcc  ldap connection config
   */
  public PersistentSearchDnResolver(final LdapConnectionConfig lcc)
  {
    this.setLdapConnectionConfig(lcc);
  }


  /**
   * Sets the ldap connection config.
   *
   * @param  lcc  ldap connection config
   */
  public void setLdapConnectionConfig(final LdapConnectionConfig lcc)
  {
    super.setLdapConnectionConfig(lcc);
    this.ldapConnection.setLdapConnectionConfig(this.config);
  }


  /**
   * Opens a persistent connection to the ldap.
   *
   * @throws  LdapException  if an error occurs opening the connection
   */
  public void open()
    throws LdapException
  {
    this.ldapConnection.open();
  }


  /** {@inheritDoc} */
  protected LdapResult performLdapSearch(final SearchFilter filter)
    throws LdapException
  {
    final SearchRequest request = this.createSearchRequest(filter);
    final SearchOperation op = new SearchOperation(this.ldapConnection);
    return op.execute(request).getResult();
  }


  /**
   * Closes the persistent connection to the ldap.
   */
  public void close()
  {
    this.ldapConnection.close();
  }
}
