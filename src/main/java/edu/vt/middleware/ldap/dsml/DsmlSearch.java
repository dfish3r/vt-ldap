/*
  $Id$

  Copyright (C) 2003-2009 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.dsml;

import java.io.IOException;
import java.io.Writer;
import javax.naming.NamingException;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapSearch;
import edu.vt.middleware.ldap.pool.LdapPool;

/**
 * <code>DsmlSearch</code> queries an LDAP and returns the result as DSML. Each
 * instance of <code>DsmlSearch</code> maintains it's own pool of LDAP
 * connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */

public class DsmlSearch extends LdapSearch
{

  /** Valid DSML versions. */
  public enum Version {

    /** DSML version 1. */
    ONE,

    /** DSML version 2. */
    TWO
  }

  /** Version of DSML to produce, default is 1. */
  private Version version = Version.ONE;

  /** Dsml version 1 object. */
  private Dsmlv1 dsmlv1 = new Dsmlv1();

  /** Dsml version 2 object. */
  private Dsmlv2 dsmlv2 = new Dsmlv2();


  /**
   * This creates a new <code>DsmlSearch</code> with the supplied pool.
   *
   * @param  pool  <code>LdapPool</code>
   */
  public DsmlSearch(final LdapPool<Ldap> pool)
  {
    super(pool);
  }


  /**
   * This gets the version of dsml to produce.
   *
   * @return  <code>Version</code> of DSML to produce
   */
  public Version getVersion()
  {
    return this.version;
  }


  /**
   * This sets the version of dsml to produce.
   *
   * @param  v  <code>Version</code> of DSML to produce
   */
  public void setVersion(final Version v)
  {
    this.version = v;
  }


  /**
   * This will perform an LDAP search with the supplied query and return
   * attributes. The results will be written to the supplied <code>
   * Writer</code>. Use {@link #version} to control which version of DSML is
   * written.
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
    if (this.version == Version.TWO) {
      this.dsmlv2.outputDsml(this.search(query, attrs), writer);
    } else {
      this.dsmlv1.outputDsml(this.search(query, attrs), writer);
    }
  }
}
