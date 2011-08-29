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

import java.util.Arrays;
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;

/**
 * Looks up a user's DN using an LDAP search and keeps the connection open.
 *
 * @author  Middleware Services
 * @version  $Revision: 1634 $ $Date: 2010-09-29 16:03:09 -0400 (Wed, 29 Sep 2010) $
 */
public class PersistentSearchDnResolver extends SearchDnResolver
  implements ManagedDnResolver
{

  /** serial version uid. */
  private static final long serialVersionUID = -7275676180831565373L;

  /** Connection. */
  protected Connection connection;


  /** Default constructor. */
  public PersistentSearchDnResolver() {}


  /**
   * Creates a new persistent search dn resolver.
   *
   * @param  cc  connection config
   */
  public PersistentSearchDnResolver(final ConnectionConfig cc)
  {
    setConnectionConfig(cc);
  }


  /** {@inheritDoc} */
  @Override
  public void initialize()
    throws LdapException
  {
    connection = new Connection();
    connection.setConnectionConfig(config);
    connection.open();
  }


  /** {@inheritDoc} */
  @Override
  protected LdapResult performLdapSearch(final SearchFilter filter)
    throws LdapException
  {
    final SearchRequest request = createSearchRequest(filter);
    final SearchOperation op = new SearchOperation(connection);
    return op.execute(request).getResult();
  }


  /** {@inheritDoc} */
  @Override
  public void close()
  {
    connection.close();
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::baseDn=%s, userFilter=%s, userFilterArgs=%s, " +
        "allowMultipleDns=%s, subtreeSearch=%s, config=%s, connection=%s]",
        getClass().getName(),
        hashCode(),
        baseDn,
        userFilter,
        userFilterArgs != null ? Arrays.asList(userFilterArgs) : null,
        allowMultipleDns,
        subtreeSearch,
        config,
        connection);
  }
}
