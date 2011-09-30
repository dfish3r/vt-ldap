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
import edu.vt.middleware.ldap.Connection;
import edu.vt.middleware.ldap.ConnectionConfig;
import edu.vt.middleware.ldap.ConnectionFactory;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.SearchOperation;
import edu.vt.middleware.ldap.SearchRequest;
import edu.vt.middleware.ldap.io.Dsmlv1Writer;
import edu.vt.middleware.ldap.io.LdapResultWriter;
import edu.vt.middleware.ldap.io.LdifWriter;
import edu.vt.middleware.ldap.props.ConnectionConfigPropertySource;
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
  @Override
  protected void initOptions()
  {
    options.addOption(
      new Option(OPT_DSMLV1, false, "output results in DSML v1"));
    final Map<String, String> desc = getArgDesc(
      ConnectionConfig.class, SearchRequest.class);
    for (String s : ConnectionConfigPropertySource.getProperties()) {
      options.addOption(new Option(s, true, desc.get(s)));
    }
    for (String s : SearchRequestPropertySource.getProperties()) {
      options.addOption(new Option(s, true, desc.get(s)));
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
    final SearchRequest request = new SearchRequest();
    final SearchRequestPropertySource srSource =
      new SearchRequestPropertySource(
        request, getPropertiesFromOptions(PropertyDomain.LDAP.value(), line));
    srSource.initialize();
    return request;
  }


  /** {@inheritDoc} */
  @Override
  protected void dispatch(final CommandLine line)
    throws Exception
  {
    if (line.hasOption(OPT_DSMLV1)) {
      outputDsmlv1 = true;
    }
    if (line.hasOption(OPT_HELP)) {
      printHelp();
    } else {
      search(
        initConnectionFactory(line),
        initSearchRequest(line));
    }
  }


  /**
   * Executes the ldap search operation.
   *
   * @param  cf  connection factory
   * @param  request  search request
   *
   * @throws  Exception  on any LDAP search error
   */
  protected void search(final ConnectionFactory cf, final SearchRequest request)
    throws Exception
  {
    final Connection conn = cf.getConnection();
    conn.open();

    final SearchOperation op = new SearchOperation(conn);
    final LdapResult result = op.execute(request).getResult();
    LdapResultWriter writer = null;
    if (outputDsmlv1) {
      writer = new Dsmlv1Writer(
        new BufferedWriter(new OutputStreamWriter(System.out)));
    } else {
      writer = new LdifWriter(
        new BufferedWriter(new OutputStreamWriter(System.out)));
    }
    writer.write(result);
    conn.close();
  }


  /** {@inheritDoc} */
  @Override
  protected String getCommandName()
  {
    return COMMAND_NAME;
  }
}
