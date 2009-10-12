/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.ldif;

import java.io.IOException;
import java.io.Writer;
import javax.naming.NamingException;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapSearch;
import edu.vt.middleware.ldap.pool.LdapPool;

/**
 * <code>LdifSearch</code> queries an LDAP and returns the result as an LDIF.
 * Each instance of <code>LdifSearch</code> maintains it's own pool of LDAP
 * connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class LdifSearch extends LdapSearch
{
  /** Ldif object. */
  private Ldif ldif = new Ldif();


  /**
   * This creates a new <code>LdifSearch</code> with the supplied pool.
   *
   * @param  pool  <code>LdapPool</code>
   */
  public LdifSearch(final LdapPool<Ldap> pool)
  {
    super(pool);
  }


  /**
   * This will perform a LDAP search with the supplied query and return
   * attributes. The results will be written to the supplied <code>
   * Writer</code>.
   *
   * @param  query  <code>String</code> to search for
   * @param  attrs  <code>String[]</code> to return
   * @param  writer  <code>Writer</code> to write to
   *
   * @throws  NamingException  if an error occurs while searching
   * @throws  IOException  if an error occurs while writing search results
   */
  public void search(
    final String query,
    final String[] attrs,
    final Writer writer)
    throws NamingException, IOException
  {
    this.ldif.outputLdif(this.search(query, attrs), writer);
  }
}
