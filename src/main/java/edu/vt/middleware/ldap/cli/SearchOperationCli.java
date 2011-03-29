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
package edu.vt.middleware.ldap.cli;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Map;
import edu.vt.middleware.ldap.LdapConnection;
import edu.vt.middleware.ldap.LdapConnectionConfig;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.dsml.Dsmlv1Writer;
import edu.vt.middleware.ldap.ldif.LdifWriter;
import edu.vt.middleware.ldap.props.LdapConnectionConfigPropertySource;
import edu.vt.middleware.ldap.props.PropertySource.PropertyDomain;
import edu.vt.middleware.ldap.props.SearchRequestPropertySource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Command line interface for {@link SearchOperation}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class SearchOperationCli extends AbstractCli
{

  /** option for dsmlv1 output. */
  private static final String OPT_DSMLV1 = "dsmlv1";

  /** name of operation provided by this class. */
  private static final String COMMAND_NAME = "ldapsearch";


  /**
   * CLI entry point method.
   *
   * @param  args  command line arguments.
   */
  public static void main(final String[] args)
  {
    new SearchOperationCli().performAction(args);
  }


  /** {@inheritDoc} */
  protected void initOptions()
  {
    this.options.addOption(
      new Option(OPT_DSMLV1, false, "output results in DSML v1"));
    final Map<String, String> desc = this.getArgDesc(
      LdapConnectionConfig.class, SearchRequest.class);
    for (String s : LdapConnectionConfigPropertySource.getProperties()) {
      this.options.addOption(new Option(s, true, desc.get(s)));
    }
    for (String s : SearchRequestPropertySource.getProperties()) {
      this.options.addOption(new Option(s, true, desc.get(s)));
    }
    super.initOptions();
  }


  /**
   * Initialize a search request with command line options.
   *
   * @param  line  parsed command line arguments
   *
   * @return  search request that has been initialized
   *
   * @throws  Exception  if a search request cannot be created
   */
  protected SearchRequest initSearchRequest(final CommandLine line)
    throws Exception
  {
    final SearchRequestPropertySource reader = new SearchRequestPropertySource(
      this.getPropertiesFromOptions(PropertyDomain.LDAP.value(), line));
    return reader.get();
  }


  /** {@inheritDoc} */
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_DSMLV1)) {
      this.outputDsmlv1 = true;
    }
    if (line.hasOption(OPT_HELP)) {
      this.printHelp();
    } else {
      this.search(
        this.initLdapConnectionConfig(line),
        this.initSearchRequest(line));
    }
  }


  /**
   * Executes the ldap search operation.
   *
   * @param  lcc  ldap connection configuration
   * @param  sr  search request
   *
   * @throws  Exception  on any LDAP search error
   */
  protected void search(final LdapConnectionConfig lcc, final SearchRequest sr)
    throws Exception
  {
    final LdapConnection conn = new LdapConnection(lcc);
    conn.open();

    final SearchOperation op = new SearchOperation(conn);
    final LdapResult result = op.execute(sr).getResult();
    if (this.outputDsmlv1) {
      final Dsmlv1Writer writer = new Dsmlv1Writer(
        new BufferedWriter(new OutputStreamWriter(System.out)));
      writer.write(result);
    } else {
      final LdifWriter writer = new LdifWriter(
        new BufferedWriter(new OutputStreamWriter(System.out)));
      writer.write(result);
    }
    conn.close();
  }


  /** {@inheritDoc} */
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }
}
